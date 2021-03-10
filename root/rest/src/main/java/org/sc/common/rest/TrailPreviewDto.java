package org.sc.common.rest;

import org.sc.data.model.TrailClassification;

import java.util.Date;
import java.util.Objects;


public class TrailPreviewDto {
    private String code;
    private TrailClassification classification;
    private PositionDto startPos;
    private PositionDto finalPos;
    private Date date;

    public TrailPreviewDto() {
    }

    public TrailPreviewDto(String code, TrailClassification classification,
                           PositionDto startPos, PositionDto finalPos, Date date) {
        this.code = code;
        this.classification = classification;
        this.startPos = startPos;
        this.finalPos = finalPos;
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public TrailClassification getClassification() {
        return classification;
    }

    public PositionDto getStartPos() {
        return startPos;
    }

    public PositionDto getFinalPos() {
        return finalPos;
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

    public void setStartPos(PositionDto startPos) {
        this.startPos = startPos;
    }

    public void setFinalPos(PositionDto finalPos) {
        this.finalPos = finalPos;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrailPreviewDto that = (TrailPreviewDto) o;
        return getCode().equals(that.getCode()) &&
                getClassification() == that.getClassification() &&
                getStartPos().equals(that.getStartPos()) &&
                getFinalPos().equals(that.getFinalPos()) &&
                getDate().equals(that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getClassification(), getStartPos(), getFinalPos(), getDate());
    }
}