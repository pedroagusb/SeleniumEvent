package utils;

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

    private static String getPropertyWithFallback(String propertyName){
        String systemValue = System.getProperty(propertyName);
        if(systemValue != null && !systemValue.trim().isEmpty()){
            return systemValue;
        }

        return props.getString(propertyName);
    }
}