package com.registry.repository;

import com.registry.repository.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author taeho
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Private Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	private static final long serialVersionUID = 1L;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/** 생성자 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by")
	protected User createdBy;

	/** 생성일자 */
	@JsonIgnore
	@Column(name = "created_date")
	@Type(type = "org.hibernate.type.LocalDateTimeType")
	protected LocalDateTime createdDate;

	/** 수정자 */
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by")
	protected User updatedBy;

	/** 수정일자 */
	@JsonIgnore
	@Column(name = "updated_date")
	@Type(type = "org.hibernate.type.LocalDateTimeType")
	protected LocalDateTime updatedDate;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Constructor
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public AbstractEntity() {
		// empty constructor
	}

	public AbstractEntity(User createdBy) {
		this.createdBy = createdBy;
	}

	public AbstractEntity(User createdBy, User updatedBy) {
		this.createdBy 	= createdBy;
		this.updatedBy	= updatedBy;
	}
	
	@PrePersist
	public void prePersist() {
		this.createdDate = LocalDateTime.now();
		this.updatedDate = this.createdDate;
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedDate = LocalDateTime.now();
	}

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Getter & Setter Method ( DI Method )
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	@JsonIgnore
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	/**
	 * Formmating 처리된 DateTime
	 * 하위 Entity에서 사용하려면 아래와 같이 사용하면 됨.
	 * ========================================
	 * @Override
	 * @JsonIgnore(false)
	 * public String getCreatedDateFormmat() {
	 *     return super.getCreatedDateFormmat();
	 * }
	 * ========================================
	 * @return
	 */
	@JsonIgnore
	@JsonProperty("createdDate")
	public String getCreatedDateFormmat() {

		if( null != createdDate ) {

			DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return createdDate.format(fm);
		}
		else {

			return null;
		}
	}

	/**
	 * Formmating 처리된 DateTime
	 * 하위 Entity에서 사용하려면 아래와 같이 사용하면 됨.
	 * ========================================
	 * @Override
	 * @JsonIgnore(false)
	 * public String getCreatedDateFormmat() {
	 *     return super.getCreatedDateFormmat();
	 * }
	 * ========================================
	 * @return
	 */
	@JsonIgnore
	@JsonProperty("createdTimeDate")
	public String getCreatedDateTimeFormmat() {

		if( null != createdDate ) {

			DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			return createdDate.format(fm);
		}
		else {

			return null;
		}
	}

	/**
	 * Formmating 처리된 DateTime
	 * 하위 Entity에서 사용하려면 아래와 같이 사용하면 됨.
	 * ========================================
	 * @Override
	 * @JsonIgnore(false)
	 * public String getUpdatedDateFormmat() {
	 *     return super.getUpdatedDateFormmat();
	 * }
	 * ========================================
	 * @return
	 */
	@JsonIgnore
	@JsonProperty("updatedDate")
	public String getUpdatedDateFormmat() {

		if( null != updatedDate ) {

			DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return updatedDate.format(fm);
		}
		else {

			return null;
		}
	}

	/**
	 * Formmating 처리된 DateTime
	 * 하위 Entity에서 사용하려면 아래와 같이 사용하면 됨.
	 * ========================================
	 * @Override
	 * @JsonIgnore(false)
	 * public String getUpdatedDateFormmat() {
	 *     return super.getUpdatedDateFormmat();
	 * }
	 * ========================================
	 * @return
	 */
	@JsonIgnore
	@JsonProperty("updatedTimeDate")
	public String getUpdatedDateTimeFormmat() {

		if( null != updatedDate ) {

			DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			return updatedDate.format(fm);
		}
		else {

			return null;
		}
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate 	= createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

}
