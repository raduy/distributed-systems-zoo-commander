package pl.agh.sr.zookeeper.nodemonitor;

import org.apache.zookeeper.ZooKeeper;
import pl.agh.sr.zookeeper.Callback;
import pl.agh.sr.zookeeper.visitor.ChildVisitor;

import java.util.function.Supplier;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class NodeChangeWatcherBuilder {
    private Callback onNodeCreated;
    private Callback onNodeDeleted;
    private Supplier<ChildVisitor> childVisitor;

    NodeChangeWatcherBuilder() {
    }

    public NodeChangeWatcherBuilder onNodeCreated(Callback callback) {
        this.onNodeCreated = callback;
        return this;
    }

    public NodeChangeWatcherBuilder onNodeDeleted(Callback callback) {
        this.onNodeDeleted = callback;
        return this;
    }

    public NodeChangeWatcherBuilder onChildrenChanged(Supplier<ChildVisitor> childVisitor) {
        this.childVisitor = childVisitor;
        return this;
    }

    public NodeChangeWatcher listen(String zNode, ZooKeeper zooKeeper) {
        NodeChangeWatcher nodeChangeWatcher = new NodeChangeWatcher(zNode, zooKeeper, onNodeCreated, onNodeDeleted, childVisitor);
        nodeChangeWatcher.beginListening(zNode);

        return nodeChangeWatcher;
    }
}
