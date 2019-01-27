package com.lyh.demo.resolve;

import java.net.URI;
import javax.annotation.Nullable;
import io.grpc.Attributes;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

public class ZkNameResolverProvider extends NameResolverProvider {
  @Nullable
  @Override
  public NameResolver newNameResolver(URI targetUri, Attributes params) {
    return new ZkNameResolver(targetUri);
  }

  @Override
  protected int priority() {
    return 5;
  }

  @Override
  protected boolean isAvailable() {
    return true;
  }

  @Override
  public String getDefaultScheme() {
    return "zk";
  }
}
