package org.sc.common.rest;

import java.util.*;


public class TrailPreviewDto {
    private String code;
    private TrailClassification classification;
    private PositionDto startPos;
    private PositionDto Pos;
    private Date date;

    public TrailPreviewDto() {
    }

    public TrailPreviewDto(String code, TrailClassification classification,
                           PositionDto startPos, PositionDto Pos, Date date) {
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

    public PositionDto getStartPos() {
        return startPos;
    }

    public PositionDto getPos() {
        return Pos;
    }

    public Date getDate() {
        return date;
    }
}