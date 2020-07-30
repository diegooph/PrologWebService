package test.br.com.zalf.prolog.webservice.pilares.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.AlternativaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 2019-10-09
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class ModeloChecklistSelecaoTest extends BaseTest {
    private static final String CPF_TOKEN = "03383283194";
    private static final Long COD_CARGO = 951L;
    private static final Long COD_TIPO_VEICULO_COM_PLACAS = 63L;
    private ChecklistModeloService service;
    private String token;

    @Override
    @BeforeAll
    public void initialize() throws Throwable {
        DatabaseManager.init();
        token = getValidToken(CPF_TOKEN);
        service = new ChecklistModeloService();
    }

    @Override
    @AfterAll
    public void destroy() {
        DatabaseManager.finish();
        service = null;
    }

    @Test
    @DisplayName("Insere modelo checklist e seleciona, informações devem ser iguais")
    public void insereModeloChecklistCompleto_entaoSeleciona_informacoesDevemSerIguais() {
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();
        {
            // P1.
            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // A1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.CRITICA,
                    true,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));
            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    1L,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        final String nomeModelo = UUID.randomUUID().toString();
        final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                nomeModelo,
                5L,
                Collections.singletonList(COD_TIPO_VEICULO_COM_PLACAS),
                Collections.singletonList(COD_CARGO),
                perguntas);

        final ResultInsertModeloChecklist result = service.insertModeloChecklist(modelo, token);

        final List<ModeloChecklistSelecao> modelos = service.getModelosSelecaoRealizacao(5L, COD_CARGO, token);
        assertThat(modelos).isNotNull();
        assertThat(modelos).hasSizeGreaterThan(1);
        assertThat(modelos)
                .extracting(
                        ModeloChecklistSelecao::getNomeModelo,
                        ModeloChecklistSelecao::getCodModelo,
                        ModeloChecklistSelecao::getCodUnidadeModelo)
                .contains(Assertions.tuple(
                        nomeModelo,
                        result.getCodModeloChecklistInserido(),
                        5L));
        assertThat(modelos)
                .extracting(ModeloChecklistSelecao::getVeiculosVinculadosModelo)
                .hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("Insere modelo checklist sem cargo e seleciona, não deve retornar o modelo inserido")
    public void insereModeloChecklistSemCargoVinculado_entaoSeleciona_modeloNaoDeveEstarPresente() {
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();
        {
            // P1.
            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // A1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.CRITICA,
                    true,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));
            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    1L,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                UUID.randomUUID().toString(),
                5L,
                Collections.emptyList(),
                Collections.singletonList(COD_CARGO),
                perguntas);

        final ResultInsertModeloChecklist result = service.insertModeloChecklist(modelo, token);

        final List<ModeloChecklistSelecao> modelos = service.getModelosSelecaoRealizacao(5L, COD_CARGO, token);
        assertThat(modelos).isNotNull();
        assertThat(modelos).hasSizeGreaterThan(0);
        assertThat(modelos)
                .extracting(ModeloChecklistSelecao::getCodModelo)
                .doesNotContain(result.getCodModeloChecklistInserido());
        assertThat(modelos)
                .extracting(ModeloChecklistSelecao::getCodVersaoModelo)
                .doesNotContain(result.getCodVersaoModeloChecklistInserido());
    }

    @Test
    @DisplayName("Insere modelo checklist sem tipo veículo e seleciona, não deve retornar o modelo inserido")
    public void insereModeloChecklistSemTipoVeiculoVinculado_entaoSeleciona_modeloNaoDeveEstarPresente() {
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();
        {
            // P1.
            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // A1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.CRITICA,
                    true,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));
            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    1L,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                UUID.randomUUID().toString(),
                5L,
                Collections.singletonList(COD_TIPO_VEICULO_COM_PLACAS),
                Collections.emptyList(),
                perguntas);

        final ResultInsertModeloChecklist result = service.insertModeloChecklist(modelo, token);

        final List<ModeloChecklistSelecao> modelos = service.getModelosSelecaoRealizacao(5L, COD_CARGO, token);
        assertThat(modelos).isNotNull();
        assertThat(modelos).hasSizeGreaterThan(0);
        assertThat(modelos)
                .extracting(ModeloChecklistSelecao::getCodModelo)
                .doesNotContain(result.getCodModeloChecklistInserido());
        assertThat(modelos)
                .extracting(ModeloChecklistSelecao::getCodVersaoModelo)
                .doesNotContain(result.getCodVersaoModeloChecklistInserido());
    }
}