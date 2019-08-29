package com.registry.repository.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.registry.constant.Const;
import com.registry.repository.AbstractEntity;
import com.registry.repository.user.Role;
import com.registry.util.SecurityUtil;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	/** 이름 */
  	@Column(name = "name", columnDefinition="varchar(40)", nullable=false)
  	private String name;

	/** 네임스페이스 */
	@Column(name = "namespace", columnDefinition="varchar(40)")
	private String namespace;

	/** 공개여부 */
	@Column(name = "is_public", nullable=false)
	private boolean isPublic;

	/** 삭제여부 */
	@Column(name = "del_yn", nullable=false)
	private boolean delYn;

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
	private List<Tag> tagHistories = new ArrayList<Tag>();

	@JsonProperty
	@Transient
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

	@JsonProperty
	@Transient
	private String lastModified;

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

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
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

	public boolean getDelYn() {
		return delYn;
	}

	public void setDelYn(boolean delYn) {
		this.delYn = delYn;
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

	public List<Tag> getTagHistories() {
		return tagHistories;
	}

	public void setTagHistories(List<Tag> tags) {
		this.tagHistories = tags;
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
					if (Const.Role.ADMIN.equals(value.getName())) {
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
					if (Const.Role.ADMIN.equals(value.getName()) || Const.Role.WRITE.equals(value.getName())) {
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
				if (SecurityUtil.getUser().equals(value.getUser().getUsername())) {
					this.isStarred = value.getIsStarred();
				}
			});
		}

		return isStarred;
	}

	public String getLastModified() {
		if( null != updatedDate ) {

			DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			return updatedDate.format(fm);
		}
		else {

			return null;
		}
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
