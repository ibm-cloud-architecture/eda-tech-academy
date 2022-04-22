package ibm.gse.eda.stores.infra.events;

import org.apache.kafka.common.serialization.Serde;

import ibm.gse.eda.stores.domain.ItemTransaction;
import ibm.gse.eda.stores.domain.StoreInventory;

public class StoreSerdes {
    
    public static Serde<ItemTransaction> ItemTransactionSerde() {
        return new JSONSerde<ItemTransaction>(ItemTransaction.class.getCanonicalName());
    }

    public static Serde<StoreInventory> StoreInventorySerde() {
        return new JSONSerde<StoreInventory>(StoreInventory.class.getCanonicalName());
    }
}
