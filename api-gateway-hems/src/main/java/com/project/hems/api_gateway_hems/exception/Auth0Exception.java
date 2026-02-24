package com.project.hems.api_gateway_hems.exception;

public class Auth0Exception extends RuntimeException {
  public Auth0Exception(String message) { super(message); }
  public Auth0Exception(String message, Throwable cause) { super(message, cause); }
}