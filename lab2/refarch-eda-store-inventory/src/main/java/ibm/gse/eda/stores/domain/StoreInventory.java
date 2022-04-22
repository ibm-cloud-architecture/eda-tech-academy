package ibm.gse.eda.stores.domain;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Represents the store inventory in the form
 * Store -> [<item_id,quantity>,....]
 */
@RegisterForReflection
public class StoreInventory  {
    
    public String storeName;
    // map <item_id,quantity>
    public HashMap<String,Long> stock = new HashMap<String,Long>();

    public StoreInventory(){}

    public StoreInventory(String storeName) {
        this.storeName = storeName;
    }

    public StoreInventory(String storeName, String sku, int quantity) {
        this.storeName = storeName;
        this.updateStock(sku, quantity);
    }

    public StoreInventory updateStockQuantity(String key, ItemTransaction newValue) {
        this.storeName = key;
        if (newValue.type != null && ItemTransaction.SALE.equals(newValue.type))
            newValue.quantity=-newValue.quantity;
        return this.updateStock(newValue.sku,newValue.quantity);
    }

    public StoreInventory updateStock(String sku, long newV) {
        if (stock.get(sku) == null) {
            stock.put(sku, Long.valueOf(newV));
        } else {
            Long currentValue = stock.get(sku);
            stock.put(sku, Long.valueOf(newV) + currentValue );
        }
        return this;
    }

    public String toString(){
        String s = "{ storeID: " + storeName + ",";
        for (Map.Entry<String, Long> entry : stock.entrySet()) {
           s = s + entry.getKey() + ": " + entry.getValue() + ",";
        }
        return s;
    }
}