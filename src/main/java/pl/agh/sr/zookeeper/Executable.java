package pl.agh.sr.zookeeper;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

/**
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

    public boolean start() {
        LOG.info("Starting executable application {}", command);
        try {
            this.process = Runtime.getRuntime().exec(command);
            return true;
        } catch (IOException e) {
            LOG.error("Cannot run executable. Cause: ", e);
        }
        return false;
    }

    public boolean stop() {
        LOG.info("Stopping executable application...");

        if (process.isAlive()) {
            this.process.destroy();
            return true;
        }
        return false;
    }
}
