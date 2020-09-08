package test.br.com.zalf.prolog.webservice.pilares.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.AlternativaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.AlternativaRealizacaoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.PerguntaRealizacaoChecklist;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoService;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
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
public class ModeloChecklistRealizacaoTest extends BaseTest {
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
    @DisplayName("Insere modelo checklist e busca para realização, informações devem ser iguais")
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

        {
            // P2.
            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();

            // B1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "B2",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));

            // B2
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.BAIXA,
                    true,
                    2,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO, "TESTE"));
            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    null,
                    2,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        final String nomeModelo = UUID.randomUUID().toString();
        final ResultInsertModeloChecklist result = service.insertModeloChecklist(
                new ModeloChecklistInsercao(
                        nomeModelo,
                        5L,
                        Collections.singletonList(COD_TIPO_VEICULO_COM_PLACAS),
                        Collections.singletonList(COD_CARGO),
                        perguntas),
                token);

        final Veiculo veiculo = new VeiculoService()
                .getVeiculosAtivosByUnidade(token, 5L, true)
                .stream()
                .filter(v -> v.getCodTipo().equals(COD_TIPO_VEICULO_COM_PLACAS))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhum veículo encontrado do tipo: " + COD_TIPO_VEICULO_COM_PLACAS));

        final ModeloChecklistRealizacao modelo = service.getModeloChecklistRealizacao(
                result.getCodModeloChecklistInserido(),
                veiculo.getCodigo(),
                veiculo.getPlaca(),
                TipoChecklist.RETORNO.asString(),
                token);

        assertThat(modelo).isNotNull();
        assertThat(modelo).hasNoNullFieldsOrProperties();
        assertThat(modelo.getNomeModelo()).isEqualTo(nomeModelo);
        assertThat(modelo.getCodModelo()).isEqualTo(result.getCodModeloChecklistInserido());
        assertThat(modelo.getCodVersaoModelo()).isEqualTo(result.getCodVersaoModeloChecklistInserido());
        assertThat(modelo.getCodUnidadeModelo()).isEqualTo(5L);
        assertThat(modelo.getVeiculoRealizacao().getCodVeiculo()).isEqualTo(veiculo.getCodigo());
        assertThat(modelo.getVeiculoRealizacao().getPlacaVeiculo()).isEqualTo(veiculo.getPlaca());

        assertThat(modelo.getPerguntas()).hasSize(2);
        {
            // P1.
            final PerguntaRealizacaoChecklist p1 = modelo.getPerguntas().get(0);
            assertThat(p1).isNotNull();
            assertThat(p1).hasNoNullFieldsOrProperties();
            assertThat(p1)
                    .isEqualToIgnoringGivenFields(
                            perguntas.get(0),
                            "codigo",
                            "urlImagem",
                            "alternativas");

            assertThat(p1.getAlternativas()).hasSize(1);
            final AlternativaRealizacaoChecklist a1 = p1.getAlternativas().get(0);
            assertThat(a1).isNotNull();
            assertThat(a1).hasNoNullFieldsOrProperties();
            assertThat(a1)
                    .isEqualToIgnoringGivenFields(
                            perguntas.get(0).getAlternativas().get(0),
                            "deveAbrirOrdemServico",
                            "codigo");
        }

        {
            // P2.
            final PerguntaRealizacaoChecklist p2 = modelo.getPerguntas().get(1);
            assertThat(p2).isNotNull();
            // Essa pergunta não tem imagem.
            assertThat(p2).hasNoNullFieldsOrPropertiesExcept("codImagem", "urlImagem");
            assertThat(p2)
                    .isEqualToIgnoringGivenFields(
                            perguntas.get(1),
                            "codigo",
                            "urlImagem",
                            "alternativas");

            assertThat(p2.getAlternativas()).hasSize(2);
            {
                final AlternativaRealizacaoChecklist b1 = p2.getAlternativas().get(0);
                assertThat(b1).isNotNull();
                assertThat(b1).hasNoNullFieldsOrProperties();
                assertThat(b1)
                        .isEqualToIgnoringGivenFields(
                                perguntas.get(1).getAlternativas().get(0),
                                "deveAbrirOrdemServico",
                                "codigo");
            }
            {
                final AlternativaRealizacaoChecklist b2 = p2.getAlternativas().get(1);
                assertThat(b2).isNotNull();
                assertThat(b2).hasNoNullFieldsOrProperties();
                assertThat(b2)
                        .isEqualToIgnoringGivenFields(
                                perguntas.get(1).getAlternativas().get(1),
                                "deveAbrirOrdemServico",
                                "codigo");
            }
        }
    }
}