package com.illdangag.iricom.server.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FirebaseAuthentication {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof FirebaseAuthentication)) {
            return false;
        }

        FirebaseAuthentication other = (FirebaseAuthentication) object;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
