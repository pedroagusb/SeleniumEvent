package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import logging.Logging;

public class PropertyReader implements Logging {

    private Properties prop = new Properties();

    public PropertyReader() {
        try (InputStream in = getClass().getResourceAsStream("/" + Constants.PROPERTIES_NAME)){
            try {
                log().debug("Attempting event.properties file load.");
                prop.load(in);
            } catch (FileNotFoundException e) {
                log().error(Constants.PROPERTIES_NAME + " Property file not found", e.getLocalizedMessage());
            }

        } catch (IOException e) {
            log().error("Error reading file " + Constants.PROPERTIES_NAME, e.getLocalizedMessage());
        }
    }

    public String getString(String propertyName) {
        return prop.getProperty(propertyName);
    }

    public Integer getInt(String propertyName) {
        int temp = -1;

        try {
            temp = Integer.parseInt(prop.getProperty(propertyName));
        } catch (NumberFormatException e) {
            log().error("The property named: {} cannot be parsed to an Int.", propertyName);
        }
        return temp;
    }
}