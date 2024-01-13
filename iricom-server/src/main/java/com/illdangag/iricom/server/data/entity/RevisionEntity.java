package com.illdangag.iricom.server.data.entity;

import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@org.hibernate.envers.RevisionEntity
public class RevisionEntity implements Serializable {
    @Id
    @GeneratedValue
    @RevisionNumber
    private Long rev;

    @RevisionTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
}
