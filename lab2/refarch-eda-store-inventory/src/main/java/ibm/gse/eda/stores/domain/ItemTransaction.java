package ibm.gse.eda.stores.domain;

import java.time.LocalDateTime;
import java.util.Date;


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
                this.id = new Date().getTime();
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

        public String toString(){
                StringBuffer sb = new StringBuffer();
                sb.append("id: " + id);
                sb.append(" Store: " + storeName);
                sb.append(" Item: " + sku);
                sb.append(" Type: " + type);
                sb.append(" Quantity: " + quantity);
                return sb.toString(); 
        }
}