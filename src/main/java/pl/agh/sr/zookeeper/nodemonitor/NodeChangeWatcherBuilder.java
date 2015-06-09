package pl.agh.sr.zookeeper.nodemonitor;

import org.apache.zookeeper.ZooKeeper;

import java.util.function.Supplier;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class NodeChangeWatcherBuilder {
    private Supplier<Boolean> onNodeCreated;
    private Supplier<Boolean> onNodeDeleted;

    NodeChangeWatcherBuilder() {}

    public NodeChangeWatcherBuilder onNodeCreated(Supplier<Boolean> callback) {
        this.onNodeCreated = callback;
        return this;
    }

    public NodeChangeWatcherBuilder onNodeDeleted(Supplier<Boolean> callback) {
        this.onNodeDeleted = callback;
        return this;
    }

    public NodeChangeWatcher listen(String zNode, ZooKeeper zooKeeper) {
        NodeChangeWatcher nodeChangeWatcher = new NodeChangeWatcher(zNode, zooKeeper, onNodeCreated, onNodeDeleted);
        nodeChangeWatcher.leaveWatcher();

        return nodeChangeWatcher;
    }
}
