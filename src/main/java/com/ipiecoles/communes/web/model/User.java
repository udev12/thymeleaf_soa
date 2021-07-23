package com.ipiecoles.communes.web.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.Set;

/**
 * Classe qui représente l'entité "User"
 */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank // ne doit pas être vide
    @Length(max = 50) // 50 caractères maximum
    private String name;


    @NotBlank // ne doit pas être vide
    @Length(max = 50) // 50 caractères maximum
    private String lastName;

    @Email
    @NotBlank // ne doit pas être vide
    private String email;

    @NotBlank // ne doit pas être vide
    @Column(nullable = false, unique = true)
    @Length(min = 5, max = 50) // entre 5 et 50 caractères
    private String userName;

    @NotBlank // ne doit pas être vide
    @Length(min = 8) // 8 caractères maximum
    private String password;

    private Boolean active;

    // première façon de faire : évite erreur au moment du login
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    /**
     * Constructeur par défaut
     */
    public User() {
    }

    /**
     * Constructeur surchargé avec tous les attributs
     *
     * @param id       : identifiant
     * @param name     : prénom de l'utilisateur
     * @param lastName : nom de l'utilisateur
     * @param email    : adresse électronique de l'utilisateur
     * @param userName : nom d'utilisateur
     * @param password : mot de passe de l'utilisateur
     * @param active   : si utilisateur actif ou pas
     * @param roles    : rôle de l'utilisateur
     */
    public User(Integer id, String name, String lastName, String email, String userName, String password, Boolean active, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.active = active;
        this.roles = roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * On reféniti la méthode "equals"
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(userName, user.userName) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(name, user.name) && Objects.equals(lastName, user.lastName) && Objects.equals(active, user.active) && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, email, password, name, lastName, active, roles);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", email='").append(email).append('\'');
        //On ne log pas les mots de passe...
        //sb.append(", password='").append(password).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", active=").append(active);
        sb.append(", roles=").append(roles);
        sb.append('}');
        return sb.toString();
    }

}


