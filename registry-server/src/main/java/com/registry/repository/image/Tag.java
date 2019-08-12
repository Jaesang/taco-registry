package com.registry.repository.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author boozer
 */
@Entity
@Table(name = "tag")
@org.hibernate.annotations.DynamicUpdate
public class Tag implements Serializable {

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

    /** image */
    @Column(name = "docker_image_id", columnDefinition="varchar(40)", nullable=false)
    private String dockerImageId;

    /** manifest digest */
    @Column(name = "manifest_digest", columnDefinition="varchar(255)")
    private String manifestDigest;

    /** 이름 */
    @Column(name = "name", columnDefinition="varchar(40)")
    private String name;

    /** size */
    @Column(name = "size")
    private Long size;

    /** expiration */
    @Column(name = "expiration")
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    private LocalDateTime expiration;

    /** start ts */
    @Column(name = "start_time")
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    private LocalDateTime startTime;

    /** end ts */
    @Column(name = "end_time")
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    private LocalDateTime endTime;

    /** last modified ts */
    @Column(name = "last_modified")
    @Type(type = "org.hibernate.type.LocalDateTimeType")
    private LocalDateTime lastModified;

    @JsonProperty
    @Transient
    private Long startTs;

    @JsonProperty
    @Transient
    private Long endTs;

    /** build id */
    @Column(name = "build_id")
    private UUID buildId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="image_id", nullable = false)
	private Image image;

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Constructor
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	public Tag() { }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Getter & Setter Method ( DI Method )
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDockerImageId() {
        return dockerImageId;
    }

    public void setDockerImageId(String dockerImageId) {
        this.dockerImageId = dockerImageId;
    }

    public String getManifestDigest() {
        return manifestDigest;
    }

    public void setManifestDigest(String manifestDigest) {
        this.manifestDigest = manifestDigest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getExpiration() {
        if( null != this.expiration ) {

            DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return this.expiration.format(fm);
        }
        else {

            return null;
        }
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public UUID getBuildId() {
        return buildId;
    }

    public void setBuildId(UUID buildId) {
        this.buildId = buildId;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getLastModified() {
        if( null != this.lastModified ) {

            DateTimeFormatter fm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return this.lastModified.format(fm);
        }
        else {

            return null;
        }
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public Long getStartTs() {
        this.startTs = 0l;
        if (this.startTime != null) {
            this.startTs = Timestamp.valueOf(this.startTime).getTime();
        }
        return this.startTs;
    }

    public Long getEndTs() {
        this.endTs = 0l;
        if (this.endTime != null) {
            this.endTs = Timestamp.valueOf(this.endTime).getTime();
        }
        return this.endTs;
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
