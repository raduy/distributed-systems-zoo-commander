package pl.agh.sr.zookeeper.nodemonitor;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;

import java.util.function.Supplier;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class NodeChangeWatcher implements Watcher {
    private static final Logger LOG = getLogger(NodeChangeWatcher.class);
    private final String zNode;
    private final ZooKeeper zooKeeper;
    private final Supplier<Boolean> onNodeCreated;
    private final Supplier<Boolean> onNodeDeleted;

    NodeChangeWatcher(String zNode,
                             ZooKeeper zooKeeper,
                             Supplier<Boolean> onNodeCreated,
                             Supplier<Boolean> onNodeDeleted) {
        this.zNode = zNode;
        this.zooKeeper = zooKeeper;
        this.onNodeCreated = onNodeCreated;
        this.onNodeDeleted = onNodeDeleted;
    }

    public static NodeChangeWatcherBuilder builder() {
        return new NodeChangeWatcherBuilder();
    }

    @Override
    public void process(WatchedEvent event) {
        LOG.info("Watching node changed: {}", event);

        switch (event.getType()) {
            case NodeCreated:
                LOG.info("{} created! Running executable", this.zNode);
                onNodeCreated.get();
                break;
            case NodeDeleted:
                LOG.info("{} deleted! Stopping executable", this.zNode);
                onNodeDeleted.get();
                break;
        }

        leaveWatcher();
    }

    void leaveWatcher() {
        try {
            zooKeeper.exists(this.zNode, this);
            LOG.info("Watcher left on zNode: {}", this.zNode);
        } catch (KeeperException e) {
            LOG.error("Server signaled error!", e);
        } catch (InterruptedException e) {
            LOG.error("Interrupted exception!", e);
        }
    }
}
