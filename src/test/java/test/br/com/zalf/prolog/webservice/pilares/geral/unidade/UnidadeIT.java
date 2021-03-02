package test.br.com.zalf.prolog.webservice.pilares.geral.unidade;

import br.com.zalf.prolog.webservice.geral.unidade.UnidadeDao;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoListagemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class UnidadeIT extends IntegrationTest {

    @Test
    @DisplayName("Busca uma unidade através de um código.")
    void givenCodUnidadeToRequest_ThenReturnUnidadeVisualizacaoListagemAndStatusOK() {

        final RequestEntity<Void> requestEntity = RequestEntity
                .get(URI.create(createPathWithPort(RESOURCE + TEST_UNIDADE_ID)))
                .accept(MediaType.APPLICATION_JSON)
                .build();

        final ResponseEntity<UnidadeVisualizacaoListagemDto> response = getTestRestTemplate()
                .exchange(requestEntity, ParameterizedTypeReference.forType(UnidadeVisualizacaoListagemDto.class));

        assertBaseValidations(response);
        assertThat(response.getBody().getCodUnidade())
                .isEqualTo(TEST_UNIDADE_ID);
    }
}