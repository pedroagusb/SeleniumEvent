package utils;

import java.time.Duration;

public class Constants {

    private Constants(){
        throw new UnsupportedOperationException("This is a utility class");
    }

    public static final String PROPERTIES_NAME = "event.properties";
    private static PropertyReader props = new PropertyReader();

    public static String getContextUrl() {
        return getPropertyWithFallback("url");
    }

    public static String getBrowser() {
        String browser = getPropertyWithFallback("browser");
        return browser != null ? browser.toUpperCase() : "CHROME";
    }

    public static boolean isHeadless() {
        String headless = getPropertyWithFallback("headless");
        return "true".equalsIgnoreCase(headless);
    }

    public static Duration getDefaultTimeout(){
        String timeout = getPropertyWithFallback("default.timeout");
        int seconds = timeout != null ? Integer.parseInt(timeout) : 10;
        return Duration.ofSeconds(seconds);
    }

    public static Duration getElementTimeout(){
        String timeout = getPropertyWithFallback("element.timeout");
        int seconds = timeout != null ? Integer.parseInt(timeout) : 8;
        return Duration.ofSeconds(seconds);
    }

    public static Duration getPageTimeout(){
        String timeout = getPropertyWithFallback("page.timeout");
        int seconds = timeout != null? Integer.parseInt(timeout) : 8;
        return Duration.ofSeconds(seconds);
    }

    public static Duration getPollingInterval() {
        String interval = getPropertyWithFallback("polling.interval");
        int milliseconds = interval != null ? Integer.parseInt(interval) : 500;
        return Duration.ofMillis(milliseconds);
    }

    private static String getPropertyWithFallback(String propertyName){
        String systemValue = System.getProperty(propertyName);
        if(systemValue != null && !systemValue.trim().isEmpty()){
            return systemValue;
        }

        return props.getString(propertyName);
    }
}