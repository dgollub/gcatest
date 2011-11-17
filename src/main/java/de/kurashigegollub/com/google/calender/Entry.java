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

import de.kurashigegollub.dev.gcatest.googleadditions.Author;
import com.google.api.client.util.Data;
import com.google.api.client.util.Key;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Yaniv Inbar
 */
public class Entry implements Cloneable {

    @Key("id")
    public String id;

    @Key("author")
    public Author author;
    
    @Key
    public String summary;
    
    @Key
    public String title;
    
    @Key
    public String updated;
    
    @Key("link")
    public List<Link> links;

    @Override
    protected Entry clone() {
        try {
            @SuppressWarnings("unchecked")
            Entry result = (Entry) super.clone();
            Data.deepCopy(this, result);
            return result;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public String getDecodedId() {
        String tmp = id.substring(id.lastIndexOf("/")+1);        
        try {
            return URLDecoder.decode(tmp, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
            return tmp;
        }
    }

    public String getEditLink() {
        return Link.find(links, "edit");
    }
    
    public String getEventFeedLinks() {
        return Link.find(links, "http://schemas.google.com/gCal/2005#eventFeed");
    }
}
