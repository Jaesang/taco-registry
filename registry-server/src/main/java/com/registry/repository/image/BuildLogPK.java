package com.registry.repository.image;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by exntu on 14/08/2019.
 */
@Embeddable
public class BuildLogPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="build_id", nullable = false)
    private Build build;

    public BuildLogPK(){}

    public BuildLogPK(UUID id, Build build) {
        super();
        this.id = id;
        this.build = build;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }
}
