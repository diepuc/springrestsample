package com.userbot.test.entity;

import com.userbot.test.entity.util.CheckEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@ToString(callSuper = false, onlyExplicitlyIncluded = true)
public class TST_Country extends CheckEntity implements Serializable {

	@Override
	@PrePersist@PreUpdate
	protected void checkStrings() throws RollbackException {
		super.checkStrings(this);
	}

	@Id
	@NonNull
	@EqualsAndHashCode.Include
	@ToString.Include
	@Column(name="Name", length = 64, nullable=false)
	protected String name;

	@NonNull
	@Column(name="Description", length = 1024)
	protected String description;

	@OneToMany(mappedBy="countryEntity", cascade={CascadeType.REMOVE})
	protected List<TST_Region> regions;


}
