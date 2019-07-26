package com.registry.repository.usage;

import com.registry.repository.user.User;
import com.registry.util.SecurityUtil;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

	/** 생성자 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "performer")
	protected User performer;

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
	@Column(name = "build_id", columnDefinition="varchar(255)")
	private String buildId;

	/** namespace */
	@Column(name = "namespace", columnDefinition="varchar(255)")
	private String namespace;

	/** image */
	@Column(name = "image", columnDefinition="varchar(255)")
	private String image;

	/** role */
	@Column(name = "role", columnDefinition="varchar(255)")
	private String role;

	/** team */
	@Column(name = "team", columnDefinition="varchar(255)")
	private String team;

	/** member */
	@Column(name = "member", columnDefinition="varchar(255)")
	private String member;

	/** tag */
	@Column(name = "tag", columnDefinition="varchar(255)")
	private String tag;

	/** image */
	@Column(name = "docker_image_id", columnDefinition="varchar(255)")
	private String dockerImageId;

	/** original_image */
	@Column(name = "original_docker_image", columnDefinition="varchar(255)")
	private String originalDockerImageId;

	/** visibility */
	@Column(name = "visibility", columnDefinition="varchar(255)")
	private String visibility;

	/** description */
	@Column(name = "description", columnDefinition="varchar(255)")
	private String description;

	/** expiration_date */
	@Column(name = "expiration_date", columnDefinition="varchar(255)")
	private String expirationDate;

	/** old_expiration_date */
	@Column(name = "old_expiration_date", columnDefinition="varchar(255)")
	private String oldExpirationDate;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Constructor
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public Log() { }

	public Log(String kind) {
		this.setIp(SecurityUtil.getIP());
		this.setKind(kind);
		this.setDatetime(LocalDateTime.now());
	}

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

	public String getDatetime() {

		if( null != this.datetime ) {

			DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			return this.datetime.format(fm);
		}
		else {

			return null;
		}
	}

	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}

	public User getPerformer() {
		return performer;
	}

	public void setPerformer(User performer) {
		this.performer = performer;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public String getDockerImageId() {
		return dockerImageId;
	}

	public void setDockerImageId(String dockerImageId) {
		this.dockerImageId = dockerImageId;
	}

	public String getOriginalDockerImageId() {
		return originalDockerImageId;
	}

	public void setOriginalDockerImageId(String originalDockerImageId) {
		this.originalDockerImageId = originalDockerImageId;
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
