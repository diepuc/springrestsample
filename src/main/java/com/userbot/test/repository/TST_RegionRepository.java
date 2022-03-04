package com.userbot.test.repository;

import com.userbot.test.entity.TST_Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface TST_RegionRepository extends JpaRepository<TST_Region, Long> {

    Optional<TST_Region> getByCountryAndName(String country, String name);

    Page<TST_Region> findByCountry(String country, Pageable pageable);

}
