package ibm.gse.eda.stores.infra.api.dto;

import java.util.Optional;
import java.util.OptionalInt;

import org.apache.kafka.streams.state.HostInfo;

public class ItemCountQueryResult {
    private static ItemCountQueryResult NOT_FOUND = new ItemCountQueryResult(null, null, null);
    private final Long result;
    private final String host;
    private final Integer port;

    public ItemCountQueryResult(){
        result = 0L;
        host="localhost";
        port=8080;
    }

    public ItemCountQueryResult(Long result, String host, Integer port) {
        this.result = result;
        this.host = host;
        this.port = port;
    }

    public static ItemCountQueryResult notFound() {
        return NOT_FOUND;
    }

    public static ItemCountQueryResult found(Long data) {
        return new ItemCountQueryResult(data, null, null);
    }

    public static ItemCountQueryResult foundRemotely(HostInfo host) {
        return new ItemCountQueryResult(null, host.host(), host.port());
    }

    public Optional<Long> getResult() {
        return Optional.ofNullable(result);
    }

    public Optional<String> getHost() {
        return Optional.ofNullable(host);
    }

    public OptionalInt getPort() {
        return port != null ? OptionalInt.of(port) : OptionalInt.empty();
    }
}