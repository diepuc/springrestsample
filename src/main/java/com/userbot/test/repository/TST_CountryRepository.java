package com.userbot.test.repository;

import com.userbot.test.entity.TST_Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TST_CountryRepository extends JpaRepository<TST_Country, String> {

}
