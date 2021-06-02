package test.br.com.zalf.prolog.webservice.v3.frota.pneu;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuCadastroDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

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
}