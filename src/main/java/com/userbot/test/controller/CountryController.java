package com.userbot.test.controller;

import com.userbot.test.dto.DTOInfoDetail;
import com.userbot.test.entity.TST_Country;
import com.userbot.test.exception.BadRequestException;
import com.userbot.test.exception.ResourceNotFoundException;
import com.userbot.test.repository.TST_CountryRepository;
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
public class CountryController {

    @Autowired
    private TST_CountryRepository countryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Operation(summary = "Aggiunge un nuovo paese")
    @Transactional
    @PostMapping("/countries")
    public ResponseEntity<DTOInfoDetail> addNew(@Valid @RequestBody DTOInfoDetail countryDetail) throws BadRequestException {

        if (countryDetail == null)
            throw new BadRequestException("Input request not found");

        if (countryRepository.findById(countryDetail.getName()).isPresent())
            throw new BadRequestException("Resounce exist");

        TST_Country country = modelMapper.map(countryDetail, TST_Country.class);
        countryRepository.save(country);

        return new ResponseEntity<>(countryDetail, HttpStatus.CREATED);
    }

    @Operation(summary = "Modifica un paese")
    @Transactional
    @PutMapping("/countries/{name}")
    public DTOInfoDetail modify(@PathVariable(value = "name") String name,
                                              @Valid @RequestBody DTOInfoDetail newCountry) throws ResourceNotFoundException, BadRequestException {
        if (newCountry.getName() != null && !newCountry.getName().equals(name))
            throw new BadRequestException("Can't modify primary key");

        TST_Country country2 = countryRepository.findById(name)
                .map(country -> {
                    country.setName(newCountry.getName());
                    country.setDescription(newCountry.getDescription());
                    return countryRepository.save(country);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Country " + name));

        return modelMapper.map(country2, DTOInfoDetail.class);
    }

    @Operation(summary = "Restituisce tutti i paesi")
    @GetMapping(path = "/countries")
    public List<DTOInfoDetail> all(@RequestParam("page") int page,
                                 @RequestParam("size") int size,
                                 @RequestParam(value = "sort", required=false) String sort,
                                 @RequestParam(value = "name.dir", required=false) String direction) throws ResourceNotFoundException {
        if (sort == null || sort.length() == 0)
            sort = "name";

        if (direction == null || direction.length() == 0)
            direction = Sort.Direction.ASC.name();

        Page<TST_Country> pageResult = countryRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.valueOf(direction.toUpperCase()), sort)));

        if (pageResult != null)
            return pageResult.getContent()
                    .stream()
                    .map(o -> modelMapper.map(o, DTOInfoDetail.class))
                    .collect(Collectors.toList());

        throw new ResourceNotFoundException("Countries");
    }

    @Operation(summary = "Restituisce un paese")
    @GetMapping(path = "/countries/{name}")
    public DTOInfoDetail one(@PathVariable(value = "name") String name) throws ResourceNotFoundException {
        return modelMapper.map(countryRepository.findById(name)
                .orElseThrow(() -> new ResourceNotFoundException("Country " + name)), DTOInfoDetail.class);
    }

    @Operation(summary = "Cancella un paese")
    @Transactional
    @DeleteMapping("/countries/{name}")
    public ResponseEntity<HttpStatus> delete(@PathVariable(value = "name") String name)
            throws ResourceNotFoundException {
        TST_Country country = countryRepository.findById(name)
                .orElseThrow(() -> new ResourceNotFoundException("Country " + name));

        countryRepository.delete(country);
        return new ResponseEntity<HttpStatus>(HttpStatus.ACCEPTED);
    }
}
