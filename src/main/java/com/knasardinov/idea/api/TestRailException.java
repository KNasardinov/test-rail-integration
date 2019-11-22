package com.knasardinov.idea.api;

public class TestRailException extends RuntimeException {

    public TestRailException(String message) {
        super(message);
    }

    public TestRailException(String message, Throwable e) {
        super(message, e);
    }

}