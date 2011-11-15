package de.kurashigegollub.dev.gcatest;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

/**
 * Copyright by Daniel Kurashige-Gollub, 2011
 * @author Daniel Kurashige-Gollub, daniel@kurashige-gollub.de
 */
public class Utils {

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
}
