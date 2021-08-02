package test.br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder;

import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder.ChecklistWorkOrderResource;
import br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model.ChecklistWorkOrderDto;
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
 * Created on 2021-04-13
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@TestComponent
public final class ChecklistWorkOrderApiClient {
    @Autowired
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<List<ChecklistWorkOrderDto>> getChecklistWorkOrder(@NotNull final List<Long> branchesId,
                                                                             final int limit,
                                                                             final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(ChecklistWorkOrderResource.RESOURCE_PATH)
                .path("/")
                .queryParam("codUnidades", branchesId.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("incluirItensOrdemServico", true)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> reqEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        return restTemplate.exchange(reqEntity, new ParameterizedTypeReference<List<ChecklistWorkOrderDto>>() {});
    }

    @NotNull
    public ResponseEntity<ClientSideErrorException> getChecklistWorkOrderWithWrongBranchesId(
            @NotNull final List<String> wrongBranchesId,
            final int limit,
            final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(ChecklistWorkOrderResource.RESOURCE_PATH)
                .path("/")
                .queryParam("codUnidades", String.join(",", wrongBranchesId))
                .queryParam("incluirItensOrdemServico", true)
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
