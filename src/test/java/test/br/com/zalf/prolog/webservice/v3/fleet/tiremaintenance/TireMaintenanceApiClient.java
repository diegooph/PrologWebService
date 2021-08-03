package test.br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance;

import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance.TireMaintenanceResource;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceDto;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Created on 2021-05-28
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@TestComponent
public class TireMaintenanceApiClient {
    @Autowired
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<List<TireMaintenanceDto>> getTireMaintenanceByFilter(final List<Long> branchesId,
                                                                               final int limit,
                                                                               final int offset) {
        return getTireMaintenanceByFilter(branchesId, null, limit, offset);
    }

    @NotNull
    public ResponseEntity<List<TireMaintenanceDto>> getTireMaintenanceByFilter(final List<Long> branchesId,
                                                                               final TireMaintenanceStatus status,
                                                                               final int limit,
                                                                               final int offset) {
        return getTireMaintenanceByFilter(branchesId, status, null, null, limit, offset);
    }

    @NotNull
    public ResponseEntity<List<TireMaintenanceDto>> getTireMaintenanceByFilter(final List<Long> branchesId,
                                                                               final TireMaintenanceStatus status,
                                                                               final Long vehicleId,
                                                                               final Long tireId,
                                                                               final int limit,
                                                                               final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(TireMaintenanceResource.RESOURCE_PATH)
                .queryParam("branchesId", branchesId)
                .queryParam("maintenanceStatus", status)
                .queryParam("vehicleId", vehicleId)
                .queryParam("tireId", tireId)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .build();
        return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<TireMaintenanceDto>>() {});
    }
}
