package pl.agh.sr.zookeeper.connection;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class ConnectionChangeWatcher implements Watcher {
    private static final Logger LOG = getLogger(ConnectionChangeWatcher.class);

    public void process(WatchedEvent event) {
        LOG.info("Connection state changed! Received event! {}", event);

        switch (event.getState()) {
            case SyncConnected:
                LOG.info("Client connected {}", event.toString());
                break;
            case Disconnected:
                LOG.info("Client disconnected {}", event.toString());
                break;

            default:
                LOG.info("Connection state event: {}", event.toString());
        }
    }
}
