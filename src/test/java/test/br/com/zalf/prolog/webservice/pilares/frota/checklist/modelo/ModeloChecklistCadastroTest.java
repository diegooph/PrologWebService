package test.br.com.zalf.prolog.webservice.pilares.frota.checklist.modelo;

import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.AlternativaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import org.junit.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Para esse teste funcionar corretamente em repetidas execuções, é necessário dropar um index da tabela
 * CHECKLIST_MODELO:
 * > drop index checklist_modelo_data_nome_index;
 *
 * Created on 2019-09-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class ModeloChecklistCadastroTest extends BaseTest {
    private static final String DEFAULT_DESCRICAO_TIPO_OUTROS = "Outros (Opção padrão)";
    private static final String CPF_TOKEN = "03383283194";
    private ChecklistModeloService service;
    private String token;

    @Override
    public void initialize() throws Throwable {
        DatabaseManager.init();
        token = getValidToken(CPF_TOKEN);
        service = new ChecklistModeloService();
    }

    @Override
    public void destroy() {
        DatabaseManager.finish();
        service = null;
    }

    @Test(expected = ProLogException.class)
    public void caso1_insertDePerguntaSemAlternativaTipoOutros_deveDarErro() {
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
                    true));
            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    1L,
                    1,
                    true,
                    alternativas));
        }

        {
            // P2.
            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    1L,
                    2,
                    true,
                    Collections.emptyList()));
        }

        final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                "Teste",
                5L,
                Collections.emptyList(),
                Collections.emptyList(),
                perguntas);

        // 4 - Então inserimos o modelo.
        service.insertModeloChecklist(modelo, token);
    }

    @Test(expected = ProLogException.class)
    public void caso2_insertSemPerguntas_deveDarErro() {
        // 1 - Primeiro criamos um modelo sem perguntas.
        final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                "Teste",
                5L,
                Collections.emptyList(),
                Collections.emptyList(),
                // Essa última lista vazia que vai causar o erro.
                Collections.emptyList());

        // 2 - Então tentamos inserir esse modelo, deve dar erro.
        service.insertModeloChecklist(modelo, token);
    }

    @Test(expected = ProLogException.class)
    public void caso3_insertAlternativaTipoOutrosComDescricaoForaDoPadrao_deveDarErro() {
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
                    true));
            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    null,
                    1,
                    true,
                    alternativas));
        }

        final Long codUnidade = 5L;
        final ResultInsertModeloChecklist result;
        {
            final ModeloChecklistInsercao modelo = new ModeloChecklistInsercao(
                    "Teste",
                    codUnidade,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    perguntas);

            // 3 - Então inserimos o modelo.
            result = service.insertModeloChecklist(modelo, token);
        }

        // 4 - Agora buscamos o modelo inserido.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                codUnidade,
                result.getCodModeloChecklistInserido());

        // 5 - Deve conter duas perguntas e ser da versão 1.
        assertThat(modeloBuscado).isNotNull();
        assertThat(modeloBuscado.getPerguntas()).hasSize(1);
        assertThat(modeloBuscado.getCodModelo()).isEqualTo(result.getCodModeloChecklistInserido());
        assertThat(modeloBuscado.getCodVersaoModelo()).isEqualTo(result.getCodVersaoModeloChecklistInserido());

        // 6 - A pergunta deve ter uma alternativa do tipo_outros com a descrição 'Outros'.
        final List<AlternativaModeloChecklist> alternativas = modeloBuscado.getPerguntas().get(0).getAlternativas();
        assertThat(alternativas).hasSize(1);
        final AlternativaModeloChecklist a = alternativas.get(0);
        assertThat(a.isTipoOutros()).isTrue();
        assertThat(a.getDescricao()).isEqualTo("Outros");
    }

    @Test
    public void caso4_insertDeUmModeloCompleto_deveInserirEBaterAsInformacoes() {
        // 1, 2, 3 - Criamos duas perguntas, a segunda sem alternativa tipo_outros.
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();
        {
            // P1.

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // A1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    DEFAULT_DESCRICAO_TIPO_OUTROS,
                    PrioridadeAlternativa.CRITICA,
                    true,
                    1,
                    true));

            // A2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "A2",
                    PrioridadeAlternativa.ALTA,
                    false,
                    2,
                    false));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P1",
                    1L,
                    1,
                    true,
                    alternativas));
        }

        {
            // P2.

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // B1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    DEFAULT_DESCRICAO_TIPO_OUTROS,
                    PrioridadeAlternativa.BAIXA,
                    true,
                    1,
                    false));

            // B2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "B2",
                    PrioridadeAlternativa.ALTA,
                    false,
                    2,
                    true));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    null,
                    2,
                    false,
                    alternativas));
        }

        final Long codUnidade = 5L;
        final String nomeModelo = "$Teste Método$";
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
                assertThat(a1.isTipoOutros()).isTrue();
                assertThat(a1.getDescricao()).isEqualTo("Outros (Opção padrão)");
                assertThat(a1.isDeveAbrirOrdemServico()).isTrue();
                assertThat(a1.getPrioridade()).isEqualTo(PrioridadeAlternativa.CRITICA);
                assertThat(a1.getOrdemExibicao()).isEqualTo(1);
            }

            {
                final AlternativaModeloChecklist a2 = p1.getAlternativas().get(1);
                assertThat(a2.isTipoOutros()).isFalse();
                assertThat(a2.getDescricao()).isEqualTo("A2");
                assertThat(a2.isDeveAbrirOrdemServico()).isFalse();
                assertThat(a2.getPrioridade()).isEqualTo(PrioridadeAlternativa.ALTA);
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
                assertThat(b1.isTipoOutros()).isTrue();
                assertThat(b1.getDescricao()).isEqualTo("Outros (Opção padrão)");
                assertThat(b1.isDeveAbrirOrdemServico()).isFalse();
                assertThat(b1.getPrioridade()).isEqualTo(PrioridadeAlternativa.BAIXA);
                assertThat(b1.getOrdemExibicao()).isEqualTo(1);
            }

            {
                final AlternativaModeloChecklist b2 = p2.getAlternativas().get(1);
                assertThat(b2.isTipoOutros()).isFalse();
                assertThat(b2.getDescricao()).isEqualTo("B2");
                assertThat(b2.isDeveAbrirOrdemServico()).isTrue();
                assertThat(b2.getPrioridade()).isEqualTo(PrioridadeAlternativa.ALTA);
                assertThat(b2.getOrdemExibicao()).isEqualTo(2);
            }
        }
    }
}