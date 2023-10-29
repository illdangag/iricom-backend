package com.illdangag.iricom.server.data.entity;

import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@org.hibernate.envers.RevisionEntity
public class RevisionEntity implements Serializable {
    @Id
    @GeneratedValue
    @RevisionNumber
    private long rev;

    @RevisionTimestamp
    private long timestamp;
}
