package pl.agh.sr.zookeeper;

import java.io.IOException;

public class ZooCommander {
    public static void main(String[] args) {
        if (args.length < 2) {
            printUsage();
            return;
        }

        String connectionString = args[0];
        String zNode = args[1];

        Executable executable = parseExecutable(args);

        try {
            ZooKeeperClient zooKeeperClient = new ZooKeeperClient(connectionString, zNode, executable);

            zooKeeperClient.run();
        } catch (IOException e) {
            System.out.println("Cannot connect to server! Try again" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Problem running commander:" + e.getMessage());
        }
    }

    private static Executable parseExecutable(String[] args) {
        String executableName = args[2];

        String[] executableArgs = new String[args.length - 2];
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