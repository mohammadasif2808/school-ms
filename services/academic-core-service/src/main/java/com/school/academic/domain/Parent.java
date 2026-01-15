package com.school.academic.domain;

import jakarta.persistence.*;

/**
 * Parent (Guardian) entity - Represents legal guardians.
 */
@Entity
@Table(name = "parent", schema = "academic_core")
public class Parent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 64)
    private String userId; // Optional link to identity-service

    @Column(name = "first_name", nullable = false, length = 128)
    private String firstName;

    @Column(name = "last_name", length = 128)
    private String lastName;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "relationship", length = 64)
    private String relationship; // Mother, Father, Guardian

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    // Default constructor
    public Parent() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Helper method to get full name.
     */
    public String getFullName() {
        return firstName + (lastName != null ? " " + lastName : "");
    }
}
