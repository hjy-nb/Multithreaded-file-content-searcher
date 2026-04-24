package com.yao.mfcs;

public class SearchResultIsNull extends RuntimeException{

    public SearchResultIsNull(String message) {
        super(message);
    }

    public SearchResultIsNull(String message, Throwable cause) {
        super(message, cause);
    }
}
