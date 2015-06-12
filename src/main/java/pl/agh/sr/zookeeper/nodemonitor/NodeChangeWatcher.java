package pl.agh.sr.zookeeper.nodemonitor;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import pl.agh.sr.zookeeper.Callback;
import pl.agh.sr.zookeeper.nodemonitor.traverse.ChildrenTraversor;
import pl.agh.sr.zookeeper.visitor.ChildVisitor;
import pl.agh.sr.zookeeper.visitor.LeavingWatchChildVisitor;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class NodeChangeWatcher implements Watcher {
    private static final Logger LOG = getLogger(NodeChangeWatcher.class);
    private final String rootNode;
    private final ZooKeeper zooKeeper;
    private final Callback onNodeCreated;
    private final Callback onNodeDeleted;
    private final Supplier<ChildVisitor> childVisitorSupplier;
    private final ChildrenTraversor childrenTraversor = new ChildrenTraversor();


    NodeChangeWatcher(String zNode,
                      ZooKeeper zooKeeper,
                      Callback onNodeCreated,
                      Callback onNodeDeleted,
                      Supplier<ChildVisitor> childVisitorSupplier) {

        this.rootNode = zNode;
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

        String path = event.getPath();
        switch (event.getType()) {
            case NodeCreated:
                if (rootNode.equals(path)) {
                    LOG.info("{} created! Running executable", rootNode);
                    onNodeCreated.invoke();
                    leaveWatcher(rootNode);
                }
                return;
            case NodeDeleted:
                if (rootNode.equals(path)) {
                    LOG.info("{} deleted! Stopping executable", rootNode);
                    onNodeDeleted.invoke();
                    leaveWatcher(rootNode);
                }
                return;
            case NodeChildrenChanged:
                LOG.info("Children changed for zNode: {}", path);
                runVisitor();
                leaveWatcherOnChildren(path);
        }
    }

    Optional<Stat> leaveWatcher(String path) {
        try {
            Stat exists = zooKeeper.exists(path, this);
            if (nonNull(exists)) {
                leaveWatcherOnChildren(path);
            }
            LOG.info("Watcher left on zNode: {}", path);
            return Optional.ofNullable(exists);
        } catch (KeeperException e) {
            LOG.error("Server signaled error!", e);
        } catch (InterruptedException e) {
            LOG.error("Interrupted exception!", e);
        }
        return Optional.empty();
    }

    private void leaveWatcherOnChildren(String zNode) {
        LOG.info("Leaving child watch for path: {}", zNode);
        LeavingWatchChildVisitor watchChildVisitor = new LeavingWatchChildVisitor(zooKeeper, this);
        childrenTraversor.walk(zooKeeper, zNode, watchChildVisitor);
    }

    private void runVisitor() {
        ChildVisitor visitor = childVisitorSupplier.get();
        childrenTraversor.walk(zooKeeper, rootNode, visitor);
        visitor.terminate();
    }

    public void beginListening(String zNode) {
        Optional<Stat> nodeOptional = leaveWatcher(zNode);

        if (nodeOptional.isPresent()) {
            onNodeCreated.invoke();
        }
    }
}
