package com.registry.repository.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.registry.repository.AbstractEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author boozer
 */
@Entity
@Table(name = "build")
@org.hibernate.annotations.DynamicUpdate
public class Build extends AbstractEntity {

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
  	@Column(name = "display_name", columnDefinition="varchar(40)")
  	private String displayName;

	/** 네임스페이스 */
	@Column(name = "status", columnDefinition="varchar(40)")
	private String status;

	/** 에러 */
	@Column(name = "error", columnDefinition="varchar(40)")
	private String error;

	/** sub directory */
	@Column(name = "subdirectory", columnDefinition="varchar(40)")
	private String subdirectory;

	/** archive url */
	@Column(name = "archive_url", columnDefinition="varchar(40)")
	private String archiveUrl;

	/** trigger */
	@Column(name = "build_trigger", columnDefinition="varchar(40)")
	private String buildTrigger;

	/** trigger metadata */
	@Column(name = "trigger_metadata", columnDefinition="varchar(40)")
	private String triggerMetadata;

	/** context */
	@Column(name = "context", columnDefinition="varchar(40)")
	private String context;

	/**
	 * phase
	 * complete, error, cancelled, waiting, pulling, pushing, building
	 */
	@Column(name = "phase", columnDefinition="varchar(40)")
	private String phase;

	/** resource key */
	@Column(name = "resource_key", columnDefinition="varchar(40)")
	private String resourceKey;

	/** dockerfile path */
	@Column(name = "dockerfile_path", columnDefinition="varchar(40)")
	private String dockerfilePath;

	/** dockerfile */
	@Column(name = "dockerfile", columnDefinition="text")
	private String dockerfile;

	/** git url */
	@Column(name = "git_path", columnDefinition="varchar(255)")
	private String gitPath;

	/** git username */
	@Column(name = "git_username", columnDefinition="varchar(255)")
	private String gitUsername;

	/** git password */
	@Column(name = "git_password", columnDefinition="varchar(255)")
	private String gitPassword;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="image_id", nullable = false)
	private Image image;

	@JsonProperty
	@Transient
	private String started;

	@JsonProperty
	@Transient
	private String manualUser;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Constructor
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public Build() { }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Getter & Setter Method ( DI Method )
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getSubdirectory() {
		return subdirectory;
	}

	public void setSubdirectory(String subdirectory) {
		this.subdirectory = subdirectory;
	}

	public String getArchiveUrl() {
		return archiveUrl;
	}

	public void setArchiveUrl(String archiveUrl) {
		this.archiveUrl = archiveUrl;
	}

	public String getBuildTrigger() {
		return buildTrigger;
	}

	public void setBuildTrigger(String buildTrigger) {
		this.buildTrigger = buildTrigger;
	}

	public String getTriggerMetadata() {
		return triggerMetadata;
	}

	public void setTriggerMetadata(String triggerMetadata) {
		this.triggerMetadata = triggerMetadata;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getResourceKey() {
		return resourceKey;
	}

	public void setResourceKey(String resourceKey) {
		this.resourceKey = resourceKey;
	}

	public String getDockerfilePath() {
		return dockerfilePath;
	}

	public void setDockerfilePath(String dockerfilePath) {
		this.dockerfilePath = dockerfilePath;
	}

	public String getDockerfile() {
		return dockerfile;
	}

	public void setDockerfile(String dockerfile) {
		this.dockerfile = dockerfile;
	}

	public String getGitPath() {
		return gitPath;
	}

	public void setGitPath(String gitPath) {
		this.gitPath = gitPath;
	}

	public String getGitUsername() {
		return gitUsername;
	}

	public void setGitUsername(String gitUsername) {
		this.gitUsername = gitUsername;
	}

	public String getGitPassword() {
		return gitPassword;
	}

	public void setGitPassword(String gitPassword) {
		this.gitPassword = gitPassword;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getStarted() {
		if( null != createdDate ) {

			DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			return createdDate.format(fm);
		}
		else {

			return null;
		}
	}

	public String getManualUser() {
		return createdBy.getUsername();
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
