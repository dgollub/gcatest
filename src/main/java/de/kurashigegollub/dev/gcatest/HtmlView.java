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
import de.kurashigegollub.com.google.calender.CalendarEntry;
import de.kurashigegollub.com.google.calender.CalendarFeed;
import de.kurashigegollub.com.google.calender.Entry;
import de.kurashigegollub.com.google.calender.EventEntry;
import de.kurashigegollub.com.google.calender.EventFeed;
import de.kurashigegollub.com.google.calender.Feed;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 */
public class HtmlView {

    public static String createListHtml(String baseUrl, Feed feed, DateTime dtnow, DateTime dt2weeks,
                                        String backUrl) {

        StringBuilder sb = new StringBuilder();

        if (feed.author != null) {
            sb.append("<span class=\"author\">Author: ");
            if (feed.author.name != null)
                sb.append(feed.author.name);
            else
                sb.append("&lt;no name found&gt;");
            if (feed.author.email != null && !feed.author.email.equalsIgnoreCase(feed.author.name))
                sb.append(", ").append(feed.author.email);
            sb.append("</span><br><br>\n");
        }        
        
        //CalendarFeed and EventFeed are mutual exclusive classes, so this is okay to do.
        CalendarFeed cf = null;
        if (feed instanceof CalendarFeed)
            cf = (CalendarFeed) feed;
        EventFeed ef = null;
        if (feed instanceof EventFeed)
            ef = (EventFeed) feed;
        
        if (cf != null) {
            sb.append("<p>Please select a calendar to see its events for the next two weeks, from ");
            sb.append(dtnow.toString());
            sb.append(" until ");
            sb.append(dt2weeks.toString());
            sb.append(".</p>\n");                       
        }
        if (ef != null) {
            sb.append("<p>Please select the entry you want to send via your Gmail account.</p>\n");            
            //add the gmail javascript and html stuff here
            sb.append(createGmailPrepForm());
        }

        sb.append("<hr>");

        for (Entry entry : feed.getEntries()) {
            sb.append(createListEntryHtml(baseUrl, entry));
            sb.append("<br>");
        }       
        
        sb.append("<hr>");
        
        return sb.toString();
    }

