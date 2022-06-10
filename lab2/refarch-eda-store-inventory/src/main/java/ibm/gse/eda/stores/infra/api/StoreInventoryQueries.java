package ibm.gse.eda.stores.infra.api;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import ibm.gse.eda.stores.domain.StoreInventory;
import ibm.gse.eda.stores.domain.StoreInventoryAggregator;
import ibm.gse.eda.stores.infra.api.dto.InventoryQueryResult;
import ibm.gse.eda.stores.infra.api.dto.PipelineMetadata;
import ibm.gse.eda.stores.infra.events.ItemProcessingAgent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class StoreInventoryQueries {

    private static final Logger LOG = Logger.getLogger(StoreInventoryQueries.class.getName());
    
    @ConfigProperty(name = "hostname")
    String host;

    @Inject
    private ItemProcessingAgent itemProcessingAgent;

    public List<PipelineMetadata> getStoreInventoryStoreMetadata() {
        return itemProcessingAgent.getKafkaStreams().allMetadataForStore(StoreInventoryAggregator.STORE_INVENTORY_KAFKA_STORE_NAME)
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
        LOG.info("Search metadata for key " + storeID);
        try {
            metadata = itemProcessingAgent.getKafkaStreams().queryMetadataForKey(
                StoreInventoryAggregator.STORE_INVENTORY_KAFKA_STORE_NAME,
                storeID,
                Serdes.String().serializer());
        } catch (Exception e) {
            e.printStackTrace();
            return InventoryQueryResult.notFound();
        }
        if (metadata == null || metadata == KeyQueryMetadata.NOT_AVAILABLE) {
            LOG.info("Found no metadata for key " + storeID);
            return InventoryQueryResult.notFound();
        } else if (metadata.getActiveHost().host().equals(host)) {
            LOG.info("Found data for key {0} locally" + storeID);
            StoreInventory result = getInventoryStockStore().get(storeID);

            if (result != null) {
                return InventoryQueryResult.found(result);
            } else {
                return InventoryQueryResult.notFound();
            }
        } else {
            LOG.info("Found data for key " + storeID + " on remote host " + metadata.getActiveHost().host() + ":" + metadata.getActiveHost().port());
            return InventoryQueryResult.foundRemotely(metadata.getActiveHost());
        }
    }

    private ReadOnlyKeyValueStore<String, StoreInventory> getInventoryStockStore() {
        while (true) {
            try {
                StoreQueryParameters<ReadOnlyKeyValueStore<String,StoreInventory>> parameters = StoreQueryParameters.fromNameAndType(StoreInventoryAggregator.STORE_INVENTORY_KAFKA_STORE_NAME,QueryableStoreTypes.keyValueStore());
                return itemProcessingAgent.getKafkaStreams().store(parameters);
             } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }
}