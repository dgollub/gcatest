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

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 */
public class Utils {

    private static final Logger log = Logger.getLogger(Utils.class.getSimpleName());
    
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public static HttpTransport getHttpTransport() {
        return HTTP_TRANSPORT;
    }

    public static JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }

    public static boolean isEmpty(String s) {
        return (s == null || s.isEmpty());
    }

    /**
     * Read a configuration value from the properties file
     * @param string
     * @return 
     */
    public static String readFromPropertiesAsString(String propertiesFile, String propertyName) throws IOException {

        InputStream is = Utils.class.getResourceAsStream(propertiesFile.replace('/', File.separatorChar));
        if (is == null) {
            throw new IOException(String.format("Could not find %s", propertiesFile));
        }

        Properties p = new Properties();
        p.load(is);

        return p.getProperty(propertyName);
    }
    
    public static String readFileAsStringFromBundle(String filename) throws IOException {        
        InputStream is = Utils.class.getResourceAsStream(("/"+filename).replace('/', File.separatorChar));
        if (is == null) {
            throw new IOException(String.format("Could not find %s", filename));
        }
        StringWriter sw = new StringWriter();
        IOUtils.copy(is, sw, "utf-8"); //this uses Apache commons io
        return sw.toString();
    }

    /**
     * 
     * Will return a String with the request URL. 
     * @param req The current HttpServletRequest.
     * @param includeServlet Will include the servlet name in the return value.
     * @param includePathInfo Will include the path and query parts in the return value (only added, if includeServlet is true as well).
     * @return 
     */
    // http://hostname.com:80/appname/servlet/MyServlet/a/b;c=123?d=789
    public static String reconstructURL(HttpServletRequest req, boolean includeServlet, boolean includePathInfo) {
        String scheme       = req.getScheme();         // http
        String serverName   = req.getServerName();     // hostname.com
        int serverPort      = req.getServerPort();     // 80
        String contextPath  = req.getContextPath();    // /appname
        String servletPath  = req.getServletPath();    // /servlet/MyServlet
        String pathInfo     = req.getPathInfo();       // /a/b;c=123
        String queryString  = req.getQueryString();    // d=789

        // Reconstruct original requesting URL
        String url = scheme + "://" + serverName + ":" + serverPort + contextPath;
        
        if (includeServlet) {
            url += servletPath;
            if (includePathInfo) {
                if (pathInfo != null) {
                    url += pathInfo;
                }
                if (queryString != null) {
                    url += "?" + queryString;
                }
            }
        }
        return url;
    }
    public static String getDateAsString(DateTime dt) {
        return getDateAsString(dt, null);
    }
    public static String getDateAsString(DateTime dt, String format) {
        return getDateAsString(new Date(dt.getValue()), format);
    }
    public static String getDateAsString(Date dt) {
        return getDateAsString(dt, null);
    }
    public static String getDateAsString(Date dt, String format) {
        if (isEmpty(format))
            format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(dt);
    }
    
    public static DateTime getDateTimeNow() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        //Google's DateTime class works with GMT timezone internally and converts all passed in Date values to it.
        //see code here:
        //http://code.google.com/p/google-api-java-client/source/browse/google-api-client/src/main/java/com/google/api/client/util/DateTime.java?spec=svnf7334c6f6f7c0941306e18de989c90a053941669&r=f7334c6f6f7c0941306e18de989c90a053941669
        //Therefore we have to apply our local timezone here so the calculated time will
        DateTime dtg = new DateTime(c.getTime(), TimeZone.getDefault());
//        log.info("TimeZone: " + c.getTimeZone().getDisplayName());
//        log.info("TimeZone: " + c.getTimeZone().getID());
//        log.info("Date: " + getDateAsString(c.getTime()));
//        log.info("DateTime: " + getDateAsString(dtg));
//        log.info("DateTime: " + dtg.toString());
//        log.info("DateTime: " + dtg.toStringRfc3339());
        return dtg;
    }      
    
    public static DateTime getDateTime2Weeks(DateTime dtFrom) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dtFrom.getValue());
        c.add(Calendar.WEEK_OF_YEAR, 2);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return new DateTime(c.getTime(), TimeZone.getDefault());
    }
    
    public static String getStackTraceAsString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
