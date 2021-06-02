package test.br.com.zalf.prolog.webservice.v3.geral.unidade;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.exception.BadRequestException;
import br.com.zalf.prolog.webservice.errorhandling.sql.NotFoundException;
import br.com.zalf.prolog.webservice.v3.geral.unidade.UnidadeDao;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeEdicaoDto;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeEntity;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeVisualizacaoListagemDto;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import test.br.com.zalf.prolog.webservice.IntegrationTest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static test.br.com.zalf.prolog.webservice.config.TestConstants.TEST_UNIDADE_ID;

public class UnidadeIT extends IntegrationTest {
    @Autowired
    private UnidadeApiClient client;
    @Autowired
    private UnidadeDao dao;

    private <T> void assertBaseValidations(final ResponseEntity<T> responseEntity) {
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Dado código da unidade, retorne a UnidadeVisualizacaoListagem correspondente.")
    void givenCodUnidadeToRequest_ThenReturnUnidadeVisualizacaoListagemAndStatusOK() {
        final ResponseEntity<UnidadeVisualizacaoListagemDto> response = client.getUnidadeByCodigo(TEST_UNIDADE_ID);
        assertBaseValidations(response);
        assertThat(response.getBody().getCodUnidade()).isEqualTo(TEST_UNIDADE_ID);
    }

    @Test
    @DisplayName("Dado código da empresa e códigos de regionais, retorne uma lista de UnidadeVisualizacaoListagem.")
    void givenCodUnidadeAndCodRegionais_ThenReturnListUnidadeVisualizacaoListagemAndStatusOk() {
        final Long codEmpresa = 3L;
        final List<Long> codsRegionais = Collections.singletonList(1L);
        final ResponseEntity<List<UnidadeVisualizacaoListagemDto>> response =
                client.getUnidadesListagem(codEmpresa, codsRegionais);
        assertBaseValidations(response);
        response.getBody().stream()
                .map(Assertions::assertThat)
                .forEach(AbstractAssert::isNotNull);
    }

    @Nested
    class PersistenceTests {
        private UnidadeEntity baseEntity;

        @BeforeEach
        void setUp() {
            baseEntity = dao.findById(TEST_UNIDADE_ID).orElseThrow(NotFoundException::new);
        }

        @Test
        @DisplayName("Dado UnidadeEdicaoDto, retorne SuccessResponse")
        void givenUnidadeEdicaoDto_ThenReturnSuccessResponseAndStatusOk() {
            final UnidadeEdicaoDto dtoToUpdate =
                    UnidadeEdicaoDtoFactory.createValidUnidadeEdicaoDtoToUpdate(baseEntity);
            final ResponseEntity<SuccessResponse> response = client.updateUnidade(dtoToUpdate);
            assertBaseValidations(response);
            assertThat(response.getBody().getUniqueItemId()).isEqualTo(dtoToUpdate.getCodUnidade());
        }

        @Test
        @DisplayName("Dado DTO com código de unidade inválido, retorne BadRequestException")
        void givenUnidadeEdicaoDtoWithInvalidCodUnidade_ThenReturnBadRequestException() {
            final UnidadeEdicaoDto dtoWithInvalidCodUnidade =
                    UnidadeEdicaoDtoFactory.createUnidadeEdicaoDtoWithInvalidCodUnidade(baseEntity);
            final ResponseEntity<BadRequestException> response =
                    client.updateUnidade(dtoWithInvalidCodUnidade, BadRequestException.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getHttpStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }
    }
}