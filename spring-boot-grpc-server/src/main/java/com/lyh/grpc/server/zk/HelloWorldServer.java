package com.lyh.grpc.server.zk;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;
import com.lyh.grpc.server.api.EchoServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class HelloWorldServer {

  public static int port;
  private static final Logger logger = Logger.getLogger(HelloWorldServer.class.getName());

  private Server server;

  private void start(int port) throws IOException {
    /* The port on which the server should run */
    server = ServerBuilder.forPort(port)//
        .addService(new EchoServiceImpl()).build().start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        HelloWorldServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }


  public static void main(String[] args) throws IOException, InterruptedException {
    String zk_addr = "zk://127.0.0.1:2181";
    try {
      port = 50000;
      port += new Random().nextInt(100);
      zk_addr = new String(zk_addr);
    } catch (Exception e) {
      System.out.println("Usage: helloworld_server PORT zk://ADDR:PORT");
      return;
    }
    ZookeeperConnection zk_conn = new ZookeeperConnection();
    if (!zk_conn.connect(zk_addr, "localhost", port)) {
      return;
    }

    final HelloWorldServer server = new HelloWorldServer();
    server.start(port);
    server.blockUntilShutdown();
  }


}
