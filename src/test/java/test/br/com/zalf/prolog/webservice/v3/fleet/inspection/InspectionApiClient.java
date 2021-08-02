package test.br.com.zalf.prolog.webservice.v3.fleet.inspection;

import br.com.zalf.prolog.webservice.v3.fleet.inspection.InspectionResource;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.TireInspectionDto;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.VehicleInspectionDto;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-03-05
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@TestComponent
public class InspectionApiClient {
    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<List<VehicleInspectionDto>> getVehicleInspection(@NotNull final List<Long> branchesId,
                                                                           @NotNull final String startDate,
                                                                           @NotNull final String endDate,
                                                                           final int limit,
                                                                           final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(InspectionResource.RESOURCE_PATH)
                .path("/veiculos")
                .queryParam("codUnidades", branchesId.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("dataInicial", startDate)
                .queryParam("dataFinal", endDate)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> reqEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        return restTemplate.exchange(reqEntity, new ParameterizedTypeReference<List<VehicleInspectionDto>>() {});
    }

    @NotNull
    public ResponseEntity<List<TireInspectionDto>> getTireInspection(@NotNull final List<Long> branchesId,
                                                                     @NotNull final String startDate,
                                                                     @NotNull final String endDate,
                                                                     final int limit,
                                                                     final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(InspectionResource.RESOURCE_PATH)
                .path("/avulsas")
                .queryParam("codUnidades", branchesId.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("dataInicial", startDate)
                .queryParam("dataFinal", endDate)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();

        final RequestEntity<Void> reqEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(reqEntity, new ParameterizedTypeReference<List<TireInspectionDto>>() {});
    }
}