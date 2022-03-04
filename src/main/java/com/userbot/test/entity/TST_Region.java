package com.userbot.test.entity;

import com.userbot.test.entity.util.CheckEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="TST_Region", uniqueConstraints={@UniqueConstraint(columnNames={"Name", "Country"}, name = "UIDX_Name_Country")})
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@ToString(callSuper = false, onlyExplicitlyIncluded = true)
public class TST_Region extends CheckEntity implements Serializable {

	@Override
	@PrePersist@PreUpdate
	protected void checkStrings() throws RollbackException {
		super.checkStrings(this);
	}

	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	@GeneratedValue(strategy=GenerationType.AUTO, generator="TST_Region_sequence")
	@SequenceGenerator(name = "TST_Region_sequence", sequenceName="TST_Region_sequence")
	@Column(name = "Id", nullable=false)
	protected Long id;

	@NonNull
	@EqualsAndHashCode.Include
	@ToString.Include
	@Column(name="Name", length = 64, nullable=false)
	protected String name;

	@NonNull
	@EqualsAndHashCode.Include
	@ToString.Include
	@Column(name="Country", length = 64, nullable=false)
	protected String country;

	@NonNull
	@Column(name="Description", length = 1024)
	protected String description;

	@ManyToOne
	@JoinColumn(name="Country", insertable = false, updatable = false)
	protected TST_Country countryEntity;

	@OneToMany(mappedBy="regionEntity", cascade={CascadeType.REMOVE})
	protected List<TST_City> cities;

	public void setCountryEntity(TST_Country countryEntity) {
		country = countryEntity != null ? countryEntity.getName() : null;
		this.countryEntity = countryEntity;
	}


}
