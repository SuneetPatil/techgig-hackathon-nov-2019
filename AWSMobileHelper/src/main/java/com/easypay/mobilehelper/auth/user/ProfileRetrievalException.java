package com.easypay.mobilehelper.auth.user;
//
// Copyright 2017 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.18
//

/**
 * Thrown when a user profile cannot be retrieved.
 */
public class ProfileRetrievalException extends Exception {

    /**
     * Constructor.
     * @param message the message.
     */
    public ProfileRetrievalException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message the message.
     * @param cause the cause.
     */
    public ProfileRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
