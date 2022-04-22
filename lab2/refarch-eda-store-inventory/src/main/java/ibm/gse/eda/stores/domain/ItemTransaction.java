package ibm.gse.eda.stores.domain;

import java.time.LocalDateTime;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ItemTransaction   {
        public static String RESTOCK = "RESTOCK";
        public static String SALE = "SALE";
        public Long id;
        public String storeName;
        public String sku;
        public int quantity;
        public String type;
        public Double price;
        public String timestamp;

        public ItemTransaction() {
        }

        public ItemTransaction(String store, String sku, String type, int quantity, double price) {
                this.storeName = store;
                this.sku = sku;
                this.type = type;
                this.quantity = quantity;
                this.price = price;
                this.timestamp = LocalDateTime.now().toString();
        }

        public ItemTransaction(String store, String sku, String type, int quantity) {
                this.storeName = store;
                this.sku = sku;
                this.type = type;
                this.quantity = quantity;
                this.timestamp = LocalDateTime.now().toString();
        }
}