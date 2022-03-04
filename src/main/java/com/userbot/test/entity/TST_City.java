package com.userbot.test.entity;

import com.userbot.test.entity.util.CheckEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="TST_City", uniqueConstraints={@UniqueConstraint(columnNames={"Name", "RegionId"}, name = "UIDX_Name_RegionId")})
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@ToString(callSuper = false, onlyExplicitlyIncluded = true)
public class TST_City extends CheckEntity implements Serializable {

	@Override
	@PrePersist@PreUpdate
	protected void checkStrings() throws RollbackException {
		super.checkStrings(this);
	}

	@Id
	@EqualsAndHashCode.Include
	@ToString.Include
	@GeneratedValue(strategy=GenerationType.AUTO, generator="TST_City_sequence")
	@SequenceGenerator(name = "TST_City_sequence", sequenceName="TST_City_sequence")
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
	@Column(name="RegionId", nullable=false)
	protected Long regionId;

	@NonNull
	@Column(name="Description", length = 1024)
	protected String description;

	@ManyToOne
	@JoinColumn(name="RegionId", insertable = false, updatable = false)
	protected TST_Region regionEntity;

	public void setRegionEntity(TST_Region regionEntity) {
		regionId = regionEntity != null ? regionEntity.getId() : null;
		this.regionEntity = regionEntity;
	}

	public TST_Region getRegionEntity() {
		return regionEntity;
	}
}
