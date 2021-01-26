package org.sc.data.entity;

import org.sc.common.rest.TrailClassification;

import java.util.Date;


public class TrailPreview {
    private String code;
    private TrailClassification classification;
    private Position startPos;
    private Position Pos;
    private Date date;

    public TrailPreview() {
    }

    public TrailPreview(String code, TrailClassification classification,
                        Position startPos, Position Pos, Date date) {
        this.code = code;
        this.classification = classification;
        this.startPos = startPos;
        this.Pos = Pos;
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public TrailClassification getClassification() {
        return classification;
    }

    public Position getStartPos() {
        return startPos;
    }

    public Position getPos() {
        return Pos;
    }

    public Date getDate() {
        return date;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setClassification(TrailClassification classification) {
        this.classification = classification;
    }

    public void setStartPos(Position startPos) {
        this.startPos = startPos;
    }

    public void setPos(Position pos) {
        Pos = pos;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}