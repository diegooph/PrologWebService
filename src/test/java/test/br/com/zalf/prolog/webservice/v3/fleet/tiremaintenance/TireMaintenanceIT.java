package test.br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance;

import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceDto;
import br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model.TireMaintenanceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static test.br.com.zalf.prolog.webservice.config.TestConstants.TEST_UNIDADE_ID;

/**
 * Created on 2021-05-28
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
public class TireMaintenanceIT extends IntegrationTest {
    private static final int LIMIT = 4;
    private static final int OFFSET = 0;
    @Autowired
    private TireMaintenanceApiClient client;

    private boolean containsTestCodUnidade(final TireMaintenanceDto dto) {
        return dto.getTireMaintenanceBranchId().equals(TEST_UNIDADE_ID);
    }

    private boolean containsStatus(final TireMaintenanceDto dto, final TireMaintenanceStatus status) {
        return dto.getMaintenanceStatus().equals(status);
    }

    @Test
    @DisplayName("Dado apenas parâmetros padrões retorne todos os servicos abertos e fechados")
    void givenDefaultParams_ThenReturnAllServicos() {

        final ResponseEntity<List<TireMaintenanceDto>> response = this.client
                .getServicosByFiltros(List.of(TEST_UNIDADE_ID), LIMIT, OFFSET);
        assertThat(response.getBody())
                .satisfies(dtos -> {
                    assertThat(dtos)
                            .isNotEmpty()
                            .hasSize(LIMIT)
                            .allMatch(this::containsTestCodUnidade)
                            .anyMatch(dto -> this.containsStatus(dto, TireMaintenanceStatus.OPEN))
                            .anyMatch(dto -> this.containsStatus(dto, TireMaintenanceStatus.RESOLVED));
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Dado parâmetros padrões e status ABERTO, retorne todos os servicos abertos")
    void givenDefaultParamsAndStatusAberto_ThenReturnServicosAbertos() {

        final ResponseEntity<List<TireMaintenanceDto>> response = this.client
                .getServicosByFiltros(List.of(TEST_UNIDADE_ID), TireMaintenanceStatus.OPEN, LIMIT, OFFSET);

        assertThat(response.getBody())
                .satisfies(dtos -> {
                    assertThat(dtos)
                            .isNotEmpty()
                            .hasSize(2)
                            .allMatch(this::containsTestCodUnidade)
                            .allMatch(dto -> this.containsStatus(dto, TireMaintenanceStatus.OPEN));
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Dado parâmetros padrões e status FECHADO, retorne todos os servicos fechados")
    void givenDefaultParamsAndStatusFechado_ThenReturnServicosFechados() {
        final ResponseEntity<List<TireMaintenanceDto>> response = this.client
                .getServicosByFiltros(List.of(TEST_UNIDADE_ID), TireMaintenanceStatus.RESOLVED, LIMIT, OFFSET);

        assertThat(response.getBody())
                .satisfies(dtos -> {
                    assertThat(dtos)
                            .isNotEmpty()
                            .hasSize(2)
                            .allMatch(this::containsTestCodUnidade)
                            .allMatch(dto -> this.containsStatus(dto, TireMaintenanceStatus.RESOLVED));
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Dado parâmetros padrões e codPneu, retorne todos os serviços com o codPneu")
    void givenDefaultParamsAndCodPneu_ThenReturnServicosWithCodPneu() {
        final long codPneu = 1000L;
        final ResponseEntity<List<TireMaintenanceDto>> response = this.client
                .getServicosByFiltros(List.of(TEST_UNIDADE_ID), null, null, codPneu, LIMIT, OFFSET);
        assertThat(response.getBody())
                .satisfies(dtos -> {
                    assertThat(dtos)
                            .isNotEmpty()
                            .hasSize(1)
                            .allMatch(this::containsTestCodUnidade)
                            .allMatch(dto -> Objects.equals(codPneu, dto.getTireId()));
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Dado parâmetros padrões e codVeiculo, retorne todos os serviços com o codVeiculo")
    void givenDefaultParamsAndCodVeiculo_ThenReturnServicosWithCodVeiculo() {
        final long codVeiculo = 7945L;
        final ResponseEntity<List<TireMaintenanceDto>> response = this.client
                .getServicosByFiltros(List.of(TEST_UNIDADE_ID), null, codVeiculo, null, LIMIT, OFFSET);
        assertThat(response.getBody())
                .satisfies(dtos -> {
                    assertThat(dtos)
                            .isNotEmpty()
                            .hasSize(4)
                            .allMatch(this::containsTestCodUnidade)
                            .allMatch(dto -> Objects.equals(codVeiculo, dto.getVehicleId()));
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
