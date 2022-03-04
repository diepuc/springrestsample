package com.userbot.test.controller;

import com.userbot.test.dto.DTOInfoDetail;
import com.userbot.test.dto.DTORegion;
import com.userbot.test.entity.TST_Country;
import com.userbot.test.entity.TST_Region;
import com.userbot.test.exception.BadRequestException;
import com.userbot.test.exception.ResourceNotFoundException;
import com.userbot.test.repository.TST_CountryRepository;
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
public class RegionController {

    @Autowired
    private TST_CountryRepository countryRepository;

    @Autowired
    private TST_RegionRepository regionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Operation(summary = "Aggiunge una nuova regione")
    @Transactional
    @PostMapping("/regions")
    public ResponseEntity<DTORegion> addNew(@RequestParam(value = "countryName") String countryName,
                                            @Valid @RequestBody DTOInfoDetail regionDetail) throws BadRequestException, ResourceNotFoundException {
        if (regionDetail == null)
            throw new BadRequestException("Input request not found");

        if (regionRepository.getByCountryAndName(countryName, regionDetail.getName()).isPresent())
            throw new BadRequestException("Resource exist");

        TST_Country country = countryRepository.findById(countryName)
                .orElseThrow(() -> new ResourceNotFoundException("Country " + countryName));

        TST_Region region = modelMapper.map(regionDetail, TST_Region.class);
        region.setCountryEntity(country);
        region = regionRepository.save(region);

        return new ResponseEntity<>(modelMapper.map(region, DTORegion.class), HttpStatus.CREATED);
    }

    @Operation(summary = "Modifica una regione di un paese")
    @Transactional
    @PutMapping("/regions/{id}")
    public DTORegion modify(@PathVariable(value = "id") Long id,
                                @RequestParam(value = "countryName") String countryName,
                                @Valid @RequestBody DTOInfoDetail newRegion) throws ResourceNotFoundException, BadRequestException {
        TST_Region region = regionRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Region " + id));

        TST_Country country = countryRepository.findById(countryName)
                .orElseThrow(() -> new ResourceNotFoundException("Country " + countryName));

        //Cotrollo integrità
        TST_Region region2 = regionRepository.getByCountryAndName(countryName, newRegion.getName()).get();

        if (region2 != null && region2.getId().longValue() != id.longValue())
            throw new BadRequestException("Resource already present");

        region.setCountryEntity(country);
        region.setName(newRegion.getName());
        region.setDescription(newRegion.getDescription());

        region = regionRepository.save(region);

        return modelMapper.map(region, DTORegion.class);
    }

    @Operation(summary = "Restituisce tutte le regioni di un paese")
    @GetMapping(path = "/regions")
    public List<DTORegion> all(@RequestParam(value = "countryName") String countryName,
                                   @RequestParam("page") int page,
                                 @RequestParam("size") int size,
                                 @RequestParam(value = "sort", required=false) String sort,
                                 @RequestParam(value = "name.dir", required=false) String direction) throws ResourceNotFoundException {
        if (sort == null || sort.length() == 0)
            sort = "name";

        if (direction == null || direction.length() == 0)
            direction = Sort.Direction.ASC.name();

        Page<TST_Region> pageResult = regionRepository.findByCountry(countryName,
                PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), sort)));

        if (pageResult != null)
            return pageResult.getContent()
                    .stream()
                    .map(o -> modelMapper.map(o, DTORegion.class))
                    .collect(Collectors.toList());

        throw new ResourceNotFoundException("Regions");
    }

    @Operation(summary = "Restituisce una regione dal suo id")
    @GetMapping(path = "/regions/{id}")
    public DTORegion one(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        return modelMapper.map(regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region " + id)), DTORegion.class);
    }

    @Operation(summary = "Restituisce una regione dal suo nome e paese di appartenenza")
    @GetMapping(path = "/regions/{name}/{countryName}")
    public DTORegion one(@PathVariable(value = "name") String name,
                         @PathVariable(value = "countryName") String countryName) throws ResourceNotFoundException {
        return modelMapper.map(regionRepository.getByCountryAndName(countryName, name)
                .orElseThrow(() -> new ResourceNotFoundException("Region " + name)), DTORegion.class);
    }

    @Operation(summary = "Cancella una regione dato il suo id")
    @Transactional
    @DeleteMapping("/regions/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable(value = "id") Long id)
            throws ResourceNotFoundException {
        TST_Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region " + id));

        regionRepository.delete(region);
        return new ResponseEntity<HttpStatus>(HttpStatus.ACCEPTED);
    }
}
