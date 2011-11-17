/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.kurashigegollub.com.google.calender;

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * GData client.
 *
 * @author Yaniv Inbar
 */
public abstract class GDataClient {

    private final static Logger log = Logger.getLogger(GDataClient.class.getSimpleName());
    
  private HttpRequestFactory requestFactory;

  private final String gdataVersion;

  private String applicationName;

  private boolean prettyPrint;

  /** Method override needed for PATCH. */
  final MethodOverride override = new MethodOverride();

  protected GDataClient(String gdataVersion, HttpRequestFactory requestFactory) {
    this.gdataVersion = gdataVersion;
    this.requestFactory = requestFactory;
  }

  protected final HttpRequestFactory getRequestFactory() {
    return requestFactory;
  }

  public final String getApplicationName() {
    return applicationName;
  }

  public final void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public final boolean getPrettyPrint() {
    return prettyPrint;
  }


  public final void setPrettyPrint(boolean prettyPrint) {
    this.prettyPrint = prettyPrint;
  }

  protected final HttpTransport getTransport() {
    return getRequestFactory().getTransport();
  }

  protected void prepareUrl(GoogleUrl url, Class<?> parseAsType) {
    url.prettyprint = this.prettyPrint;
  }

  protected void prepare(HttpRequest request) throws IOException {
    request.getHeaders().setUserAgent(applicationName);
    request.getHeaders().put("GData-Version", gdataVersion);
    override.intercept(request);
  }

  protected final HttpResponse execute(HttpRequest request) throws IOException {
    prepare(request);
    log.info("request: " + request.getUrl().toString());
    HttpResponse response = request.execute();
    log.info("execute-ce: " + response.getContentEncoding());
    log.info("execute-ct: " + response.getContentType());
    log.info("execute-sm: " + response.getStatusMessage());
    log.info("execute-sc: " + response.getStatusCode());
    //if (request.getUrl().toString().startsWith("https://www.google.com/calendar/feeds/gollub.daniel@gmail.com"))
      //  log.info("execute: " + response.parseAsString());
    return response;
  }

  protected final <T> T executeGet(GoogleUrl url, Class<T> parseAsType) throws IOException {
    prepareUrl(url, parseAsType);
    HttpRequest request = getRequestFactory().buildGetRequest(url);
    return execute(request).parseAs(parseAsType);
  }

  protected final void executeDelete(GoogleUrl url, String etag) throws IOException {
    prepareUrl(url, null);
    HttpRequest request = getRequestFactory().buildDeleteRequest(url);
    setIfMatch(request, etag);
    execute(request).ignore();
  }

  protected final <T> T executePost(
      GoogleUrl url, AbstractHttpContent content, Class<T> parseAsType) throws IOException {
    prepareUrl(url, parseAsType);
    HttpRequest request = getRequestFactory().buildPostRequest(url, content);
    return execute(request).parseAs(parseAsType);
  }

  protected final <T> T executePatchRelativeToOriginal(
      GoogleUrl url, AbstractHttpContent patchContent, Class<T> parseAsType, String etag)
      throws IOException {
    prepareUrl(url, parseAsType);
    HttpRequest request = getRequestFactory().buildPatchRequest(url, patchContent);
    setIfMatch(request, etag);
    return execute(request).parseAs(parseAsType);
  }

  private void setIfMatch(HttpRequest request, String etag) {
    if (etag != null && !etag.startsWith("W/")) {
      request.getHeaders().setIfMatch(etag);
    }
  }
}
