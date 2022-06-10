package ibm.gse.eda.stores.infra.api;

import java.util.logging.Logger;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/api/v1/version")
@ApplicationScoped
public class VersionResource {
    private static final Logger logger = Logger.getLogger(VersionResource.class.getName());
    @Inject
    @ConfigProperty(name="app.version")
    private String version;

    @GET
    public String getVersion(){
        return "{ \"version\": \"" + version + "\"}";
    }

    public void onStart(@Observes @Initialized(ApplicationScoped.class)Object context ){
		logger.info(getVersion());
	}
}
