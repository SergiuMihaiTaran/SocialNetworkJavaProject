package com.example.demo.Utils;

public class Pageable {
    private int pageSize;
    private int pageNumber;

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public Pageable(int pageSize, int pageNumber) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }
}
