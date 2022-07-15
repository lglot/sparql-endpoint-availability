package it.unife.sparql_endpoint_availability.model.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;
//
//    @Column(name = "password", nullable = false)
//    private String password;
//
//    //role
//    @Column(name = "role", nullable = false)
//    private String role;
//
//    @Column(name = "enabled", nullable = false)
//    private boolean enabled;
//
//    @Column(name = "email", nullable = false)
//    private String email;
}