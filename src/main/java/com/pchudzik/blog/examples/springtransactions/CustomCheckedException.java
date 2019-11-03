package com.pchudzik.blog.examples.springtransactions;

class CustomCheckedException extends Exception {
    public CustomCheckedException(String message) {
        super(message);
    }
}
