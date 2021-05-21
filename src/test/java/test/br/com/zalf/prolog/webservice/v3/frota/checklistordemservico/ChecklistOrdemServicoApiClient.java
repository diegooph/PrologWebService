package test.br.com.zalf.prolog.webservice.v3.frota.checklistordemservico;

import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoListagemDto;
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
public final class ChecklistOrdemServicoApiClient {
    private static final String RESOURCE = "/api/v3/checklists/ordens-servico";
    @Autowired
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<List<ChecklistOrdemServicoListagemDto>> getOrdensServico(
            @NotNull final List<Long> codUnidades,
            final int limit,
            final int offset) {

        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .path("/")
                .queryParam("codUnidades", codUnidades.stream()
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

        return restTemplate.exchange(reqEntity,
                                     new ParameterizedTypeReference<List<ChecklistOrdemServicoListagemDto>>() {});
    }

    @NotNull
    public ResponseEntity<ClientSideErrorException> getOrdensServicoWithWrongUnidades(
            @NotNull final List<String> wrongTypeCodUnidades,
            final int limit,
            final int offset) {

        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .path("/")
                .queryParam("codUnidades", String.join(",", wrongTypeCodUnidades))
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
