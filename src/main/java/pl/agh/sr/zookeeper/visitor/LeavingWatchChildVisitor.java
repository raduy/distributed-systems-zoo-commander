package pl.agh.sr.zookeeper.visitor;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class LeavingWatchChildVisitor implements ChildVisitor {
    private static final Logger LOG = getLogger(LeavingWatchChildVisitor.class);

    private ZooKeeper zooKeeper;
    private Watcher watcher;

    public LeavingWatchChildVisitor(ZooKeeper zooKeeper, Watcher watcher) {
        this.zooKeeper = zooKeeper;
        this.watcher = watcher;
    }

    @Override
    public void visit(String child) {
        try {
            zooKeeper.getChildren(child, watcher);
        } catch (KeeperException | InterruptedException e) {
            LOG.error("Error on leaving child watch on node: {}", child);
        }
    }

    @Override
    public void beforeChildren() {
    }

    @Override
    public void afterChildren() {
    }

    @Override
    public void terminate() {
    }
}
