/**
 * Copyright (C) 2011 Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 * Please see the README file for details.
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

package de.kurashigegollub.dev.gcatest;

import com.google.api.client.util.DateTime;
import de.kurashigegollub.com.google.calender.CalendarClient;
import de.kurashigegollub.com.google.calender.CalendarFeed;
import de.kurashigegollub.com.google.calender.CalendarUrl;
import de.kurashigegollub.com.google.calender.EventFeed;
import java.io.IOException;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 */
public class GoogleCalendar {

    private CalendarClient client;
    
    public GoogleCalendar(CalendarClient client) {
        this.client = client;
    }
    
    public CalendarFeed listCalendarsAll() throws IOException {
        CalendarUrl url = CalendarUrl.forAllCalendarsFeed();
        CalendarFeed feed = client.calendarFeed().list().execute(url);
        return feed;
    }
    public CalendarFeed listCalendarsOwn() throws IOException {
        CalendarUrl url = CalendarUrl.forOwnCalendarsFeed();
        client.setPartialResponse(false);
        CalendarFeed feed = client.calendarFeed().list().execute(url);
        return feed;
    }
    
    public EventFeed listEventsForCalendar(String calendarId, DateTime dtFrom, DateTime dtTo) throws IOException {
        CalendarUrl url = CalendarUrl.forCalendarEvents(calendarId);
        url.startMin = dtFrom.toStringRfc3339();
        url.startMax = dtTo.toStringRfc3339();
        url.maxResults = 100; //TODO: maybe set this higher for very active calendars? This really should be a user setting of some kind
        client.setPartialResponse(false);
        EventFeed feed = client.eventFeed().list().execute(url);
        return feed;
    }
}
