package com.coursework.persistence;

public class Pager {

    private final int allElementCount;
    private final int pageSize;
    private final int numberOfPages;
    private int currentPage;

    public Pager(int allElementCount, int pageSize) {
        this.allElementCount = allElementCount;
        this.pageSize = pageSize;
        this.numberOfPages = (int) Math.ceil(allElementCount / (float) pageSize);
    }

    public int getOffset(int page) {
        setPage(page);
        return (currentPage - 1) * pageSize;
    }

    private void setPage(int page) {
        if (page <= 1) {
            currentPage = 1;
        } else currentPage = Math.min(page, numberOfPages);
    }

    public int getTotalPages() {
        return numberOfPages;
    }

    public boolean hasPrevious() {
        return currentPage > 1;
    }

    public boolean hasNext() {
        return currentPage < numberOfPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getAllElementCount() {
        return allElementCount;
    }
}
