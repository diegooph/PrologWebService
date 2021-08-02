package test.br.com.zalf.prolog.webservice.v3.fleet.inspection;

import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.TireInspectionDto;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.VehicleInspectionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 2021-03-05
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class InspectionIT extends IntegrationTest {
    @Autowired
    private InspectionApiClient client;

    @Test
    @DisplayName("Dado parâmetros corretos, retorne List<AfericaoPlacaDto> e status OK")
    void givenCorrectParameters_ThenReturnListAfericaoPlacaDtoAndStatusOk() {
        final ResponseEntity<List<VehicleInspectionDto>> response =
                client.getAfericoesPlacas(List.of(215L),
                                          "2019-01-01",
                                          LocalDate.now().toString(),
                                          100,
                                          0);
        final HttpStatus status = response.getStatusCode();
        final List<VehicleInspectionDto> body = response.getBody();
        assertThat(status).isEqualTo(HttpStatus.OK);
        assertThat(body).isNotNull();
        assertThat(body.stream().findFirst().get()).isNotNull();
    }

    @Test
    @DisplayName("Dado parâmetros corretos, retorne List<AfericaoAvulsaDto> e status OK")
    void givenCorrectParameters_ThenReturnListAfericaoAvulsaDtoAndStatusOk() {
        final ResponseEntity<List<TireInspectionDto>> response =
                client.getAfericoesAvulsas(List.of(215L),
                                           "2019-01-01",
                                           LocalDate.now().toString(),
                                           100,
                                           0);

        final HttpStatus status = response.getStatusCode();
        final List<TireInspectionDto> body = response.getBody();
        assertThat(status).isEqualTo(HttpStatus.OK);
        assertThat(body).isNotNull();
        assertThat(body.stream().findFirst().get()).isNotNull();
    }
}
