package pl.agh.sr.zookeeper;

import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

class ZooKeeperClient implements Runnable {
    private static final Logger LOG = getLogger(ZooKeeperClient.class);
    private static final int SESSION_TIMEOUT = 3000;

    public ZooKeeperClient(String connectionString, String zNode, Executable executable) throws IOException {

        ConnectionChangeWatcher connectionChangeWatcher = new ConnectionChangeWatcher();
        ZooKeeper zooKeeper = new ZooKeeper(connectionString, SESSION_TIMEOUT, connectionChangeWatcher);

        NodeMonitor nodeMonitor = new NodeMonitor(zooKeeper);
        nodeMonitor.listen(zNode, executable);
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            LOG.error("Interruption exception", e);
        }
    }
}