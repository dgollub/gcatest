package de.kurashigegollub.dev.gcatest;

import de.kurashigegollub.com.google.calender.CalendarClient;
import de.kurashigegollub.com.google.calender.CalendarFeed;
import de.kurashigegollub.com.google.calender.CalendarUrl;
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
        CalendarUrl url = CalendarUrl.getUrlAllCalendarsFeed();
        CalendarFeed feed = client.calendarFeed().list().execute(url);
        return feed;
    }
    public CalendarFeed listCalendarsOwn() throws IOException {
        CalendarUrl url = CalendarUrl.getUrlOwnCalendarsFeed();
        CalendarFeed feed = client.calendarFeed().list().execute(url);
        return feed;
    }
    
}
