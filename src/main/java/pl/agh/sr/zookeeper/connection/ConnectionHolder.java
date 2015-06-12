package pl.agh.sr.zookeeper.connection;

import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Creates and maintain connection with ZooKeeper server.
 *
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class ConnectionHolder implements AutoCloseable {
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

    @Override
    public void close() throws Exception {
        LOG.info("Closing connection to server");
        this.zooKeeper.close();
    }
}