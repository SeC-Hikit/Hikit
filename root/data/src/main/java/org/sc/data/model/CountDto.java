package org.sc.common.rest;

public class CountDto {

    private long count;

    public CountDto(long count) {
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
