package com.rawsaurus.sleep_not_included.comment.handler;

public class ActionNotAllowed extends RuntimeException {

    public ActionNotAllowed(String message) {
        super(message);
    }
}
