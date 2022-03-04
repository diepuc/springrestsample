package com.userbot.test.controller;

import com.userbot.test.dto.DTOCity;
import com.userbot.test.dto.DTOInfoDetail;
import com.userbot.test.entity.TST_City;
import com.userbot.test.entity.TST_Region;
import com.userbot.test.exception.BadRequestException;
import com.userbot.test.exception.ResourceNotFoundException;
import com.userbot.test.repository.TST_CityRepository;
import com.userbot.test.repository.TST_RegionRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class CityController {

    @Autowired
    private TST_CityRepository cityRepository;

    @Autowired
    private TST_RegionRepository regionRepository;

    @Autowired
    private ModelMapper modelMapper;

    private DTOCity getDTOCity(TST_City city) {
        DTOCity res = modelMapper.map(city, DTOCity.class);
        if (city.getRegionEntity() != null && city.getRegionEntity().getCountryEntity() != null)
            res.setCountry(new DTOInfoDetail(city.getRegionEntity().getCountryEntity().getName(), city.getRegionEntity().getCountryEntity().getDescription()));
        return res;
    }

    @Operation(summary = "Aggiunge una nuova città ad una regione")
    @Transactional
    @PostMapping("/cities")
    public ResponseEntity<DTOCity> addNew(@RequestParam(value = "regionId") Long regionId,
                                          @Valid @RequestBody DTOInfoDetail cityDetail) throws BadRequestException, ResourceNotFoundException {
        if (cityDetail == null)
            throw new BadRequestException("Input request not found");

        TST_Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Region " + regionId));

        if (cityRepository.getByRegionIdAndName(regionId , cityDetail.getName()).isPresent())
            throw new BadRequestException("Resource exist");

        TST_City city = modelMapper.map(cityDetail, TST_City.class);
        city.setRegionEntity(region);
        city = cityRepository.save(city);

        return new ResponseEntity<>(getDTOCity(city), HttpStatus.CREATED);
    }

    @Operation(summary = "Modifica una citta di una regione")
    @Transactional
    @PutMapping("/cities/{id}")
    public DTOCity modify(@PathVariable(value = "id") Long id,
                                @RequestParam(value = "regionId") Long regionId,
                                @Valid @RequestBody DTOInfoDetail newCity) throws ResourceNotFoundException, BadRequestException {
        TST_City city = cityRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("City " + id));

        TST_Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Region " + regionId));

        //Cotrollo integrità
        TST_City city2 = cityRepository.getByRegionIdAndName(regionId , newCity.getName()).get();

        if (city2 != null && city2.getId().longValue() != id.longValue())
            throw new BadRequestException("Resource already present");

        city.setRegionEntity(region);
        city.setName(newCity.getName());
        city.setDescription(newCity.getDescription());

        city = cityRepository.save(city);

        return getDTOCity(city);
    }

    @Operation(summary = "Restituisce tutte le città di una regione")
    @GetMapping(path = "/cities")
    public List<DTOCity> all(@RequestParam(value = "regionId") Long regionId,
                                   @RequestParam("page") int page,
                                 @RequestParam("size") int size,
                                 @RequestParam(value = "sort", required=false) String sort,
                                 @RequestParam(value = "name.dir", required=false) String direction) throws ResourceNotFoundException {
        if (sort == null || sort.length() == 0)
            sort = "name";

        if (direction == null || direction.length() == 0)
            direction = Sort.Direction.ASC.name();

        Page<TST_City> pageResult = cityRepository.findByRegionId(regionId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), sort)));

        if (pageResult != null)
            return pageResult.getContent()
                    .stream()
                    .map(o -> getDTOCity(o))
                    .collect(Collectors.toList());

        throw new ResourceNotFoundException("Cities");
    }

    @Operation(summary = "Restituisce una città dal suo id")
    @GetMapping(path = "/cities/{id}")
    public DTOCity one(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return getDTOCity(cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region " + id)));
    }

    @Operation(summary = "Restituisce una città dal suo nome e regione di appartenenza")
    @GetMapping(path = "/cities/{name}/{regionId}")
    public DTOCity one(@PathVariable(value = "name") String name,
                         @PathVariable(value = "regionId") Long regionId) throws ResourceNotFoundException {
        return getDTOCity(cityRepository.getByRegionIdAndName(regionId, name)
                .orElseThrow(() -> new ResourceNotFoundException("City " + name)));
    }

    @Operation(summary = "Cancella una città dato il suo id")
    @Transactional
    @DeleteMapping("/cities/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable(value = "id") Long id)
            throws ResourceNotFoundException {
        TST_City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City " + id));

        cityRepository.delete(city);
        return new ResponseEntity<HttpStatus>(HttpStatus.ACCEPTED);
    }
}
