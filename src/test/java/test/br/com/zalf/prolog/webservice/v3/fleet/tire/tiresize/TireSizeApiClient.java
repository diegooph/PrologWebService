package test.br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.TireSizeResource;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreateDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.net.URI;

@TestComponent
public class TireSizeApiClient {
    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<SuccessResponse> insert(@NotNull final TireSizeCreateDto dto) {
        return restTemplate.postForEntity(URI.create(TireSizeResource.RESOURCE_PATH), dto, SuccessResponse.class);
    }
}
