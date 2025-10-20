package com.example.LoginTUgether.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "citizen_id")
    private String citizenId; // รหัสบัตรประชาชน ใช้เป็นรหัสผ่านตามคำสั่งเพื่อน

    private String name;

    public User() {}

    public User(String email, String citizenId, String name) {
        this.email = email;
        this.citizenId = citizenId;
        this.name = name;
    }

    // getters & setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCitizenId() { return citizenId; }
    public void setCitizenId(String citizenId) { this.citizenId = citizenId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
