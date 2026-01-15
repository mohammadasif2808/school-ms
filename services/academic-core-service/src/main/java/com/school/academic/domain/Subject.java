package com.school.academic.domain;

import jakarta.persistence.*;

/**
 * Subject entity - Represents a topic of study.
 */
@Entity
@Table(name = "subject", schema = "academic_core")
public class Subject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, length = 64)
    private String code;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "type", length = 32)
    private String type; // Theory, Practical

    @Column(name = "is_optional")
    private Boolean isOptional = false;

    // Default constructor
    public Subject() {
    }

    // Builder-style constructor
    public Subject(String code, String name, String type, Boolean isOptional) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.isOptional = isOptional;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(Boolean isOptional) {
        this.isOptional = isOptional;
    }
}
