package test.br.com.zalf.prolog.webservice.v3.fleet.tire;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.v3.fleet.tire.TireResource;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@TestComponent
public class TireApiClient {
    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<SuccessResponse> insert(@NotNull final TireCreateDto dto) {
        return restTemplate.postForEntity(URI.create(TireResource.RESOURCE_PATH), dto, SuccessResponse.class);
    }

    @NotNull
    public ResponseEntity<List<TireDto>> getTireByStatus(@NotNull final List<Long> branchesId,
                                                         @Nullable final StatusPneu tireStatus,
                                                         final int limit,
                                                         final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(TireResource.RESOURCE_PATH)
                .queryParam("codUnidades", branchesId.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("statusPneu", tireStatus)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> reqEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(reqEntity, new ParameterizedTypeReference<List<TireDto>>() {});
    }

    @NotNull
    public ResponseEntity<ClientSideErrorException> getTireByStatusWithError(@NotNull final List<Long> branchesId,
                                                                             @Nullable final StatusPneu tireStatus,
                                                                             final int limit,
                                                                             final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(TireResource.RESOURCE_PATH)
                .queryParam("codUnidades", branchesId.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("statusPneu", tireStatus)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> reqEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(reqEntity, new ParameterizedTypeReference<ClientSideErrorException>() {});
    }
}
