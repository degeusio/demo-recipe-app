package io.degeus.recipeappapi.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "user", schema = "iam", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements Serializable, UserDetails {

    public static final int MAX_LENGTH_PASSWORD = 255; //matches db

    public static final int COLUMN_ID_MAX_LENGTH = 36;
    public static final int COLUMN_USERNAME_MAX_LENGTH = 64;
    public static final int COLUMN_EMAIL_MAX_LENGTH = 64;
    public static final int COLUMN_PASSWORDENCRYPTIONMETHOD_MAX_LENGTH = 10;

    //for now, users are added as static data, so we don't need sequence generator set-up now to wire Hibernate with the sequence, e.g. using
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user_generator")
    //@SequenceGenerator(name = "seq_user_generator", schema = "iam", sequenceName = "seq_user",
    //            allocationSize = 1)
    @Id
    private Long id;

    @Column(length = COLUMN_USERNAME_MAX_LENGTH)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "pw_enc_meth",nullable = false, length = COLUMN_PASSWORDENCRYPTIONMETHOD_MAX_LENGTH)
    String passwordEncryptionMethod;

    /** this needed for quite intrusive requirements from the User object from the framework */
    @Column(nullable = false)
    Boolean accountExpired;

    @Column(nullable = false)
    Boolean accountLocked;

    @Column(nullable = false)
    Boolean accountDisabled;

    @Column(nullable = false)
    Boolean credentialsExpired;
    /** /this needed for quite intrusive requirements from the User object from the framework */

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", schema = "iam",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_role_id", referencedColumnName = "id"))
    @OrderBy
    private Collection<UserRole> authorities = new ArrayList<>();

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return !accountDisabled;
    }

}
