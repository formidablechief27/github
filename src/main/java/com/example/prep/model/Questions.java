package com.example.prep.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "questions")
public class Questions {

	 @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "tag")
    private String tag;

    @Column(name = "testcase_start")
    private int testcaseStart;

    @Column(name = "testcase_end")
    private int testcaseEnd;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getTestcaseStart() {
        return testcaseStart;
    }

    public void setTestcaseStart(int testcaseStart) {
        this.testcaseStart = testcaseStart;
    }

    public int getTestcaseEnd() {
        return testcaseEnd;
    }

    public void setTestcaseEnd(int testcaseEnd) {
        this.testcaseEnd = testcaseEnd;
    }
}