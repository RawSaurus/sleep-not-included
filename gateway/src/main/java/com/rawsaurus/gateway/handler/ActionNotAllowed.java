package com.rawsaurus.gateway.handler;

public class ActionNotAllowed extends RuntimeException {

    public ActionNotAllowed(String message) {
        super(message);
    }
}
