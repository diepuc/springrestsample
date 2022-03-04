package com.userbot.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
public class DTOInfoDetail implements Serializable {

    @NotBlank
    @EqualsAndHashCode.Include
    @Length(max = 64)
    protected String name;

    @Length(max = 1024)
    protected String description;

}
