package test.br.com.zalf.prolog.webservice.v3.fleet.vehicle;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleDto;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class VehicleIT extends IntegrationTest {
    @Autowired
    private VehicleApiClient client;

    @Test
    @DisplayName("given correct VeiculoCadastroDto to insert, then return status created")
    void givenCorrectVeiculoCadastroToInsert_ThenReturnStatusCreated() {
        final VehicleCreateDto veiculoCadastroToInsert = VehicleFactory.createVeiculoCadastroToInsert();
        final ResponseEntity<SuccessResponse> response = client.insert(veiculoCadastroToInsert);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("given correct VeiculoCadastroDto to insert, then return status created")
    void givenCorrectParameters_ThenReturnListVeiculosOk() {
        final ResponseEntity<List<VehicleDto>> response =
                client.getVeiculoListagem(List.of(215L),
                                          true,
                                          2,
                                          0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @DisplayName("Tipo do dado do parâmetro codUnidades incorreto, retornando ClientSideErrorException.")
    void givenWrongTypeParameter_ThenReturnClientSideErrorExceptionBadRequest() {
        final ResponseEntity<ClientSideErrorException> response = client.getVeiculoListagemBadRequest();
        Truth.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
