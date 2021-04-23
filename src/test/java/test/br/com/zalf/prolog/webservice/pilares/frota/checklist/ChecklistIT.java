package test.br.com.zalf.prolog.webservice.pilares.frota.checklist;

import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistListagemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 2021-04-23
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class ChecklistIT extends IntegrationTest {

    @Autowired
    private ChecklistApiClient client;

    @Test
    @DisplayName("Dado código da unidade, retorne a UnidadeVisualizacaoListagem correspondente.")
    void givenCodUnidadeToRequest_ThenReturnUnidadeVisualizacaoListagemAndStatusOK() {
        final ResponseEntity<List<ChecklistListagemDto>> responseEntity =
                client.getChecklistsByFitlro(List.of(215L),
                                             "2020-01-01",
                                             "2021-03-12");
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().size()).isEqualTo(2);
    }
}
