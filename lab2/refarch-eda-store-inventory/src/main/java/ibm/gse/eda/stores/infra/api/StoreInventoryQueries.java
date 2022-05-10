package ibm.gse.eda.stores.infra.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import ibm.gse.eda.stores.domain.StoreInventory;
import ibm.gse.eda.stores.domain.StoreInventoryAggregator;
import ibm.gse.eda.stores.infra.api.dto.InventoryQueryResult;
import ibm.gse.eda.stores.infra.api.dto.PipelineMetadata;

@ApplicationScoped
public class StoreInventoryQueries {

    private static final Logger LOG = Logger.getLogger(StoreInventoryQueries.class);
    
    @ConfigProperty(name = "hostname")
    String host;

    @Inject
    KafkaStreams kafkaStreams;

    public List<PipelineMetadata> getStoreInventoryStoreMetadata() {
        return kafkaStreams.allMetadataForStore(StoreInventoryAggregator.STORE_INVENTORY_KAFKA_STORE_NAME)
                .stream()
                .map(m -> new PipelineMetadata(
                        m.hostInfo().host() + ":" + m.hostInfo().port(),
                        m.topicPartitions()
                                .stream()
                                .map(TopicPartition::toString)
                                .collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    public InventoryQueryResult getStoreStock(String storeID) {
        KeyQueryMetadata metadata = null;
        LOG.warnv("Search metadata for key {0}", storeID);
        try {
            metadata = kafkaStreams.queryMetadataForKey(
                StoreInventoryAggregator.STORE_INVENTORY_KAFKA_STORE_NAME,
                storeID,
                Serdes.String().serializer());
        } catch (Exception e) {
            e.printStackTrace();
            return InventoryQueryResult.notFound();
        }
        if (metadata == null || metadata == KeyQueryMetadata.NOT_AVAILABLE) {
            LOG.warnv("Found no metadata for key {0}", storeID);
            return InventoryQueryResult.notFound();
        } else if (metadata.getActiveHost().host().equals(host)) {
            LOG.infov("Found data for key {0} locally", storeID);
            StoreInventory result = getInventoryStockStore().get(storeID);

            if (result != null) {
                return InventoryQueryResult.found(result);
            } else {
                return InventoryQueryResult.notFound();
            }
        } else {
            LOG.infov("Found data for key {0} on remote host {1}:{2}", storeID, metadata.getActiveHost().host(), metadata.getActiveHost().port());
            return InventoryQueryResult.foundRemotely(metadata.getActiveHost());
        }
    }

    private ReadOnlyKeyValueStore<String, StoreInventory> getInventoryStockStore() {
        while (true) {
            try {
                StoreQueryParameters<ReadOnlyKeyValueStore<String,StoreInventory>> parameters = StoreQueryParameters.fromNameAndType(StoreInventoryAggregator.STORE_INVENTORY_KAFKA_STORE_NAME,QueryableStoreTypes.keyValueStore());
                return kafkaStreams.store(parameters);
             } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }
}