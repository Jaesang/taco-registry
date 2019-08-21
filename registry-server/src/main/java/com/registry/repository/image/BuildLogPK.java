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

    /** seq */
    @Column(name = "seq")
    private Long seq;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="build_id", nullable = false)
    private Build build;

    public BuildLogPK(){}

    public BuildLogPK(Long seq, Build build) {
        super();
        this.seq = seq;
        this.build = build;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }
}
