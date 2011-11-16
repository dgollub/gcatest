package de.kurashigegollub.dev.gcatest;

import de.kurashigegollub.com.google.calender.CalendarEntry;
import de.kurashigegollub.com.google.calender.Entry;
import de.kurashigegollub.com.google.calender.EventEntry;
import de.kurashigegollub.com.google.calender.Feed;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 */
public class HtmlView {

    public static String createCalendarListHtml(String baseUrl, Feed feed) {

        StringBuilder sb = new StringBuilder();

        for (Entry entry : feed.getEntries()) {
            sb.append(createCalendarListEntryHtml(baseUrl, entry));
            sb.append("<br>");
        }       

        return sb.toString();
    }

    public static String createCalendarListEntryHtml(String baseUrl, Entry entry) {
        StringBuilder sb = new StringBuilder();
        
        String color = entry.color != null ? entry.color.value : null;
        EventEntry event = null;
        if (entry instanceof EventEntry) {
            event = (EventEntry) entry;
        }
        
        sb.append("<div class=\"entry\"");
        if (!Utils.isEmpty(color)) {
            sb.append(" style='background-color:").append(color).append(";'");
        }
        sb.append(">\n");
        sb.append("<span class=\"title\"><a href=\"");
        sb.append(createUrl(baseUrl, entry.getEditLink()));
        sb.append("\">");
        sb.append(entry.title);
        sb.append("</a></span>");
        
        sb.append("<br>");//.append("color:").append(color).append("<br>");
        
        sb.append("<span class=\"updated\">");
        sb.append(entry.updated);
        sb.append("</span>");
        
        if (entry.summary != null) {
            sb.append("<br>");
            
            sb.append("<span class=\"summary\">");
            sb.append(entry.summary);
            sb.append("</span>");
        }
        
        if (event != null) {
            if (event.when != null) {
                if (event.when.startTime != null) {
                    sb.append("<br>");
                    sb.append("<span class=\"time\">");
                    sb.append(event.when.startTime);
                    sb.append("</span>");
                }
                if (event.when.endTime != null) {
                    sb.append("<br>");
                    sb.append("<span class=\"time\">");
                    sb.append(event.when.endTime);
                    sb.append("</span>");
                }
            }
        }
        sb.append("</div>\n\n");
        return sb.toString();
    }
    
    private static String createUrl(String base, String calendar) {
        return base + calendar;
    }
}
