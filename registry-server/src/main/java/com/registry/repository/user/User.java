package com.registry.repository.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.registry.repository.AbstractEntity;
import com.registry.repository.organization.Organization;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author boozer
 */
@Entity
@Table(name = "users")
@org.hibernate.annotations.DynamicUpdate
public class User extends AbstractEntity {
	
	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Private Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/	
	
	private static final long serialVersionUID = 1L;
	
	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| private Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/** 로그인 ID */
	@Id
	@Column(name = "username", columnDefinition="varchar(40)", nullable=false)
	private String username;

	/** 사용여부 */
  	@Column(name = "enabled", nullable=false)
  	private Boolean enabled;

	/** 비밀번호 */
	@JsonIgnore
	@Column(name = "password", columnDefinition="varchar(255)", nullable=false)
	private String password;

	/** 이름 */
  	@Column(name = "name", columnDefinition="varchar(40)", nullable=false)
  	private String name;
  	
	/** 이메일 */
  	@Column(name = "email", columnDefinition="varchar(255)", nullable=false)
  	private String email;

	/** 삭제여부 */
	@Column(name = "del_yn", nullable=false)
	private Boolean delYn;

	/** super user */
	@Column(name = "super_user", nullable=false)
	private Boolean superuser;

	/** minio enabled */
	@Column(name = "minio_enabled", columnDefinition = "boolean default false", nullable=false)
	private Boolean minioEnabled;

	/** minio host */
	@Column(name = "minio_host", columnDefinition="varchar(255)")
	private String minioHost;

	/** minio port */
	@Column(name = "minio_port")
	private Long minioPort;

	/** 유저 Role 목록 */
	@JsonIgnore
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Role> role = new ArrayList<Role>();

	/** 유저 Starred 목록 */
	@JsonIgnore
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<Role> starreds = new ArrayList<Role>();

	// 권한목록
	@JsonProperty
	@Transient
	private List<Role> roles;

	/** UserOrg 목록 */
	@JsonIgnore
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<UserOrganization> userOrg = new ArrayList<UserOrganization>();

	// org 목록
	@JsonProperty
	@Transient
	private List<Organization> organizations;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/	

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Constructor
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public User() { }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Getter & Setter Method ( DI Method )
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getDelYn() {
		return delYn;
	}

	public void setDelYn(Boolean delYn) {
		this.delYn = delYn;
	}

	public Boolean getSuperuser() {
		return superuser;
	}

	public void setSuperuser(Boolean superuser) {
		this.superuser = superuser;
	}

	public Boolean getMinioEnabled() {
		return minioEnabled;
	}

	public void setMinioEnabled(Boolean minioEnabled) {
		this.minioEnabled = minioEnabled;
	}

	public String getMinioHost() {
		return minioHost;
	}

	public void setMinioHost(String minioHost) {
		this.minioHost = minioHost;
	}

	public Long getMinioPort() {
		return minioPort;
	}

	public void setMinioPort(Long minioPort) {
		this.minioPort = minioPort;
	}

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<UserOrganization> getUserOrg() {
		return userOrg;
	}

	public void setUserOrg(List<UserOrganization> userOrg) {
		this.userOrg = userOrg;
	}

	public List<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
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
