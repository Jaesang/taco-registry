package com.registry.repository.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.registry.repository.AbstractEntity;
import com.registry.repository.user.Role;
import com.registry.util.SecurityUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author boozer
 */
@Entity
@Table(name = "image")
@org.hibernate.annotations.DynamicUpdate
public class Image extends AbstractEntity {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Private Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	private static final long serialVersionUID = 1L;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| private Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/** 유저 ID (AutoIncrement) */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 이름 */
  	@Column(name = "name", columnDefinition="varchar(40)", nullable=false)
  	private String name;

	/** 네임스페이스 */
	@Column(name = "namespace", columnDefinition="varchar(40)")
	private String namespace;

	/** 공개여부 */
	@Column(name = "is_public", nullable=false)
	private boolean isPublic;

	/** 설명 */
	@Column(name = "description", columnDefinition="varchar(255)")
	private String description;

	/** 인기 */
	@Column(name = "popularity")
	private Long popularity;

	/** 네임스페이스 종류 */
	@Column(name = "is_organization", nullable=false)
	private boolean isOrganization;

	/** 유저 Role 목록 */
	@JsonIgnore
	@OneToMany(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Role> role = new ArrayList<Role>();

	/** build 목록 */
	@JsonIgnore
	@OneToMany(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Build> builds = new ArrayList<Build>();

	/** tag 목록 */
	@JsonIgnore
	@OneToMany(mappedBy = "image", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Tag> tags = new ArrayList<Tag>();

	@JsonProperty
	@Transient
	private Boolean canAdmin;

	@JsonProperty
	@Transient
	private Boolean canWrite;

	@JsonProperty
	@Transient
	private Boolean isStarred;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Constructor
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public Image() { }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Getter & Setter Method ( DI Method )
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(boolean aPublic) {
		isPublic = aPublic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPopularity() {
		return popularity;
	}

	public void setPopularity(Long popularity) {
		this.popularity = popularity;
	}

	public boolean getIsOrganization() {
		return isOrganization;
	}

	public void setIsOrganization(boolean organization) {
		isOrganization = organization;
	}

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	public List<Build> getBuilds() {
		return builds;
	}

	public void setBuilds(List<Build> builds) {
		this.builds = builds;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public Boolean getCanAdmin() {
		this.canAdmin = false;
		if (role != null && role.size() > 0) {
			role.stream().forEach(value -> {
				if (value.getUser().getUsername().equals(SecurityUtil.getUser())) {
					if ("ADMIN".equals(value.getName())) {
						this.canAdmin = true;
					}
				}
			});
		}
		return canAdmin;
	}

	public Boolean getCanWrite() {
		this.canWrite = false;
		if (role != null && role.size() > 0) {
			role.stream().forEach(value -> {
				if (value.getUser().getUsername().equals(SecurityUtil.getUser())) {
					if ("ADMIN".equals(value.getName()) || "WRITE".equals(value.getName())) {
						this.canWrite = true;
					}
				}
			});
		}

		return canWrite;
	}

	public Boolean getIsStarred() {
		this.isStarred = false;
		if (role != null && role.size() > 0) {
			role.stream().forEach(value -> {
				this.isStarred = value.getIsStarred();
			});
		}

		return isStarred;
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
