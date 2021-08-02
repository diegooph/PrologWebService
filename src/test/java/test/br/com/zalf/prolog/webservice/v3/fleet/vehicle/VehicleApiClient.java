package test.br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.fleet.tire.TireResource;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle.VehicleResource;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@TestComponent
public class VehicleApiClient {
    @Autowired
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<SuccessResponse> insert(@NotNull final VehicleCreateDto dto) {
        return insert(dto, SuccessResponse.class);
    }

    @NotNull
    public <T> ResponseEntity<T> insert(@NotNull final VehicleCreateDto dto,
                                        @NotNull final Class<T> responseType) {
        return restTemplate.postForEntity(URI.create(VehicleResource.RESOURCE_PATH), dto, responseType);
    }

    public <T> ResponseEntity<List<VehicleDto>> getVehicles(@NotNull final List<Long> branchesId,
                                                            final boolean statusActive,
                                                            final int limit,
                                                            final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(TireResource.RESOURCE_PATH)
                .queryParam("codUnidades", branchesId.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("statusAtivo", statusActive)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<VehicleDto>>() {});
    }

    @NotNull
    public ResponseEntity<ClientSideErrorException> getVehiclesBadRequest() {
        final UriComponents components = UriComponentsBuilder
                .fromPath(TireResource.RESOURCE_PATH)
                .queryParam("codUnidades", "a")
                .queryParam("statusAtivo", false)
                .queryParam("limit", 1000)
                .queryParam("offset", 0)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<ClientSideErrorException>() {});
    }
}
