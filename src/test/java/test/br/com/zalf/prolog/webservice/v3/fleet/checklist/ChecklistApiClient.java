package test.br.com.zalf.prolog.webservice.v3.fleet.checklist;

import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistDto;
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

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-04-23
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@TestComponent
public class ChecklistApiClient {
    @NotNull
    private static final String RESOURCE = "/api/v3/checklists";
    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<List<ChecklistDto>> getChecklistsByFitlro(@NotNull final List<Long> codUnidades,
                                                                    @NotNull final String dataInicial,
                                                                    @NotNull final String dataFinal,
                                                                    @Nullable final Long codColaborador,
                                                                    @Nullable final Long codVeiculo,
                                                                    @Nullable final Long codTipoVeiculo,
                                                                    final boolean incluirRespostas,
                                                                    final int limit,
                                                                    final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .queryParam("codUnidades", codUnidades.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("dataInicial", dataInicial)
                .queryParam("dataFinal", dataFinal)
                .queryParam("codColaborador", codColaborador)
                .queryParam("codVeiculo", codVeiculo)
                .queryParam("codTipoVeiculo", codTipoVeiculo)
                .queryParam("incluirRespostas", incluirRespostas)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<ChecklistDto>>() {});
    }

    public ResponseEntity<ClientSideErrorException> getChecklistsWithWrongTypeUnidades(
            @NotNull final List<String> wrongTypeCodUnidades,
            final String dataInicial,
            final String dataFinal,
            final int limit,
            final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .queryParam("codUnidades", String.join(",", wrongTypeCodUnidades))
                .queryParam("dataInicial", dataInicial)
                .queryParam("dataFinal", dataFinal)
                .queryParam("incluirRespostas", false)
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