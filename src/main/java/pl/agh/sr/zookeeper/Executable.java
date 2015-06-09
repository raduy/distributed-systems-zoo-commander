package pl.agh.sr.zookeeper;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class Executable {
    private static final Logger LOG = getLogger(Executable.class);
    private final String executableName;
    private final String[] args;

    public Executable(String executableName, String[] args) {
        this.executableName = executableName;
        this.args = args;
    }

    public void start() {
        LOG.info("Starting executable application...");
        //todo implement
    }

    public void stop() {
        LOG.info("Stopping executable application...");
        //todo implement
    }
}
