package pl.agh.sr.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class NodeChangeWatcher implements Watcher {
    private static final Logger LOG = getLogger(NodeChangeWatcher.class);
    private final String zNode;
    private final Executable executable;
    private final ZooKeeper zooKeeper;

    public NodeChangeWatcher(String zNode, Executable executable, ZooKeeper zooKeeper) {
        this.zNode = zNode;
        this.executable = executable;
        this.zooKeeper = zooKeeper;
    }

    @Override
    public void process(WatchedEvent event) {
        LOG.info(" {}", event);

        switch (event.getType()) {
            case NodeCreated:
                LOG.info("{} created! Running executable", this.zNode);
                executable.start();
                break;
            case NodeDeleted:
                LOG.info("{} deleted! Stopping executable", this.zNode);
                executable.stop();
        }

        addWatcher();
    }

    private void addWatcher() {
        try {
            zooKeeper.exists(this.zNode, this);
        } catch (KeeperException e) {
            LOG.error("Server signaled error!", e);
        } catch (InterruptedException e) {
            LOG.error("Interrupted exception!", e);
        }
    }
}
