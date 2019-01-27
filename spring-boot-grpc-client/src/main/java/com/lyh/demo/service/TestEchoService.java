package com.lyh.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lyh.api.model.EchoReq;
import com.lyh.api.model.EchoResp;
import com.lyh.rpc.EchoServiceGrpc.EchoServiceBlockingStub;
import io.grpc.StatusRuntimeException;

public class TestEchoService {
  private Logger logger = LoggerFactory.getLogger(TestEchoService.class);

  public void test(EchoServiceBlockingStub stub) {
    try {
      EchoReq req = EchoReq.newBuilder().setData("测试消息").build();
      EchoResp response = stub.hello(req);
      System.out.println("[客户端] " + response.getReply());
    } catch (StatusRuntimeException e) {
      logger.error("RPC failed: {0}", e.getStatus());
      return;
    }
  }
}
