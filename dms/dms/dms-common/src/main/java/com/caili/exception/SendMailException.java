package com.caili.exception;


import java.io.IOException;

public class SendMailException extends IOException {

    public SendMailException() {
    }

    public SendMailException(String message) {
        super(message);
    }

    public SendMailException(Throwable cause) {
        super(cause);
    }
}
