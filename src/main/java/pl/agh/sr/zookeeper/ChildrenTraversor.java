package pl.agh.sr.zookeeper;

import com.google.common.collect.Ordering;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import pl.agh.sr.zookeeper.visitor.ChildVisitor;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class ChildrenTraversor {
    private static final Logger LOG = getLogger(ChildrenTraversor.class);

    public void walk(ZooKeeper zooKeeper, String zNode, ChildVisitor visitor) {
        visitor.visit(zNode);
        try {
            zooKeeper.getChildren(zNode, false).stream().sorted(Ordering.natural()).forEach(child -> {
                String path = zNode + "/" + child;
                visitor.beforeChildren();
                walk(zooKeeper, path, visitor);
                visitor.afterChildren();
            });
        } catch (KeeperException | InterruptedException e) {
            LOG.error("Cannot walk zNode of path: {}", zNode);
        }
    }
}
