package test.br.com.zalf.prolog.webservice.pilares.frota.movimentacao;

import br.com.zalf.prolog.webservice.v3.frota.movimentacao._model.MovimentacaoProcessoListagemDto;
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
public final class MovimentacaoProcessoIT extends IntegrationTest {
    @Autowired
    private MovimentacaoProcessoApiClient client;

    @Test
    @DisplayName("Dado mínimos parâmetros corretos, retorne List<MovimentacaoProcessoListagemDto> e status OK")
    void givenMinimumCorrectParameters_ThenReturnListMovimentacaoProcessoStatusOk() {
        final ResponseEntity<List<MovimentacaoProcessoListagemDto>> response =
                client.getMovimentacacaoProcessosByCodUnidade(List.of(215L),
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
    }
}
