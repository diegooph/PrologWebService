package test.br.com.zalf.prolog.webservice.v3.frota.checklistordemservico;

import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model.ChecklistOrdemServicoListagemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 2021-04-13
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class ChecklistOrdemServicoIT extends IntegrationTest {
    @Autowired
    private ChecklistOrdemServicoApiClient client;

    @Test
    @DisplayName("Dado parâmetros corretos, retorne List<ChecklistOrdemServicoListagemDto> e status OK")
    void givenMinimumCorrectParameters_ThenReturnListChecklistOrdemServicoStatusOk() {

        final ResponseEntity<List<ChecklistOrdemServicoListagemDto>> response =
                client.getOrdensServico(List.of(215L), 2, 0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @DisplayName("Tipo do dado do parâmetro codUnidades incorreto, retornando ClientSideErrorException.")
    void givenWrongTypeParameter_ThenReturnClientSideErrorExceptionBadRequest() {
        final ResponseEntity<ClientSideErrorException> response =
                client.getOrdensServicoWithWrongUnidades(List.of("a"), 2, 0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
