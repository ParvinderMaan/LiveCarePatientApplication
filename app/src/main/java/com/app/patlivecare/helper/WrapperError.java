package com.app.patlivecare.helper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WrapperError extends RuntimeException{
    @Expose
    @SerializedName("status_code")
    private Long statusCode;

    @Expose
    @SerializedName("message")
    private String message;

    public WrapperError(Long statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }


    public WrapperError(Long statusCode) {
        this.statusCode = statusCode;
    }


    public Long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
