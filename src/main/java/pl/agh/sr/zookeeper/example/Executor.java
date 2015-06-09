package pl.agh.sr.zookeeper.example;
/**
 * A simple example program to use DataMonitor to start and
 * stop executables based on a znode. The program watches the
 * specified znode and saves the data that corresponds to the
 * znode in the filesystem. It also starts the specified program
 * with the specified arguments when the znode exists and kills
 * the program if the znode goes away.
 */

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import pl.agh.sr.zookeeper.ConnectionChangeWatcher;

import java.io.FileOutputStream;
import java.io.IOException;

public class Executor implements Runnable, DataMonitor.DataMonitorListener {
    private DataMonitor dataMonitor;
    private String filename;
    private String exec[];
    private Process child;

    public Executor(String hostPort, String znode, String filename, String exec[])
            throws KeeperException, IOException {

        this.filename = filename;
        this.exec = exec;
        ConnectionChangeWatcher connectionChangeWatcher = new ConnectionChangeWatcher();
        ZooKeeper zooKeeper = new ZooKeeper(hostPort, 3000, connectionChangeWatcher);
        dataMonitor = new DataMonitor(zooKeeper, znode, null, this);
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("USAGE: Executor hostPort znode filename program [args ...]");
        }

        String hostPort = args[0];
        String znode = args[1];
        String filename = args[2];
        String[] exec = prepareExecutableArgs(args);

        startExecutor(hostPort, znode, filename, exec);
    }

    private static void startExecutor(String hostPort, String znode, String filename, String[] exec) {
        try {
            new Executor(hostPort, znode, filename, exec).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] prepareExecutableArgs(String[] args) {
        String exec[] = new String[args.length - 3];
        System.arraycopy(args, 3, exec, 0, exec.length);
        return exec;
    }

    public void run() {
        try {
            synchronized (this) {
                while (!dataMonitor.dead) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }

//    static class StreamWriter extends Thread {
//        OutputStream os;
//        InputStream is;
//
//        StreamWriter(InputStream is, OutputStream os) {
//            this.is = is;
//            this.os = os;
//            start();
//        }
//
//        public void run() {
//            byte b[] = new byte[80];
//            int rc;
//            try {
//                while ((rc = is.read(b)) > 0) {
//                    os.write(b, 0, rc);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }

    public void exists(byte[] data) {
        if (data == null) {
            if (child != null) {
                System.out.println("Killing process");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            child = null;
        } else {
            if (child != null) {
                System.out.println("Stopping child");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                FileOutputStream fos = new FileOutputStream(filename);
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                System.out.println("Starting child");
                child = Runtime.getRuntime().exec(exec);
//                new StreamWriter(child.getInputStream(), System.out);
//                new StreamWriter(child.getErrorStream(), System.err);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}