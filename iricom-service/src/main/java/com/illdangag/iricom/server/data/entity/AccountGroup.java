package com.illdangag.iricom.server.data.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(indexes = {
        @Index(name = "AccountGroup_enabled", columnList = "enabled"),
        @Index(name = "AccountGroup_deleted", columnList = "deleted")
})
public class AccountGroup {
    @Id
    @GeneratedValue
    private Long id;

    @Builder.Default
    @CreationTimestamp
    private LocalDateTime createDate = LocalDateTime.now();

    @Builder.Default
    @UpdateTimestamp
    private LocalDateTime updateDate = LocalDateTime.now();

    @Builder.Default
    private String title = "";

    @Builder.Default
    private String description = "";

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean deleted = false;

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AccountGroup)) {
            return false;
        }

        if (this.id == null) {
            return super.equals(object);
        }

        AccountGroup other = (AccountGroup) object;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        if (this.id == null) {
            return super.hashCode();
        } else {
            return Objects.hashCode(this.id);
        }
    }
}
