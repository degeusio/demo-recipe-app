package io.degeus.recipeappapi.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

@Entity
@javax.persistence.Table(name = "user_role", schema = "iam", uniqueConstraints = {@UniqueConstraint(columnNames = {"roleName"})})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserRole implements GrantedAuthority {

    @Id
    private Long id;

    private String roleName;

    @Override
    public String getAuthority() {
        return getRoleName();
    }
}
