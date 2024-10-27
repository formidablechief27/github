package com.example.prep.model;

import javax.persistence.*;

@Entity
@Table(name = "testcases")
public class Testcases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "input", columnDefinition = "TEXT")
    private String input;

    @Column(name = "output", columnDefinition = "TEXT")
    private String output;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
