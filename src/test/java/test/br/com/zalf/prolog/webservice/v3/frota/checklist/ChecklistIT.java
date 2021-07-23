package test.br.com.zalf.prolog.webservice.v3.frota.checklist;

import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.fleet.checklist._model.ChecklistDto;
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
    @DisplayName("Dado os parâmetros corretos, retorne a Listagem de Checklist.")
    void givenCorrectParameters_ThenReturnChecksSize() {
        final ResponseEntity<List<ChecklistDto>> responseEntity =
                client.getChecklistsByFitlro(List.of(215L),
                                             "2019-01-01",
                                             "2021-03-30",
                                             null,
                                             null,
                                             null,
                                             false,
                                             2,
                                             0);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Dado os parâmetros corretos que não incluem resposta, retorne a Listagem de Checklist sem respostas.")
    void givenCorrectParametersWithoutIncluirRespostas_ThenReturnListChecklistDtoAndStatusOk() {
        final ResponseEntity<List<ChecklistDto>> responseEntity =
                client.getChecklistsByFitlro(List.of(215L),
                                             "2019-01-01",
                                             "2021-03-30",
                                             null,
                                             null,
                                             null,
                                             false,
                                             10,
                                             0);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().get(0).getChecklistItems()).isNull();
    }

    @Test
    @DisplayName("Dado os parâmetros corretos que incluem resposta, retorne a Listagem de Checklist com respostas.")
    void givenCorrectParametersWithIncluirRespostas_ThenReturnListChecklistDtoAndStatusOk() {
        final ResponseEntity<List<ChecklistDto>> responseEntity =
                client.getChecklistsByFitlro(List.of(215L),
                                             "2019-01-01",
                                             "2021-03-30",
                                             null,
                                             null,
                                             null,
                                             true,
                                             10,
                                             0);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().get(0).getChecklistItems()).isNotNull();
    }

    @Test
    @DisplayName("Dado parâmetros que n possuem Data, retorne a Listagem de Checklist vazia.")
    void givenParametersWithoutData_ThenReturnEmptyListChecklistDto() {
        final ResponseEntity<List<ChecklistDto>> responseEntity =
                client.getChecklistsByFitlro(List.of(5L),
                                             "2021-02-01",
                                             "2021-02-27",
                                             2145L,
                                             3986L,
                                             64L,
                                             false,
                                             10,
                                             0);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Dado o tipo de dado codUnidades incorreto, retorne ClientSideErrorException.")
    void givenWrongTypeParameter_ThenReturnClientSideErrorExceptionBadRequest() {
        final ResponseEntity<ClientSideErrorException> response = client.getChecklistsWithWrongTypeUnidades(
                List.of("a"),
                "2021-03-01",
                "2021-03-30",
                2,
                0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
