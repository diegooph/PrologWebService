package test.br.com.zalf.prolog.webservice.pilares.geral.unidade;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicaoDto;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoListagemDto;
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

/**
 * Created on 2021-03-03
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@TestComponent
public class UnidadeApiClient {

    private static final String RESOURCE = "/unidades";
    @Autowired
    private TestRestTemplate restTemplate;

    public ResponseEntity<UnidadeVisualizacaoListagemDto> getUnidadeByCodigo(final Long codUnidade) {
        final RequestEntity<Void> reqEntity = RequestEntity
                .get(URI.create(RESOURCE + "/" + codUnidade))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        return restTemplate.exchange(reqEntity,
                                     ParameterizedTypeReference.forType(UnidadeVisualizacaoListagemDto.class));
    }

    public ResponseEntity<List<UnidadeVisualizacaoListagemDto>> getUnidadesListagem(final Long codEmpresa,
                                                                                    final List<Long> codRegionais) {

        final UriComponents components = UriComponentsBuilder
                .fromPath(RESOURCE)
                .queryParam("codEmpresa", codEmpresa)
                .queryParam("codsRegionais", codRegionais)
                .build();

        final RequestEntity<Void> requestEntity = RequestEntity
                .get(components.toUri())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        return restTemplate.exchange(requestEntity,
                                     new ParameterizedTypeReference<List<UnidadeVisualizacaoListagemDto>>() {});
    }

    public ResponseEntity<SuccessResponse> updateUnidade(final UnidadeEdicaoDto dto) {
        return updateUnidade(dto, SuccessResponse.class);
    }

    public <T> ResponseEntity<T> updateUnidade(final UnidadeEdicaoDto dto, final Class<T> responseType) {
        final RequestEntity<UnidadeEdicaoDto> reqEntity = RequestEntity
                .put(URI.create(RESOURCE))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
        return restTemplate.exchange(reqEntity, responseType);
    }
}