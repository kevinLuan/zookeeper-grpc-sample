package com.lyh.grpc.client.api;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.lyh.api.model.EchoReq;
import com.lyh.api.model.EchoResp;
import com.lyh.grpc.resolve.ZkNameResolverProvider;
import com.lyh.rpc.EchoServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.util.RoundRobinLoadBalancerFactory;

public class HelloWorldClient {

  private static final Logger logger = Logger.getLogger("Client");

  private final ManagedChannel channel;
  private final EchoServiceGrpc.EchoServiceBlockingStub blockingStub;

  /**
   * Construct client connecting to HelloWorld server using Zookeeper name resolver and Round Robin
   * load balancer.
   */
  public HelloWorldClient(String zkAddr) {
    this(ManagedChannelBuilder.forTarget(zkAddr)//
        .loadBalancerFactory(RoundRobinLoadBalancerFactory.getInstance())//
        .nameResolverFactory(new ZkNameResolverProvider())//
        .usePlaintext(true));
  }

  // LoadBalancerRegistry.getDefaultRegistry().register(RoundRobinLoadBalancerFactory.getInstance());

  public ManagedChannelBuilder definedDefault(String zkAddr) {
    return ManagedChannelBuilder.forTarget(zkAddr)//
        .defaultLoadBalancingPolicy("pick_first")//
        .nameResolverFactory(new ZkNameResolverProvider())//
        .usePlaintext();
  }

  /** Construct client for accessing the server using the existing channel. */
  HelloWorldClient(ManagedChannelBuilder<?> channelBuilder) {
    channel = channelBuilder.build();
    blockingStub = EchoServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public void hello() {
    try {
      EchoReq req = EchoReq.newBuilder().setData("测试消息").build();
      EchoResp response = blockingStub.hello(req);
      System.out.println("服务端返回:" + response.getReply());
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
  }

  public static void main(String[] args) throws Exception {
    HelloWorldClient client = new HelloWorldClient("zk://127.0.0.1:2181");
    try {
      while (true) {
        client.hello();
        Thread.sleep(1000);
      }
    } finally {
      client.shutdown();
    }
  }

}
