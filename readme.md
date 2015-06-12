1. Run ZooKeeper server:
$ bin/zkServer.cmd

2. Run ZooKeeper cmd client:
$ bin/zkCli.cmd -server 127.0.0.1:2181

3. Run commander: (Notepad++ as example executable)
$ java -jar zoo-commander-1.0-jar-with-dependencies.jar 127.0.0.1:2182 /znode_test "C:\Program Files (x86)\Notepad++\notepad++.exe"

4. Create zNode using cmd client:
$ create /znode_test sample_data

5. Notepad++ should be running!