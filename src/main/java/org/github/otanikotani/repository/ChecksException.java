package org.github.otanikotani.repository;

import kong.unirest.UnirestParsingException;
import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class ChecksException extends RuntimeException {

  private final String requestUrl;
  private final int status;
  private final String statusText;
  private final UnirestParsingException parsingError;

  public ChecksException(String requestUrl, int status, String statusText, UnirestParsingException parsingError) {
    this.requestUrl = requestUrl;
    this.status = status;
    this.statusText = statusText;
    this.parsingError = parsingError;
  }
}
