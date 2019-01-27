package com.lyh.demo;

import com.lyh.demo.core.ChannelFactory;
import com.lyh.demo.service.TestEchoService;
import com.lyh.rpc.EchoServiceGrpc;
import com.lyh.rpc.EchoServiceGrpc.EchoServiceBlockingStub;

public class StartupClient {
  public static void main(String[] args) throws Exception {
    ChannelFactory factory = new ChannelFactory("zk://127.0.0.1:2181");
    EchoServiceBlockingStub stub = EchoServiceGrpc.newBlockingStub(factory.getChannel());;
    try {
      while (true) {
        new TestEchoService().test(stub);
        Thread.sleep(1000);
      }
    } finally {
      factory.shutdown();
    }
  }

}
