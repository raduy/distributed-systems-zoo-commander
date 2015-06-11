package pl.agh.sr.zookeeper.nodemonitor;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import pl.agh.sr.zookeeper.ChildrenTraversor;
import pl.agh.sr.zookeeper.visitor.ChildVisitor;
import pl.agh.sr.zookeeper.visitor.LeavingWatchChildVisitor;

import java.util.Optional;
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
    private final Supplier<ChildVisitor> childVisitorSupplier;
    private final ChildrenTraversor childrenTraversor = new ChildrenTraversor();


    NodeChangeWatcher(String zNode,
                      ZooKeeper zooKeeper,
                      Supplier<Boolean> onNodeCreated,
                      Supplier<Boolean> onNodeDeleted,
                      Supplier<ChildVisitor> childVisitorSupplier) {

        this.zNode = zNode;
        this.zooKeeper = zooKeeper;
        this.onNodeCreated = onNodeCreated;
        this.onNodeDeleted = onNodeDeleted;
        this.childVisitorSupplier = childVisitorSupplier;
    }

    public static NodeChangeWatcherBuilder builder() {
        return new NodeChangeWatcherBuilder();
    }

    @Override
    public void process(WatchedEvent event) {
        LOG.info("Watching node changed: {}", event);

        switch (event.getType()) {
            case NodeCreated:
                LOG.info("{} created! Running executable", zNode);
                onNodeCreated.get();
                break;
            case NodeDeleted:
                LOG.info("{} deleted! Stopping executable", zNode);
                onNodeDeleted.get();
                break;
            case NodeChildrenChanged:
                LOG.info("Children changed for zNode: {}", zNode);
                ChildVisitor visitor = childVisitorSupplier.get();
                childrenTraversor.walk(zooKeeper, zNode, visitor);
                visitor.terminate();

                LeavingWatchChildVisitor watchChildVisitor = new LeavingWatchChildVisitor(zooKeeper, this);
                childrenTraversor.walk(zooKeeper, zNode, watchChildVisitor);
                break;
        }

        leaveWatcher(event.getPath());
    }

    Optional<Stat> leaveWatcher(String path) {
        try {
            Stat exists = zooKeeper.exists(path, this);
            zooKeeper.getChildren(path, this);
            LOG.info("Watcher left on zNode: {}", path);
            return Optional.ofNullable(exists);
        } catch (KeeperException e) {
            LOG.error("Server signaled error!", e);
        } catch (InterruptedException e) {
            LOG.error("Interrupted exception!", e);
        }
        return Optional.empty();
    }

    public void beginListening(String zNode) {
        Optional<Stat> nodeOptional = leaveWatcher(zNode);

        if (nodeOptional.isPresent()) {
            onNodeCreated.get();
        }
    }
}
