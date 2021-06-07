package test.br.com.zalf.prolog.webservice.v3.frota.afericao;

import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoAvulsaDto;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoPlacaDto;
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
public class AfericaoApiClient {
    @NotNull
    private static final String RESOURCE = "/api/v3/afericoes";
    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    @NotNull
    public ResponseEntity<List<AfericaoPlacaDto>> getAfericoesPlacas(@NotNull final List<Long> codUnidades,
                                                                     @NotNull final String dataInicial,
                                                                     @NotNull final String dataFinal,
                                                                     final int limit,
                                                                     final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .path("/veiculos")
                .queryParam("codUnidades", codUnidades.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("dataInicial", dataInicial)
                .queryParam("dataFinal", dataFinal)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();
        final RequestEntity<Void> reqEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        return restTemplate.exchange(reqEntity, new ParameterizedTypeReference<List<AfericaoPlacaDto>>() {});
    }

    @NotNull
    public ResponseEntity<List<AfericaoAvulsaDto>> getAfericoesAvulsas(@NotNull final List<Long> codUnidades,
                                                                       @NotNull final String dataInicial,
                                                                       @NotNull final String dataFinal,
                                                                       final int limit,
                                                                       final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .path("/avulsas")
                .queryParam("codUnidades", codUnidades.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("dataInicial", dataInicial)
                .queryParam("dataFinal", dataFinal)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .build();

        final RequestEntity<Void> reqEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();
        return restTemplate.exchange(reqEntity, new ParameterizedTypeReference<List<AfericaoAvulsaDto>>() {});
    }
}