package com.userbot.test.repository;

import com.userbot.test.entity.TST_City;
import com.userbot.test.entity.TST_Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TST_CityRepository extends JpaRepository<TST_City, Long> {

    Optional<TST_City> getByRegionIdAndName(Long regionId, String name);

    Page<TST_City> findByRegionId(Long regionId, Pageable pageable);

}
