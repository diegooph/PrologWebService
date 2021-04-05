package test.br.com.zalf.prolog.webservice.pilares.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.v3.frota.afericao._model.dto.AfericaoAvulsaDto;
import br.com.zalf.prolog.webservice.v3.frota.afericao._model.dto.AfericaoPlacaDto;
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

    private static final String RESOURCE = "/v3/afericoes";

    @Autowired
    @NotNull
    private TestRestTemplate restTemplate;

    public ResponseEntity<List<AfericaoPlacaDto>> getAfericoesPlacas(final List<Long> codUnidades,
                                                                    final String placaVeiculo,
                                                                    final Long codTipoVeiculo,
                                                                    final String dataInicial,
                                                                    final String dataFinal,
                                                                    final int limit,
                                                                    final int offset) {

        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .path("/placas")
                .queryParam("unidades", codUnidades.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(",")))
                .queryParam("placa", placaVeiculo)
                .queryParam("codTipoVeiculo", codTipoVeiculo)
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

    public ResponseEntity<List<AfericaoAvulsaDto>> getAfericoesAvulsas(final List<Long> codUnidades,
                                                       final String dataInicial,
                                                       final String dataFinal,
                                                       final int limit,
                                                       final int offset) {
        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .path("/avulsas")
                .queryParam("unidades", codUnidades.stream()
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