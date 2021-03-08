package test.br.com.zalf.prolog.webservice.pilares.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.AfericaoAvulsaDto;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3._model.dto.AfericaoPlacaDto;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.v3.dao.AfericaoV3Dao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 2021-03-05
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class AfericaoIT extends IntegrationTest {

    @Autowired
    private AfericaoApiClient client;

    @Autowired
    private AfericaoV3Dao dao;

    @Test
    @DisplayName("Dado parâmetros corretos, retorne List<AfericaoPlacaDto> e status OK")
    void givenCorrectParameters_ThenReturnListAfericaoPlacaDtoAndStatusOk() {

        final ResponseEntity<List<AfericaoPlacaDto>> response = client.getAfericoesPlacas(Collections.singletonList(5L),
                                                                                          "PRO0001",
                                                                                          64L,
                                                                                          LocalDate.now()
                                                                                                  .minusDays(1)
                                                                                                  .toString(),
                                                                                          LocalDate.now()
                                                                                                  .toString(),
                                                                                          100,
                                                                                          0);
        final HttpStatus status = response.getStatusCode();
        final List<AfericaoPlacaDto> body = response.getBody();
        assertThat(status).isEqualTo(HttpStatus.OK);
        assertThat(body).isNotNull();
        assertThat(body.stream()
                           .findFirst()
                           .get()).isEqualTo("PRO0001");
    }

    @Test
    @DisplayName("Dado parâmetros corretos, retorne List<AfericaoAvulsaDto> e status OK")
    void givenCorrectParameters_ThenReturnListAfericaoAvulsaDtoAndStatusOk() {

        final ResponseEntity<List<AfericaoAvulsaDto>> response =
                client.getAfericoesAvulsas(Collections.singletonList(5L),
                                           LocalDate.now().minusYears(1).toString(),
                                           LocalDate.now().toString(),
                                           100,
                                           0);

        final HttpStatus status = response.getStatusCode();
        final List<AfericaoAvulsaDto> body = response.getBody();
        assertThat(status).isEqualTo(HttpStatus.OK);
        assertThat(body).isNotNull();
        assertThat(body.stream()
                           .findFirst()
                           .get()).isNotNull();
    }
}
