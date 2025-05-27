package logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Logging {

    default Logger log() {
        return LogManager.getLogger(getClass());
    }
}