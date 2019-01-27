package com.lyh.demo.core;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZookeeperConnection {
  private static final Logger logger = Logger.getLogger("ZooKeeper");
  private ZooKeeper zoo;
  private final String zkUri;

  public ZookeeperConnection(String zkUri) {
    Objects.requireNonNull(zkUri, "`zkUri`参数不能为空");
    this.zkUri = zkUri;
    this.initZKConnec();
  }

  /**
   * 初始化zookeeper链接
   */
  private void initZKConnec() {
    String zkhostport;
    try {
      URI uri = new URI(zkUri);
      zkhostport = uri.getHost().toString() + ":" + Integer.toString(uri.getPort());
      final CountDownLatch connectedSignal = new CountDownLatch(1);
      try {
        zoo = new ZooKeeper(zkhostport, 5000, new Watcher() {
          public void process(WatchedEvent we) {
            if (we.getState() == KeeperState.SyncConnected) {
              connectedSignal.countDown();
            }
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
      /* Wait for zookeeper connection */
      connectedSignal.await();
    } catch (Exception e) {
      logger.severe("Could not parse zk URI " + zkUri);
    }
  }

  /**
   * @param serverIp gRPC Provider server IP
   * @param portStr gRPC Provider server PORT
   */
  public boolean connect(String serverIp, int port) throws IOException, InterruptedException {
    String path = "/grpc_hello_world_service";
    Stat stat;
    String currTime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    try {
      stat = zoo.exists(path, true);
      if (stat == null) {
        zoo.create(path, currTime.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
      }
    } catch (Exception e) {
      logger.severe("Failed to create path");
      return false;
    }

    String server_addr = path + "/" + serverIp + ":" + port;
    try {
      stat = zoo.exists(server_addr, true);
      if (stat == null) {
        try {
          zoo.create(server_addr, currTime.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
              CreateMode.EPHEMERAL);
        } catch (Exception e) {
          logger.severe("Failed to create server_data");
          return false;
        }
      } else {
        try {
          zoo.setData(server_addr, currTime.getBytes(), stat.getVersion());
        } catch (Exception e) {
          logger.severe("Failed to update server_data");
          return false;
        }
      }
    } catch (Exception e) {
      logger.severe("Failed to add server_data");
      return false;
    }
    return true;
  }

  // Method to disconnect from zookeeper server
  public void close() throws InterruptedException {
    zoo.close();
  }
}
