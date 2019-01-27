package com.lyh.demo.service;

import com.lyh.api.model.EchoReq;
import com.lyh.api.model.EchoResp;
import com.lyh.rpc.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;

public class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
  @Override
  public void hello(EchoReq request, StreamObserver<EchoResp> responseObserver) {
    System.out.println("[服务端日志] " + request.getData());
    EchoResp resp = EchoResp.newBuilder()
        .setReply(request.getData() + "->Server port:" + "   " + System.currentTimeMillis())
        .build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
}
