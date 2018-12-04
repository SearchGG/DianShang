package com.pingyougou.utils;

import java.io.Serializable;

public class pingyougouResult implements Serializable {

    private boolean success;
    private String message;

    public pingyougouResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
