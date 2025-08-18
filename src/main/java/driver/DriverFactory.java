package driver;

public enum DriverFactory {

    CHROME {
        @Override
        public DriverManager getDriverManager() {
            return new ChromeDriverManager();
        }
    },
    FIREFOX {
        @Override
        public DriverManager getDriverManager() {
            return new FirefoxDriverManager();
        }
    },

    MOBILE {
        @Override
        public DriverManager getDriverManager() { return new MobileDriverManager(); }
    };

    public abstract DriverManager getDriverManager();
}
