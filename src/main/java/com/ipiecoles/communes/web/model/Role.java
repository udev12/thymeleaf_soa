package com.ipiecoles.communes.web.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 */
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @ManyToMany(mappedBy = "role")
//    @NotNull
//    @Column(name = "idRole")
    private int id;

//    @Column
    private String role;

    public Role() {
    }

    public Role(int id, String role) {
        this.id = id;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role='" + role + '\'' +
                '}';
    }
}
