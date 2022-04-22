package ibm.gse.eda.stores.app;

import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


@OpenAPIDefinition(
    tags = {
            @Tag(name="eda", description="IBM Event Driven Architecture"),
            @Tag(name="labs", description="Inventory end to end solution")
    },
    info = @Info(
        title="Store item inventory aggregators API",
        version = "0.0.10",
        contact = @Contact(
            name = "IBM EDA team",
            url = "http://https://ibm-cloud-architecture.github.io/refarch-eda-store-inventory/"),
        license = @License(
            name = "Apache 2.0",
            url = "http://www.apache.org/licenses/LICENSE-2.0.html"))
)
public class StoreAggregatorApplication extends Application {
 
}

