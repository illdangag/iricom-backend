package com.illdangag.iricom.core.data.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "account_group",
        indexes = {
                @Index(name = "AccountGroup_enabled", columnList = "enabled"),
        }
)
@Audited(withModifiedFlag = true)
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
    @OneToMany(mappedBy = "accountGroup", fetch = FetchType.LAZY)
    private List<AccountGroupAccount> accountGroupAccountList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "accountGroup", fetch = FetchType.LAZY)
    private List<AccountGroupBoard> accountGroupBoardList = new ArrayList<>();

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
