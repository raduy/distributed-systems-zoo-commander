package pl.agh.sr.zookeeper.connection;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * Creates and maintain connection with ZooKeeper server.
 *
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class ConnectionHolder implements AutoCloseable {
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
        this.zooKeeper.close();
    }
}