package test.br.com.zalf.prolog.webservice.v3.frota.pneu;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuCadastroDto;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuListagemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 2021-03-16
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class PneuIT extends IntegrationTest {
    @Autowired
    private PneuApiClient client;

    @Test
    @DisplayName("given correct PneuCadastro to insert, then return status created")
    void givenCorrectPneuCadastroToInsert_ThenReturnStatusCreated() {
        final PneuCadastroDto pneuForCreation = PneuCadastroFactory.createCorrectPneuCadastro();
        final ResponseEntity<SuccessResponse> response = client.insert(pneuForCreation);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("given parameters to search, then return a list of pneus")
    void givenParametersToSearch_ThenReturnListOfPneus() {
        final ResponseEntity<List<PneuListagemDto>> response =
                client.getPneusByStatus(List.of(215L), null, 1000, 0);
        final List<PneuListagemDto> pneus = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pneus).isNotNull();
        assertThat(pneus).isNotEmpty();
    }

    @Test
    @DisplayName("given wrong parameters to search, then return error")
    void givenWrongParametersToSearch_ThenReturnError() {
        final ResponseEntity<ClientSideErrorException> error =
                client.getPneusByStatusWithError(List.of(215212L), null, 1000, 0);
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
