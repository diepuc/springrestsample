package com.userbot.test.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenRequestException extends Exception{

    private static final long serialVersionUID = 1L;

    public ForbiddenRequestException(){
        super("Non autorizzato.");
    }

    public ForbiddenRequestException(String message){
        super(message);
    }
}