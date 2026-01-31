package com.rawsaurus.sleep_not_included.build.handler;

public class ActionNotAllowed extends RuntimeException {

    public ActionNotAllowed(String message) {
        super(message);
    }
}
