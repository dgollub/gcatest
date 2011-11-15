package de.kurashigegollub.dev.gcatest;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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


}
