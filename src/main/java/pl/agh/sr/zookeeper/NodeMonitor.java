package pl.agh.sr.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class NodeMonitor {
    private static final Logger LOG = getLogger(NodeMonitor.class);
    private final ZooKeeper zooKeeper;

    public NodeMonitor(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void listen(String zNode, Executable executable) {
        try {
            zooKeeper.exists(zNode, new NodeChangeWatcher(zNode, executable, zooKeeper));
        } catch (KeeperException e) {
            LOG.error("Server signaled error!", e);
        } catch (InterruptedException e) {
            LOG.error("Interrupted exception!", e);
        }
    }
}
