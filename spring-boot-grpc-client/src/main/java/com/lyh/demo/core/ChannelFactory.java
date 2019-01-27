package com.lyh.demo.core;

import java.util.concurrent.TimeUnit;
import com.lyh.demo.resolve.ZkNameResolverProvider;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ChannelFactory {
  private final ManagedChannel channel;

  public ChannelFactory(String zkAddr) {
    this.channel = ManagedChannelBuilder.forTarget(zkAddr)//
        .defaultLoadBalancingPolicy("round_robin")//
        .nameResolverFactory(new ZkNameResolverProvider())//
        .usePlaintext().build();

    // this(ManagedChannelBuilder.forTarget(zkAddr)//
    // .loadBalancerFactory(RoundRobinLoadBalancerFactory.getInstance())//
    // .nameResolverFactory(new ZkNameResolverProvider())//
    // .usePlaintext(true));

    // LoadBalancerRegistry.getDefaultRegistry().register(new Provider());
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  public Channel getChannel() {
    return this.channel;
  }

}
