package pl.agh.sr.zookeeper;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents application managed using ZooKeeper.
 *
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class Executable {
    private static final Logger LOG = getLogger(Executable.class);
    private final String command;
    private Process process;

    public Executable(String executableName, String[] args) {
        StringBuilder builder = new StringBuilder(executableName);
        Arrays.stream(args).map(arg -> " " + arg).forEach(builder::append);
        this.command = builder.toString();
    }

    /**
     * Logic to invoke on application start.
     */
    public void start() {
        LOG.info("Starting executable application {}", command);
        try {
            this.process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            LOG.error("Cannot run executable. Cause: ", e);
        }
    }

    /**
     * Logic to invoke to stop running application.
     */
    public void stop() {
        LOG.info("Stopping executable application...");

        if (process.isAlive()) {
            this.process.destroy();
        }
    }
}
