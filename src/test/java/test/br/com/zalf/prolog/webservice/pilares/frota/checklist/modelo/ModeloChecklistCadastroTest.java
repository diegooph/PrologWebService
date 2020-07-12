package test.br.com.zalf.prolog.webservice.pilares.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.AlternativaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created on 2019-09-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ModeloChecklistCadastroTest extends BaseTest {
    private static final String CPF_TOKEN = "03383283194";
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
    void caso1_insertDePerguntaSemAlternativaTipoOutros_deveDarErro() {
        // 1, 2, 3 - Criamos duas perguntas, a segunda sem alternativa tipo_outros.
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();
        {
            // P1.
            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.BAIXA,
                    true,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO));
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
            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    1L,
                    2,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    Collections.emptyList()));
        }

        final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                "Teste",
                5L,
                Collections.emptyList(),
                Collections.emptyList(),
                perguntas);

        // 4 - Então inserimos o modelo.
        assertThrows(ProLogException.class, () -> service.insertModeloChecklist(modelo, token));
    }

    @Test
    void caso2_insertSemPerguntas_deveDarErro() {
        // 1 - Primeiro criamos um modelo sem perguntas.
        final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                "Teste",
                5L,
                Collections.emptyList(),
                Collections.emptyList(),
                // Essa última lista vazia que vai causar o erro.
                Collections.emptyList());

        // 2 - Então tentamos inserir esse modelo, deve dar erro.
        assertThrows(ProLogException.class, () -> service.insertModeloChecklist(modelo, token));
    }

    @Test
    void caso3_insertAlternativaTipoOutrosComDescricaoForaDoPadrao_deveDarErro() {
        // 1, 2 - Criamos uma perguntas com uma alternativa tipo_outros tendo descrição fora do padrão.
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();
        {
            // P1.
            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "FORA_DO_PADRAO",
                    PrioridadeAlternativa.BAIXA,
                    true,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO));
            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    null,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        // 3 - Então inserimos o modelo.
        final Long codUnidade = 5L;
        final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                "Teste",
                codUnidade,
                Collections.emptyList(),
                Collections.emptyList(),
                perguntas);
        assertThrows(ProLogException.class, () -> service.insertModeloChecklist(modelo, token));
    }

    @Test
    void caso4_insertDeUmModeloCompleto_deveInserirEBaterAsInformacoes() {
        // 1, 2, 3 - Criamos duas perguntas, a segunda sem alternativa tipo_outros.
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();
        {
            // P1.

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();

            // A1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "A1",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO));

            // A2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.CRITICA,
                    true,
                    2,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO));

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
                    "B1",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    true,
                    AnexoMidiaChecklistEnum.BLOQUEADO));

            // B2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.BAIXA,
                    true,
                    2,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    null,
                    2,
                    false,
                    AnexoMidiaChecklistEnum.BLOQUEADO,
                    alternativas));
        }

        final Long codUnidade = 5L;
        final String nomeModelo = UUID.randomUUID().toString();
        final ResultInsertModeloChecklist result;
        {
            final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                    nomeModelo,
                    codUnidade,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    perguntas);

            // 4 - Então inserimos o modelo.
            result = service.insertModeloChecklist(modelo, token);
        }

        // 5 - Agora buscamos o modelo inserido.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                codUnidade,
                result.getCodModeloChecklistInserido());

        // 6 - Deve conter duas perguntas e ser da versão 1.
        assertThat(modeloBuscado).isNotNull();
        assertThat(modeloBuscado.getPerguntas()).hasSize(2);
        assertThat(modeloBuscado.getCodUnidade()).isEqualTo(codUnidade);
        assertThat(modeloBuscado.getNome()).isEqualTo(nomeModelo);
        assertThat(modeloBuscado.getCodModelo()).isEqualTo(result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado.getCodVersaoModelo()).isEqualTo(result.getCodVersaoModeloChecklistInserido());

        // Cada pergunta deve ter apenas uma alternativa do tipo_outros.
        {
            final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
            assertThat(p1.getDescricao()).isEqualTo("P1");
            assertThat(p1.getCodImagem()).isEqualTo(1L);
            assertThat(p1.getOrdemExibicao()).isEqualTo(1);
            assertThat(p1.isSingleChoice()).isTrue();
            assertThat(p1.getAlternativas()).hasSize(2);

            {
                final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
                assertThat(a1.isTipoOutros()).isFalse();
                assertThat(a1.getDescricao()).isEqualTo("A1");
                assertThat(a1.isDeveAbrirOrdemServico()).isFalse();
                assertThat(a1.getPrioridade()).isEqualTo(PrioridadeAlternativa.ALTA);
                assertThat(a1.getOrdemExibicao()).isEqualTo(1);
            }

            {
                final AlternativaModeloChecklist a2 = p1.getAlternativas().get(1);
                assertThat(a2.isTipoOutros()).isTrue();
                assertThat(a2.getDescricao()).isEqualTo("Outros");
                assertThat(a2.isDeveAbrirOrdemServico()).isTrue();
                assertThat(a2.getPrioridade()).isEqualTo(PrioridadeAlternativa.CRITICA);
                assertThat(a2.getOrdemExibicao()).isEqualTo(2);
            }
        }

        {
            final PerguntaModeloChecklistVisualizacao p2 = modeloBuscado.getPerguntas().get(1);
            assertThat(p2.getDescricao()).isEqualTo("P2");
            assertThat(p2.getCodImagem()).isNull();
            assertThat(p2.getOrdemExibicao()).isEqualTo(2);
            assertThat(p2.isSingleChoice()).isFalse();
            assertThat(p2.getAlternativas()).hasSize(2);

            {
                final AlternativaModeloChecklist b1 = p2.getAlternativas().get(0);
                assertThat(b1.isTipoOutros()).isFalse();
                assertThat(b1.getDescricao()).isEqualTo("B1");
                assertThat(b1.isDeveAbrirOrdemServico()).isTrue();
                assertThat(b1.getPrioridade()).isEqualTo(PrioridadeAlternativa.ALTA);
                assertThat(b1.getOrdemExibicao()).isEqualTo(1);
            }

            {
                final AlternativaModeloChecklist b2 = p2.getAlternativas().get(1);
                assertThat(b2.isTipoOutros()).isTrue();
                assertThat(b2.getDescricao()).isEqualTo("Outros");
                assertThat(b2.isDeveAbrirOrdemServico()).isFalse();
                assertThat(b2.getPrioridade()).isEqualTo(PrioridadeAlternativa.BAIXA);
                assertThat(b2.getOrdemExibicao()).isEqualTo(2);
            }
        }
    }
}