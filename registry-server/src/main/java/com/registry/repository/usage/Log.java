package com.registry.repository.usage;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author boozer
 */
@Entity
@Table(name = "usage_log")
@org.hibernate.annotations.DynamicUpdate
public class Log implements Serializable {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Private Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	private static final long serialVersionUID = 1L;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| private Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/** ID (AutoIncrement) */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** ip */
  	@Column(name = "ip", columnDefinition="varchar(40)", nullable=false)
  	private String ip;

	/** datetime */
	@Column(name = "datetime", nullable=false)
	@Type(type = "org.hibernate.type.LocalDateTimeType")
	private LocalDateTime datetime;

	/** kind */
	@Column(name = "kind", columnDefinition="varchar(40)", nullable=false)
	private String kind;

	/** organization id */
	@Column(name = "organization_id")
	private Long organizationId;

	/** image id */
	@Column(name = "image_id")
	private Long imageId;

	/** username */
	@Column(name = "username", columnDefinition="varchar(40)")
	private String username;

	/** build id */
	@Column(name = "build_id", columnDefinition="varchar(255)", nullable=false)
	private String buildId;

	/** namespace */
	@Column(name = "namespace", columnDefinition="varchar(255)", nullable=false)
	private String namespace;

	/** role */
	@Column(name = "role", columnDefinition="varchar(255)", nullable=false)
	private String role;

	/** team */
	@Column(name = "team", columnDefinition="varchar(255)", nullable=false)
	private String team;

	/** member */
	@Column(name = "member", columnDefinition="varchar(255)", nullable=false)
	private String member;

	/** tag */
	@Column(name = "tag", columnDefinition="varchar(255)", nullable=false)
	private String tag;

	/** image */
	@Column(name = "image", columnDefinition="varchar(255)", nullable=false)
	private String image;

	/** original_image */
	@Column(name = "original_image", columnDefinition="varchar(255)", nullable=false)
	private String originalImage;

	/** visibility */
	@Column(name = "visibility", columnDefinition="varchar(255)", nullable=false)
	private String visibility;

	/** description */
	@Column(name = "description", columnDefinition="varchar(255)", nullable=false)
	private String description;

	/** expiration_date */
	@Column(name = "expiration_date", columnDefinition="varchar(255)", nullable=false)
	private String expirationDate;

	/** old_expiration_date */
	@Column(name = "old_expiration_date", columnDefinition="varchar(255)", nullable=false)
	private String oldExpirationDate;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Constructor
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public Log() { }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Getter & Setter Method ( DI Method )
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public LocalDateTime getDatetime() {
		return datetime;
	}

	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getOriginalImage() {
		return originalImage;
	}

	public void setOriginalImage(String originalImage) {
		this.originalImage = originalImage;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getOldExpirationDate() {
		return oldExpirationDate;
	}

	public void setOldExpirationDate(String oldExpirationDate) {
		this.oldExpirationDate = oldExpirationDate;
	}

/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
	
	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Implement Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Override Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/	

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| private Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Private Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Inner Class
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
  	
}
