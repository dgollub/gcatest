package de.kurashigegollub.dev.gcatest;

import de.kurashigegollub.com.google.calender.Entry;
import de.kurashigegollub.com.google.calender.EventEntry;
import de.kurashigegollub.com.google.calender.Feed;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 */
public class HtmlView {

    public static String createFeedHtml(Feed feed) {

        StringBuilder sb = new StringBuilder();

        for (Entry entry : feed.getEntries()) {
            sb.append(createEntryHtml(entry));
            sb.append("<br>");
        }

        return sb.toString();
    }

    public static String createEntryHtml(Entry entry) {
        StringBuilder sb = new StringBuilder();

        sb.append("<div class=\"entry\">");
        sb.append("<span class=\"title\">");
        sb.append(entry.title);
        sb.append("</span>");
        
        sb.append("<br>");
        
        sb.append("<span class=\"updated\">");
        sb.append(entry.updated);
        sb.append("</span>");
        
        if (entry.summary != null) {
            sb.append("<br>");
            
            sb.append("<span class=\"summary\">");
            sb.append(entry.summary);
            sb.append("</span>");
        }
        
        if (entry instanceof EventEntry) {
            EventEntry event = (EventEntry) entry;
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
        sb.append("</div>");
        return sb.toString();
    }
}
