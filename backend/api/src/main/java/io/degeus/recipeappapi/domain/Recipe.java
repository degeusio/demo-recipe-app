package io.degeus.recipeappapi.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recipe", schema = "rcp", uniqueConstraints = {@UniqueConstraint(columnNames = {"title"})})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
@ToString
public class Recipe {

    @Id
    @org.hibernate.annotations.Type(type = "pg-uuid") // uuid works with PostgreSQL version 8.4-701 above and Hibernate version 4.3.x above, see https://medium.com/@swhp/work-with-uuid-in-jpa-and-postgresql-86a59ea989cd
    @Column(nullable = false, updatable = false, unique = true)
    private UUID id;

    @Size(min = 3, max = 64)
    @Column(nullable = false, unique = true)
    private String title;

    @NotNull
    @Column(nullable = false)
    private Boolean vegetarian;

    @NotNull
    @Column(nullable = false)
    private Integer numberOfPersons;

    @NotNull
    @Size(min = 1, max = 25) // lower and upper limit for amount of ingredients
    @ElementCollection //simple multi-valued mapping for now, @OneToMany is too complex and not applicable as ingredients are non-existent without the embedding recipe entity
    @CollectionTable(
            schema = "rcp",
            name = "recipes_ingredients",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @Column(name = "ingredient")
    private List<String> ingredients;

    /** stored in UTC */
    @CreatedDate
    private Instant createdTimestamp;

    @LastModifiedDate
    private Instant lastModifiedTimestamp;
}