    public static String createListEntryHtml(String baseUrl, Entry entry) {
        StringBuilder sb = new StringBuilder();
        
        String color = null;
        EventEntry event = null;
        if (entry instanceof EventEntry) {
            event = (EventEntry) entry;
        }
        CalendarEntry ce = null;
        if (entry instanceof CalendarEntry) {
            ce = (CalendarEntry)entry;
            color = ce.color != null ? ce.color.value : null;
        }
        
        sb.append("<div class=\"entry\" id=\"").append(entry.id).append("\">");
        sb.append("<span class=\"title\">");
        
        if (ce != null) {
            sb.append("<a href=\"");
            sb.append(createUrlForCalendar(baseUrl, entry.getDecodedId()));
            sb.append("\">");
        }
        sb.append(entry.title);
        if (ce != null) {
            sb.append("</a>");
        }
        sb.append("</span>\n");
        if (ce == null) { //we only need the checkbox when these entries are calendar entries and not calendars
            sb.append("<br>\n<span class=\"checkbox\"><input type=\"checkbox\"");
            sb.append("name=\"entry_checked\" value=\"");
            sb.append(entry.id);
            sb.append("\">Send this entry?</span>\n");
        }
        
        //sb.append("<br>");
        //sb.append("ID: ").append(entry.id).append(" - ").append(entry.getCalendarId());
        //sb.append("<br>");
        
        if (!Utils.isEmpty(color)) {
            sb.append("<br><span style='background-color:").append(color).append(";");
            sb.append("color:").append(color).append(";'>");
            sb.append("color for this calendar</span>");
        }
        
        sb.append("<br>");
        
        if (event != null && event.content != null) {
            sb.append("<span class=\"content\">");
            sb.append(event.content);
            sb.append("</span>");
            sb.append("<br>");
        }

        if (entry.author != null) {
            sb.append("<span class=\"author\">Author: ");
            if (entry.author.name != null)
                sb.append(entry.author.name);
            else
                sb.append("&lt;no name found&gt;");
            if (entry.author.email != null &&
               !entry.author.email.equalsIgnoreCase(entry.author.email)) {
//               //for some calendars these two values may indeed be the same
//               !entry.author.email.equalsIgnoreCase(entry.summary)
                sb.append(", ").append(entry.author.email);
            }
            sb.append("</span>");
            sb.append("<br>");
        }
        
        sb.append("<span class=\"updated\">Updated: ");
        sb.append(entry.updated);
        sb.append("</span>");
        
        if (entry.summary != null) {
            sb.append("<br>");
            sb.append("<span class=\"summary\">Summary: ");
            sb.append(entry.summary);
            sb.append("</span>");
        }
        
        if (event != null) {
            //sb.append("<br><span class=\"id\">").append(event.getEventId()).append("</span>");
            if (event.when != null) {
                if (event.when.startTime != null) {
                    sb.append("<br>");
                    sb.append("<span class=\"time\">Start: ");
                    sb.append(event.when.startTime);
                    sb.append("</span>");
                }
                if (event.when.endTime != null) {
                    sb.append("<br>");
                    sb.append("<span class=\"time\">End: ");
                    sb.append(event.when.endTime);
                    sb.append("</span>");
                }
                if (event.when.valueString != null) {
                    sb.append("<br>");
                    sb.append("<span class=\"info\">Info: ");
                    sb.append(event.when.valueString);
                    sb.append("</span>");
                }
            }            
        }
        sb.append("</div>\n\n");
        return sb.toString();
    }
    
    private static String createUrlForCalendar(String base, String calendarId) {
        try {
            calendarId = URLEncoder.encode(calendarId, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HtmlView.class.getName()).log(Level.SEVERE, null, ex);
        }
        String url = base;
        if (url.indexOf("?") != -1)
            url += "&calendarId=" + calendarId;
        else
            url += "?calendarId=" + calendarId;
        return url;
    }
    
    
    private static String createGmailPrepForm() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n<div id=\"mail_box\">");
        sb.append("\n<p>Please select the entries you want to send as an email.</p>");
        sb.append("\n<form id=\"mail_form\">");
        sb.append("\n<table border=\"0\">");
        sb.append("\n<tr>");
        sb.append("\n<td>TO:</td>");
        sb.append("\n<td><input id=\"mail_to\" type=\"text\" size=\"60\" maxlength=\"50\" title=\"TO\"></td>");
        sb.append("\n</tr>");
        sb.append("\n<tr>");
        sb.append("\n<td>CC:</td>");
        sb.append("\n<td><input id=\"mail_cc\" type=\"text\" size=\"60\" maxlength=\"50\" title=\"CC\"></td>");
        sb.append("\n</tr>");
        sb.append("\n<tr>");
        sb.append("\n<td>Subject:</td>");
        sb.append("\n<td><input id=\"mail_subject\" type=\"text\" size=\"60\" maxlength=\"50\" title=\"Subject\"></td>");
        sb.append("\n</tr>  ");
        sb.append("\n<tr>");
        sb.append("\n<td>");
        sb.append("\n<button type=\"button\" id=\"mail_button_open\" onclick=\"openGmail();\">");
        sb.append("\n<!-- We could make this button more stylish if we want to with some more HTML here -->");
        sb.append("\nOpen Gmail");
        sb.append("\n</button>");
        sb.append("\n</td>");
        sb.append("\n<td>This will open a Gmail compose window with the selected calendar entries.</td>");
        sb.append("\n</tr>");
        sb.append("\n</table>");
        sb.append("\n</form>");
        sb.append("\n</div>\n");
        return sb.toString();
    }
    
}
