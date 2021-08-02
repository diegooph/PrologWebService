package test.br.com.zalf.prolog.webservice.v3.fleet.tiremovement;

import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovimentProcessDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created on 2021-04-27
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class TireMovementIT extends IntegrationTest {
    @Autowired
    private TireMovementApiClient client;

    @Test
    @DisplayName("Dado mínimos parâmetros corretos, retorne List<MovimentacaoProcessoListagemDto> e status OK")
    void givenMinimumCorrectParameters_ThenReturnListMovimentacaoProcessoStatusOk() {
        final ResponseEntity<List<TireMovimentProcessDto>> response =
                client.getMovimentacacaoProcessos(List.of(215L),
                                                  "2018-01-01",
                                                  "2021-12-12",
                                                  2,
                                                  0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @DisplayName("Tipo do dado do parâmetro codUnidades incorreto, retornando ClientSideErrorException.")
    void givenWrongTypeParameter_ThenReturnClientSideErrorExceptionBadRequest() {
        final ResponseEntity<ClientSideErrorException> response = client.getMovimentacacaoProcessosBadRequest();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
