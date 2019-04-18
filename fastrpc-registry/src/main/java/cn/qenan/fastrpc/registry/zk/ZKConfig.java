package cn.qenan.fastrpc.registry.zk;

public interface ZKConfig {
    int ZK_SESSION_TIMEOUT_DEFAULT = 5000;
    int ZK_CONNECTION_TIMEOUT_DEFAULT = 1000;
    String ZK_REGISTRY_PATH = "/fastrpc";
}
