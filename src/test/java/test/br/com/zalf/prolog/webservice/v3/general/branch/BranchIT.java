package test.br.com.zalf.prolog.webservice.v3.general.branch;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.exception.BadRequestException;
import br.com.zalf.prolog.webservice.errorhandling.sql.NotFoundException;
import br.com.zalf.prolog.webservice.v3.general.branch.BranchDao;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchDto;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchUpdateDto;
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

public class BranchIT extends IntegrationTest {
    @Autowired
    private BranchApiClient client;
    @Autowired
    private BranchDao dao;

    @Test
    @DisplayName("Dado código da unidade, retorne a UnidadeVisualizacaoListagem correspondente.")
    void givenBranchIdToRequest_ThenReturnBranchesAndStatusOK() {
        final ResponseEntity<BranchDto> response = client.getBranchById(TEST_UNIDADE_ID);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCodUnidade()).isEqualTo(TEST_UNIDADE_ID);
    }

    @Test
    @DisplayName("Dado código da empresa e códigos de regionais, retorne uma lista de UnidadeVisualizacaoListagem.")
    void givenBranchIdAndGroupId_ThenReturnBranchesAndStatusOk() {
        final Long companyId = 3L;
        final List<Long> groupsId = Collections.singletonList(1L);
        final ResponseEntity<List<BranchDto>> response = client.getBranches(companyId, groupsId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        response.getBody().stream()
                .map(Assertions::assertThat)
                .forEach(AbstractAssert::isNotNull);
    }

    @Nested
    class PersistenceTests {
        private BranchEntity baseEntity;

        @BeforeEach
        void setUp() {
            baseEntity = dao.findById(TEST_UNIDADE_ID).orElseThrow(NotFoundException::new);
        }

        @Test
        @DisplayName("Dado UnidadeEdicaoDto, retorne SuccessResponse")
        void givenBranchUpdate_ThenReturnSuccessResponseAndStatusOk() {
            final BranchUpdateDto dtoToUpdate = BranchFactory.createValidBranchToUpdate(baseEntity);
            final ResponseEntity<SuccessResponse> response = client.updateBranch(dtoToUpdate);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getUniqueItemId()).isEqualTo(dtoToUpdate.getCodUnidade());
        }

        @Test
        @DisplayName("Dado DTO com código de unidade inválido, retorne BadRequestException")
        void givenBranchWithInvalidId_ThenReturnBadRequestException() {
            final BranchUpdateDto dtoWithInvalidCodUnidade = BranchFactory.createBranchWithInvalidId(baseEntity);
            final ResponseEntity<BadRequestException> response =
                    client.updateBranch(dtoWithInvalidCodUnidade, BadRequestException.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().getHttpStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        }
    }
}
