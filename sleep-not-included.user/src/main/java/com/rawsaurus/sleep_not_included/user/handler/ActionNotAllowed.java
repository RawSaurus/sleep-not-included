package com.rawsaurus.sleep_not_included.user.handler;

public class ActionNotAllowed extends RuntimeException {

    public ActionNotAllowed(String message) {
        super(message);
    }
}
