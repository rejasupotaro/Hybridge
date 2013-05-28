package com.rejasupotaro.hybridge.exception;

public class LoadInvalidDomainUrlException extends RuntimeException {
    private static final long serialVersionUID = -4245926211109611797L;

    public LoadInvalidDomainUrlException() {
        super();
    }

    public LoadInvalidDomainUrlException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public LoadInvalidDomainUrlException(String detailMessage) {
        super(detailMessage);
    }

    public LoadInvalidDomainUrlException(Throwable throwable) {
        super(throwable);
    }
}
