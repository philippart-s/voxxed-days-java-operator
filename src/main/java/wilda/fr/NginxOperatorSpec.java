package wilda.fr;

public class NginxOperatorSpec {
    private Integer replicaCount;
    private Integer port;

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

    public void setReplicaCount(Integer replicaCount) {
        this.replicaCount = replicaCount;
    }

    public Integer getReplicaCount() {
        return replicaCount;
    }
}
