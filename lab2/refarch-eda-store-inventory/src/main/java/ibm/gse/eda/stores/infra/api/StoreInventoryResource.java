package ibm.gse.eda.stores.infra.api;

import java.util.logging.Logger;

import ibm.gse.eda.stores.infra.api.dto.InventoryQueryResult;
import ibm.gse.eda.stores.infra.api.dto.ItemCountQueryResult;
import ibm.gse.eda.stores.infra.api.dto.PipelineMetadata;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;


@ApplicationScoped
@Path("/api/v1/stores")
public class StoreInventoryResource {
    private static final Logger logger = Logger.getLogger(StoreInventoryResource.class.getName());

    private final Client client = ClientBuilder.newBuilder().build();

    @Inject
    private StoreInventoryQueries inventoryQueries;
    
    @GET
    @Path("/inventory/{storeID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<InventoryQueryResult> getStock(@PathParam("storeID") String storeID) {
        InventoryQueryResult result = inventoryQueries.getStoreStock(storeID);
        if (result.getResult().isPresent()) {
            System.out.println("result: " + result.getResult().get().storeName);
            return Uni.createFrom().item(result);
        } else if (result.getHost().isPresent()) {
            System.out.println("data is remote on " + result.getHost());
            // this is a questionable implementation. here for demo purpose.
            return queryRemoteInventoryStore(result.getHost().get(), result.getPort().getAsInt(), storeID);
        } else {
            return Uni.createFrom().item(InventoryQueryResult.notFound());
        }
    }

    @GET
    @Path("/meta-data")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<PipelineMetadata> getStoreMetaData() {
        return Multi.createFrom().items(inventoryQueries.getStoreInventoryStoreMetadata().stream());
    }


    private Uni<InventoryQueryResult> queryRemoteInventoryStore(final String host, final int port, String storeId) {
        String url = String.format("http://%s:%d//inventory/store/%s", host, port, storeId);
        logger.info("Data found on " + url);
        InventoryQueryResult rep = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .get(InventoryQueryResult.class);
        return Uni.createFrom().item(rep);
    }

    private Uni<ItemCountQueryResult> queryRemoteItemCountStore(final String host, final int port, String itemID) {
        String url = String.format("http://%s:%d//inventory/item/%s", host, port, itemID);
        logger.info("Data found on " + url);
        ItemCountQueryResult rep = client.target(url)
                .request(MediaType.APPLICATION_JSON)
                .get(ItemCountQueryResult.class);
        return Uni.createFrom().item(rep);
    }

}