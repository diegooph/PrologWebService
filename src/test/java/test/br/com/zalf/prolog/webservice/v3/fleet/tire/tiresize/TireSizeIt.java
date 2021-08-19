package test.br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeCreateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

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
}
