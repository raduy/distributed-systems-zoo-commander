package pl.agh.sr.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import pl.agh.sr.zookeeper.visitor.ChildVisitor;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class ChildrenTraversor {
    private static final Logger LOG = getLogger(ChildrenTraversor.class);

    public void walk(ZooKeeper zooKeeper, String zNode, ChildVisitor visitor) {
        visitor.beforeParent();

        List<String> children;
        try {
            children = zooKeeper.getChildren(zNode, false);
            children.forEach(child -> {
                visitor.visit(child);

                String path = zNode + "/" + child;
                walk(zooKeeper, path, visitor);
                visitor.afterParent();
            });
        } catch (KeeperException | InterruptedException e) {
            LOG.error("Cannot walk zNode of path: {}", zNode);
        }
    }
}
