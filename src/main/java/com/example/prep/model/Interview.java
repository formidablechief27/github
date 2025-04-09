package com.example.prep.model;

import javax.persistence.*;

@Entity
@Table(name = "interview")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer s1;
    private Integer c1;
    private Integer s2;
    private Integer c2;
    private Integer s3;
    private Integer c3;
    private Integer s4;
    private Integer c4;
    private Integer s5;
    private Integer c5;
    private Integer s6;
    private Integer c6;
    
    public Interview() {
    }

    public Interview(Integer s1, Integer c1, Integer s2, Integer c2, Integer s3, Integer c3,
                     Integer s4, Integer c4, Integer s5, Integer c5, Integer s6, Integer c6) {
        this.s1 = s1;
        this.c1 = c1;
        this.s2 = s2;
        this.c2 = c2;
        this.s3 = s3;
        this.c3 = c3;
        this.s4 = s4;
        this.c4 = c4;
        this.s5 = s5;
        this.c5 = c5;
        this.s6 = s6;
        this.c6 = c6;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getS1() {
        return s1;
    }

    public void setS1(Integer s1) {
        this.s1 = s1;
    }

    public Integer getC1() {
        return c1;
    }

    public void setC1(Integer c1) {
        this.c1 = c1;
    }

    public Integer getS2() {
        return s2;
    }

    public void setS2(Integer s2) {
        this.s2 = s2;
    }

    public Integer getC2() {
        return c2;
    }

    public void setC2(Integer c2) {
        this.c2 = c2;
    }

    public Integer getS3() {
        return s3;
    }

    public void setS3(Integer s3) {
        this.s3 = s3;
    }

    public Integer getC3() {
        return c3;
    }

    public void setC3(Integer c3) {
        this.c3 = c3;
    }

    public Integer getS4() {
        return s4;
    }

    public void setS4(Integer s4) {
        this.s4 = s4;
    }

    public Integer getC4() {
        return c4;
    }

    public void setC4(Integer c4) {
        this.c4 = c4;
    }

    public Integer getS5() {
        return s5;
    }

    public void setS5(Integer s5) {
        this.s5 = s5;
    }

    public Integer getC5() {
        return c5;
    }

    public void setC5(Integer c5) {
        this.c5 = c5;
    }

    public Integer getS6() {
        return s6;
    }

    public void setS6(Integer s6) {
        this.s6 = s6;
    }

    public Integer getC6() {
        return c6;
    }

    public void setC6(Integer c6) {
        this.c6 = c6;
    }
}
