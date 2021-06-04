package test.br.com.zalf.prolog.webservice.v3.frota.servicopneu;

import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuStatus;
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
public class ServicoPneuIT extends IntegrationTest {
    private static final int LIMIT = 4;
    private static final int OFFSET = 0;
    @Autowired
    private ServicoPneuApiClient client;

    private boolean containsTestCodUnidade(final ServicoPneuListagemDto dto) {
        return dto.getCodUnidadeServico().equals(TEST_UNIDADE_ID);
    }

    private boolean containsStatus(final ServicoPneuListagemDto dto, final ServicoPneuStatus status) {
        return dto.getStatus().equals(status);
    }

    @Test
    @DisplayName("Dado apenas parâmetros padrões retorne todos os servicos abertos e fechados")
    void givenDefaultParams_ThenReturnAllServicos() {

        final ResponseEntity<List<ServicoPneuListagemDto>> response = this.client
                .getServicosByFiltros(List.of(TEST_UNIDADE_ID), LIMIT, OFFSET);
        assertThat(response.getBody())
                .satisfies(dtos -> {
                    assertThat(dtos)
                            .isNotEmpty()
                            .hasSize(LIMIT)
                            .allMatch(this::containsTestCodUnidade)
                            .anyMatch(dto -> this.containsStatus(dto, ServicoPneuStatus.ABERTO))
                            .anyMatch(dto -> this.containsStatus(dto, ServicoPneuStatus.FECHADO));
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Dado parâmetros padrões e status ABERTO, retorne todos os servicos abertos")
    void givenDefaultParamsAndStatusAberto_ThenReturnServicosAbertos() {

        final ResponseEntity<List<ServicoPneuListagemDto>> response = this.client
                .getServicosByFiltros(List.of(TEST_UNIDADE_ID), ServicoPneuStatus.ABERTO, LIMIT, OFFSET);

        assertThat(response.getBody())
                .satisfies(dtos -> {
                    assertThat(dtos)
                            .isNotEmpty()
                            .hasSize(2)
                            .allMatch(this::containsTestCodUnidade)
                            .allMatch(dto -> this.containsStatus(dto, ServicoPneuStatus.ABERTO));
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Dado parâmetros padrões e status FECHADO, retorne todos os servicos fechados")
    void givenDefaultParamsAndStatusFechado_ThenReturnServicosFechados() {
        final ResponseEntity<List<ServicoPneuListagemDto>> response = this.client
                .getServicosByFiltros(List.of(TEST_UNIDADE_ID), ServicoPneuStatus.FECHADO, LIMIT, OFFSET);

        assertThat(response.getBody())
                .satisfies(dtos -> {
                    assertThat(dtos)
                            .isNotEmpty()
                            .hasSize(2)
                            .allMatch(this::containsTestCodUnidade)
                            .allMatch(dto -> this.containsStatus(dto, ServicoPneuStatus.FECHADO));
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Dado parâmetros padrões e codPneu, retorne todos os serviços com o codPneu")
    void givenDefaultParamsAndCodPneu_ThenReturnServicosWithCodPneu() {
        final long codPneu = 1000L;
        final ResponseEntity<List<ServicoPneuListagemDto>> response = this.client
                .getServicosByFiltros(List.of(TEST_UNIDADE_ID), null, null, codPneu, LIMIT, OFFSET);
        assertThat(response.getBody())
                .satisfies(dtos -> {
                    assertThat(dtos)
                            .isNotEmpty()
                            .hasSize(1)
                            .allMatch(this::containsTestCodUnidade)
                            .allMatch(dto -> Objects.equals(codPneu, dto.getCodPneu()));
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Dado parâmetros padrões e codVeiculo, retorne todos os serviços com o codVeiculo")
    void givenDefaultParamsAndCodVeiculo_ThenReturnServicosWithCodVeiculo() {
        final long codVeiculo = 7945L;
        final ResponseEntity<List<ServicoPneuListagemDto>> response = this.client
                .getServicosByFiltros(List.of(TEST_UNIDADE_ID), null, codVeiculo, null, LIMIT, OFFSET);
        assertThat(response.getBody())
                .satisfies(dtos -> {
                    assertThat(dtos)
                            .isNotEmpty()
                            .hasSize(4)
                            .allMatch(this::containsTestCodUnidade)
                            .allMatch(dto -> Objects.equals(codVeiculo, dto.getCodVeiculo()));
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
