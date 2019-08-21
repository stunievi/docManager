package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author dell
 */
@Entity(name = "user")
public class User {
//    @Column
    @Id
    private Integer id;
    @Column(name = "name")
    private String name;
    private String password;
    public Integer getId () {
        return id;
    }
    public String getName () {
        return name;
    }
    public String getPassword () {
        return password;
    }
    public void setId (Integer id) {
        this.id = id;
    }
    public void setName (String name) {
        this.name = name;
    }
    public void setPassword (String password) {
        this.password = password;
    }

}
