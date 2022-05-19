package ut;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ibm.gse.eda.stores.domain.ItemTransaction;
import ibm.gse.eda.stores.domain.StoreInventory;

public class TestStoreInventoryLogic {

    @Test
    public void shouldUpdateItemSoldQuantityTo10(){
        ItemTransaction i1 = new ItemTransaction();
        i1.sku = "Item_1";
        i1.type = "SALE";
        i1.quantity = 10;
        StoreInventory inventory = new StoreInventory();
        StoreInventory out = inventory.updateStockQuantity("Store_1",i1);
        Assertions.assertEquals(-10,out.stock.get(i1.sku));
    }

    @Test
    public void shouldUpdateItemRestockQuantityTo10(){
        ItemTransaction i1 = new ItemTransaction();
        i1.sku = "Item_1";
        i1.type = "RESTOCK";
        i1.quantity = 10;
        StoreInventory inventory = new StoreInventory();
        StoreInventory out = inventory.updateStockQuantity("Store_1",i1);
        Assertions.assertEquals(10,out.stock.get(i1.sku));
    }

    @Test
    public void shouldGetRightNumberAfterStockAndResale(){
        ItemTransaction i1 = new ItemTransaction();
        i1.sku = "Item_1";
        i1.type = "RESTOCK";
        i1.quantity = 10;
        StoreInventory inventory = new StoreInventory();
        inventory.updateStockQuantity("Store_1",i1);
        i1.type = "SALE";
        StoreInventory out = inventory.updateStockQuantity("Store_1",i1);
        Assertions.assertEquals(0,out.stock.get(i1.sku));
    }
}
