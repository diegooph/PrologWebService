package test.br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreateDto;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TireSizeIt extends IntegrationTest {
    @Autowired
    private TireSizeApiClient client;

    @Test
    @DisplayName("given correct tire sizes infos to insert, then return status created")
    void givenCorrectTireToInsert_ThenReturnStatusCreated() {
        final ResponseEntity<SuccessResponse> response =
                client.insert(
                        TireSizeCreateDto.builder()
                                .withCompanyId(3L)
                                .withHeight(1.0)
                                .withWidth(2.0)
                                .withRim(3.0)
                                .withAdditionalId("Teste cod auxiliar")
                                .build());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("given wrong tire sizes infos (null rim) to insert, then return error")
    void givenCorrectTireToInsert_ThenReturnError() {
        final ResponseEntity<SuccessResponse> response =
                client.insert(
                        TireSizeCreateDto.builder()
                                .withCompanyId(3L)
                                .withHeight(1.0)
                                .withWidth(2.0)
                                .withAdditionalId("Teste cod auxiliar")
                                .build());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("given correct query params, return a list of tire sizes")
    void givenCorrectTireToList_ThenReturnTireSizeList() {
        final ResponseEntity<List<TireSizeDto>> response =
                client.getAllTireSizes(3, true);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @DisplayName("given wrong company id, return forbidden")
    void givenCorrectTireToList_ThenForbbiden() {
        final ResponseEntity<ClientSideErrorException> response =
                client.getAllTireSizesWithWrongCompanyId(10, true);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
