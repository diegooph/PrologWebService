package test.br.com.zalf.prolog.webservice.pilares.geral.unidade;

import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoListagemDto;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

public class UnidadeIT extends IntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Busca uma unidade através de um código.")
    void getUnidadeByCodigo() {
        final long codUnidade = 5;
        final ResponseEntity<UnidadeVisualizacaoListagemDto> unidadeByCodigo =
                restTemplate.exchange("/unidades/{codUnidade}",
                                      HttpMethod.GET,
                                      null,
                                      UnidadeVisualizacaoListagemDto.class,
                                      codUnidade);
        Truth.assertThat(unidadeByCodigo).isNotNull();
        Truth.assertThat(unidadeByCodigo.getStatusCode()).isEqualTo(HttpStatus.OK);
        Truth.assertThat(unidadeByCodigo.getBody()).isNotNull();
        Truth.assertThat(unidadeByCodigo.getBody().getCodUnidade()).isEqualTo(codUnidade);
    }
}