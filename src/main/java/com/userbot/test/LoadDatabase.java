package com.userbot.test;

import com.userbot.test.entity.TST_City;
import com.userbot.test.entity.TST_Country;
import com.userbot.test.entity.TST_Region;
import com.userbot.test.repository.TST_CityRepository;
import com.userbot.test.repository.TST_CountryRepository;
import com.userbot.test.repository.TST_RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(TST_CityRepository cityRepository,
                                   TST_RegionRepository regionRepository,
                                   TST_CountryRepository countryRepository) {
        return args -> {
            log.info("Preloading " + countryRepository.saveAndFlush(new TST_Country("Italy", "My country")));
            TST_Region region = regionRepository.saveAndFlush(new TST_Region("Sicilia", "Italy", "My region"));
            log.info("Preloading " + region);
            log.info("Preloading " + cityRepository.saveAndFlush(new TST_City("Palermo", region.getId(), "My city")));
        };
    }

}
