package com.lyh.demo;

import java.io.IOException;
import java.util.Random;
import com.lyh.demo.core.GrpcServers;
import com.lyh.demo.service.EchoServiceImpl;

public class StartupServer {
  public static void main(String[] args) throws IOException, InterruptedException {
    String zk_addr = "zk://127.0.0.1:2181";
    int port = 50000;
    port += new Random().nextInt(100);
    final GrpcServers server = new GrpcServers(zk_addr, port);
    server.registerService(new EchoServiceImpl());
    server.start();
    server.blockUntilShutdown();
  }

}
