package com.fujitsu.trial.exception;

public class VehicleForbiddenException extends RuntimeException {
    public VehicleForbiddenException(String message) {
        super(message);
    }
}