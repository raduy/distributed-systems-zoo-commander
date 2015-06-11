package pl.agh.sr.zookeeper.connection;

import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Creates and maintain connection with ZooKeeper server.
 *
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class ConnectionHolder implements Runnable {
    private static final Logger LOG = getLogger(ConnectionHolder.class);
    private static final int SESSION_TIMEOUT = 3000;
    private final ZooKeeper zooKeeper;

    public ConnectionHolder(String connectionString) throws IOException {

        ConnectionChangeWatcher connectionChangeWatcher = new ConnectionChangeWatcher();
        this.zooKeeper = new ZooKeeper(connectionString, SESSION_TIMEOUT, connectionChangeWatcher);
    }

    public ZooKeeper zooKeeper() {
        return zooKeeper;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            LOG.error("Interruption exception", e);
        }
    }

    public void keepRunning() {
        run();
    }
}