package com.school.academic.domain;

import jakarta.persistence.*;

/**
 * Section entity - Represents a division identifier (e.g., A, B, Blue).
 */
@Entity
@Table(name = "section", schema = "academic_core")
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 16)
    private String code;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Default constructor
    public Section() {
    }

    // Builder-style constructor
    public Section(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
