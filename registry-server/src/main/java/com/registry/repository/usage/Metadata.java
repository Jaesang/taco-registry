package com.registry.repository.usage;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author boozer
 */
@Entity
@Table(name = "metadata")
@org.hibernate.annotations.DynamicUpdate
public class Metadata implements Serializable {

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

	/** build id */
	@Column(name = "build_id", columnDefinition="varchar(255)", nullable=false)
	private String buildId;

	/** namespace */
	@Column(name = "namespace", columnDefinition="varchar(255)", nullable=false)
	private String namespace;

	/** image id */
	@Column(name = "image_id", columnDefinition="varchar(255)", nullable=false)
	private String imageId;

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

	/** username */
	@Column(name = "username", columnDefinition="varchar(255)", nullable=false)
	private String username;

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
	private String expiration_date;

	/** old_expiration_date */
	@Column(name = "old_expiration_date", columnDefinition="varchar(255)", nullable=false)
	private String old_expiration_date;

	@OneToOne
	@JoinColumn(name = "log_id")
	private Log log;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Constructor
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public Metadata() { }

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

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getExpiration_date() {
		return expiration_date;
	}

	public void setExpiration_date(String expiration_date) {
		this.expiration_date = expiration_date;
	}

	public String getOld_expiration_date() {
		return old_expiration_date;
	}

	public void setOld_expiration_date(String old_expiration_date) {
		this.old_expiration_date = old_expiration_date;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
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
