package test.br.com.zalf.prolog.webservice.v3.frota.veiculo;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoCadastroDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class VeiculoIT extends IntegrationTest {
    @Autowired
    private VeiculoApiClient client;

    @Test
    @DisplayName("given correct VeiculoCadastroDto to insert, then return status created")
    void givenCorrectVeiculoCadastroToInsert_ThenReturnStatusCreated() {
        final VeiculoCadastroDto veiculoCadastroToInsert = VeiculoCadastroFactory.createVeiculoCadastroToInsert();
        final ResponseEntity<SuccessResponse> response = client.insert(veiculoCadastroToInsert);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
