package com.userbot.test.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
public class DTORegion extends DTOInfoDetail {

    @EqualsAndHashCode.Include
    protected Long id;

    protected DTOInfoDetail country;

}
