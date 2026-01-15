package com.school.academic.domain;

import jakarta.persistence.*;

/**
 * Class (Grade) entity - Represents a pedagogical level (e.g., Grade 1, Grade 10).
 */
@Entity
@Table(name = "class", schema = "academic_core")
public class GradeClass extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 32)
    private String code;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "level_order")
    private Integer levelOrder;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Default constructor
    public GradeClass() {
    }

    // Builder-style constructor
    public GradeClass(String code, String name, Integer levelOrder, String description) {
        this.code = code;
        this.name = name;
        this.levelOrder = levelOrder;
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

    public Integer getLevelOrder() {
        return levelOrder;
    }

    public void setLevelOrder(Integer levelOrder) {
        this.levelOrder = levelOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
