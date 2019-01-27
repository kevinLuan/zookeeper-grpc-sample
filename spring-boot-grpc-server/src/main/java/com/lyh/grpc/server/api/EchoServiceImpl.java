package com.lyh.grpc.server.api;

import com.lyh.api.model.EchoReq;
import com.lyh.api.model.EchoResp;
import com.lyh.grpc.server.zk.HelloWorldServer;
import com.lyh.rpc.EchoServiceGrpc;
import io.grpc.stub.StreamObserver;

public class EchoServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {
  @Override
  public void hello(EchoReq request, StreamObserver<EchoResp> responseObserver) {
    System.out.println("client:" + request.getData());
    EchoResp resp = EchoResp.newBuilder().setReply(request.getData() + "->Server port:"
        + HelloWorldServer.port + "   " + System.currentTimeMillis()).build();
    responseObserver.onNext(resp);
    responseObserver.onCompleted();
  }
}
