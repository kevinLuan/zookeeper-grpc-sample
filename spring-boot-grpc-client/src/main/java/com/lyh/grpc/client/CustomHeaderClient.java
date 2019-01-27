package com.lyh.grpc.client;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.lyh.api.model.EchoReq;
import com.lyh.api.model.EchoResp;
import com.lyh.rpc.EchoServiceGrpc;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/**
 * A simple client that like {@link com.lyh.rpc.EchoServiceGrpc}. This client can help you create
 * custom headers.
 */
public class CustomHeaderClient {
  private static final Logger logger = Logger.getLogger(CustomHeaderClient.class.getName());

  private final ManagedChannel originChannel;
  private final EchoServiceGrpc.EchoServiceBlockingStub blockingStub;

  /**
   * A custom client.
   */
  private CustomHeaderClient(String host, int port) {
    originChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
    ClientInterceptor interceptor = new HeaderClientInterceptor();
    Channel channel = ClientInterceptors.intercept(originChannel, interceptor);
    blockingStub = EchoServiceGrpc.newBlockingStub(channel);
  }

  private void shutdown() throws InterruptedException {
    originChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /**
   * A simple client method that like {@link com.lyh.rpc.EchoServiceGrpc}.
   */
  private void greet(String name) {
    logger.info("Will try to greet " + name + " ...");
    EchoResp response = null;
    try {
      EchoReq req = EchoReq.newBuilder().setData("mkdir -p /root/name").build();
      response = blockingStub.hello(req);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("Greeting: " + response);
  }

  /**
   * Main start the client from the command line.
   */
  public static void main(String[] args) throws Exception {
    CustomHeaderClient client = new CustomHeaderClient("localhost", 50051);
    try {
      /* Access a service running on the local machine on port 50051 */
      String user = "world";
      if (args.length > 0) {
        user = args[0]; /* Use the arg as the name to greet if provided */
      }
      client.greet(user);
    } finally {
      client.shutdown();
    }
  }
}
