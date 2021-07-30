package test.br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovimentProcessDto;
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
 * Created on 2021-04-27
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@TestComponent
public final class MovimentacaoProcessoApiClient {
    private static final String RESOURCE = "/api/v3/movimentacoes";
    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<List<TireMovimentProcessDto>> getMovimentacacaoProcessos(
            @NotNull final List<Long> codUnidades,
            @NotNull final String dataInicial,
            @NotNull final String dataFinal,
            final int limit,
            final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .queryParam("codUnidades", codUnidades.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("dataInicial", dataInicial)
                .queryParam("dataFinal", dataFinal)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(requestEntity,
                                     new ParameterizedTypeReference<List<TireMovimentProcessDto>>() {});
    }

    @NotNull
    public ResponseEntity<ClientSideErrorException> getMovimentacacaoProcessosBadRequest() {
        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .queryParam("codUnidades", "a")
                .queryParam("dataInicial", "2021-01-01")
                .queryParam("dataFinal", "2021-01-01")
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
