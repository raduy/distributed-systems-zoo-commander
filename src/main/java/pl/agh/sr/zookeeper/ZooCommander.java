package pl.agh.sr.zookeeper;

import org.apache.zookeeper.ZooKeeper;
import pl.agh.sr.zookeeper.connection.ConnectionHolder;
import pl.agh.sr.zookeeper.nodemonitor.NodeChangeWatcher;
import pl.agh.sr.zookeeper.nodemonitor.traverse.ChildrenTraversor;
import pl.agh.sr.zookeeper.visitor.TreePrintingChildVisitor;

import java.io.IOException;
import java.util.Scanner;

/**
 * Main cmd app.
 * Usage: java ZooCommander <connection-string of form ip:port[,ip:port]> "
 * "<app-executable (.exe)> "
 * "<z-node name e.g. /znode_test>"
 * "[<app-executable-arg> [,<app-executable-arg>]]"
 */
public class ZooCommander {

    public static void main(String[] args) {
        if (args.length < 3) {
            printUsage();
            return;
        }

        String connectionString = args[0];
        String zNode = args[1];

        Executable executable = parseExecutable(args);
        ChildrenTraversor childrenTraversor = new ChildrenTraversor();

        try (ConnectionHolder connectionHolder = new ConnectionHolder(connectionString)) {

            ZooKeeper zooKeeper = connectionHolder.zooKeeper();
            NodeChangeWatcher.builder()
                    .onNodeCreated(executable::start)
                    .onNodeDeleted(executable::stop)
                    .onChildrenChanged(TreePrintingChildVisitor::new)
                    .listen(zNode, zooKeeper);

            listenForCommands(zNode, childrenTraversor, zooKeeper);
        } catch (IOException e) {
            System.out.println("Cannot connect to server! Try again" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Problem running commander:" + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error occurred! Cause:" + e.getMessage());
        }
    }

    private static void listenForCommands(String zNode, ChildrenTraversor childrenTraversor, ZooKeeper zooKeeper) {
        Scanner scanner = new Scanner(System.in);
        while (!Thread.interrupted()) {
            System.out.print("$");
            String cmd = scanner.nextLine();
            if (cmd.isEmpty()) {
                continue;
            }

            if (cmd.startsWith("ls")) {
                TreePrintingChildVisitor printingChildVisitor = new TreePrintingChildVisitor();
                childrenTraversor.walk(zooKeeper, zNode, printingChildVisitor);

                System.out.println(printingChildVisitor.threeString());
            } else {
                System.out.println("Hit 'ls' to print zNode tree");
            }
        }
    }

    private static Executable parseExecutable(String[] args) {
        String executableName = args[2];

        String[] executableArgs = new String[args.length - 3];
        System.arraycopy(args, 3, executableArgs, 0, args.length - 3);

        return new Executable(executableName, executableArgs);
    }

    private static void printUsage() {
        String usage = "Usage: java ZooCommander <connection-string of form ip:port[,ip:port]> " +
                "<app-executable (.exe)> " +
                "<z-node name e.g. znode_testowy>" +
                "[<app-executable-arg> [,<app-executable-arg>]]";
        System.out.println(usage);
    }
}