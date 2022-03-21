package com.bsc212.pdsa.utils;

import java.io.Serializable;

public class Wrapper <T extends Serializable> implements Serializable {
    private T wrapped;

    public Wrapper(T wrapped) {
        this.wrapped = wrapped;
    }

    public T get() {
        return wrapped;
    }
}