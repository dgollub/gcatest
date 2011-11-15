/*
 * Copyright (c) 2010 Google Inc.
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
import com.google.api.client.util.Key;

/**
 * @author Yaniv Inbar, edited by Daniel Kurashige-Gollub, 2011
 * 
 */
public class CalendarUrl extends GoogleUrl {

    public static final String CALENDER_ROOT_URL = "https://www.google.com/calendar/feeds";
    @Key("max-results")
    public Integer maxResults;

    public CalendarUrl(String url) {
        super(url);
    }

    public static CalendarUrl getUrlRoot() {
        return new CalendarUrl(CALENDER_ROOT_URL);
    }

    public static CalendarUrl getUrlCalendarMetafeed() {
        CalendarUrl result = getUrlRoot();
        result.getPathParts().add("default");
        return result;
    }

    public static CalendarUrl getUrlAllCalendarsFeed() {
        CalendarUrl result = getUrlCalendarMetafeed();
        result.getPathParts().add("allcalendars");
        result.getPathParts().add("full");
        return result;
    }

    public static CalendarUrl getUrlOwnCalendarsFeed() {
        CalendarUrl result = getUrlCalendarMetafeed();
        result.getPathParts().add("owncalendars");
        result.getPathParts().add("full");
        return result;
    }
}
