package com.example.demo.Utils;


public class Page<E>{
    private int totalNumberOfElements;
    private Iterable<E> elementsOnPage;

    public Page(int totalNumberOfElements, Iterable<E> elementsOnPage) {
        this.totalNumberOfElements = totalNumberOfElements;
        this.elementsOnPage = elementsOnPage;
    }

    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }

    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }
}
