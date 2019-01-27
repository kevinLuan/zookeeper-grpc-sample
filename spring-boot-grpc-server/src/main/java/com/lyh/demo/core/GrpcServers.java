package com.lyh.demo.core;

import java.io.IOException;
import java.util.logging.Logger;
import com.lyh.demo.utils.NetUtils;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;


public class GrpcServers {
  private final int port;
  private static final Logger logger = Logger.getLogger(GrpcServers.class.getName());
  private String zkAddress;
  private Server server;
  @SuppressWarnings("rawtypes")
  private final ServerBuilder serverBuilder;

  public GrpcServers(String zkAddress, int port) {
    this.zkAddress = zkAddress;
    this.port = port;
    serverBuilder = ServerBuilder.forPort(port);
  }

  /**
   * 注册服务
   * 
   * @param service
   * @return
   */
  public GrpcServers registerService(BindableService service) {
    serverBuilder.addService(service);
    return this;
  }

  public void start() throws IOException, InterruptedException {
    /* The port on which the server should run */
    server = serverBuilder.build();
    server.start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        GrpcServers.this.stop();
        System.err.println("*** server shut down");
      }
    });
    registerInstance();
  }

  private void registerInstance() throws IOException, InterruptedException {
    String host = NetUtils.getLocalHost();
    ZookeeperConnection zk_conn = new ZookeeperConnection(zkAddress);
    if (!zk_conn.connect(host, port)) {
      return;
    }
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }


}
