package test.br.com.zalf.prolog.webservice.pilares.geral.unidade;

import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoListagemDto;
import lombok.var;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class UnidadeIT extends IntegrationTest {

    @Test
    @DisplayName("Busca uma unidade através de um código.")
    void givenCodUnidadeToRequest_ThenReturnUnidadeVisualizacaoListagemAndStatusOK() {
        final Long codUnidade = 5L;
        final String path = createPathWithPort("unidades/" + codUnidade);
        final var requestEntity = RequestEntity
                .get(URI.create(path))
                .accept(MediaType.APPLICATION_JSON)
                .build();
        final ResponseEntity<UnidadeVisualizacaoListagemDto> response =
                getTestRestTemplate().exchange(path,
                                               HttpMethod.GET,
                                               requestEntity,
                                               ParameterizedTypeReference.forType(UnidadeVisualizacaoListagemDto.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCodUnidade()).isEqualTo(codUnidade);
    }
}