package test.br.com.zalf.prolog.webservice.pilares.frota.checklist.realizacao;

import br.com.zalf.prolog.webservice.commons.FonteDataHora;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.checklist.ChecklistService;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistResposta;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ChecklistModeloService;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AlternativaModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.AlternativaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.PerguntaModeloChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.ResultInsertModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.ModeloChecklistVisualizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.visualizacao.PerguntaModeloChecklistVisualizacao;
import org.junit.jupiter.api.Test;
import test.br.com.zalf.prolog.webservice.BaseTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testa a realização (responder) um checklist, tanto pelo fluxo offline quanto pelo online.
 *
 * Essa classe não testa o processemento das alternativas para abertura (ou não) de OSs. O foco é verificar se os dados
 * respondidos são salvos como deveriam.
 *
 * Para esse teste funcionar corretamente em repetidas execuções, é necessário dropar um index da tabela
 * CHECKLIST_MODELO:
 * > drop index checklist_modelo_data_nome_index;
 *
 * Created on 2019-10-14
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ChecklistRealizacaoTest extends BaseTest {
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

    @Test
    public void insereChecklistOnline_buscaParaComparar_deveTerInfosIguais() {
        final List<PerguntaModeloChecklistInsercao> perguntas = new ArrayList<>();
        {
            // P1.

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // A1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "A2",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    false));
            // A2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.CRITICA,
                    true,
                    2,
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

            final List<AlternativaModeloChecklistInsercao> alternativas = new ArrayList<>();
            // B1.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "B2",
                    PrioridadeAlternativa.ALTA,
                    false,
                    1,
                    true));
            // B2.
            alternativas.add(new AlternativaModeloChecklistInsercao(
                    "Outros",
                    PrioridadeAlternativa.BAIXA,
                    true,
                    2,
                    false));

            perguntas.add(new PerguntaModeloChecklistInsercao(
                    "P2",
                    null,
                    2,
                    false,
                    alternativas));
        }

        final Long codUnidade = 5L;
        final String nomeModelo = "$Teste Método$";
        // 4 - Então inserimos o modelo.
        final ResultInsertModeloChecklist result =
                service.insertModeloChecklist(
                        new ModeloChecklistInsercao(
                                nomeModelo,
                                codUnidade,
                                Collections.emptyList(),
                                Collections.emptyList(),
                                perguntas),
                        token);

        // 5 - Agora buscamos o modelo inserido.
        final ModeloChecklistVisualizacao modeloBuscado = service.getModeloChecklist(
                codUnidade,
                result.getCodModeloChecklistInserido());


        // Responde o checklist.
        final List<ChecklistResposta> respostas = new ArrayList<>();

        {
            // Responde a P1 - ela É single_choice.
            final PerguntaModeloChecklistVisualizacao p1 = modeloBuscado.getPerguntas().get(0);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            // A1.
            final AlternativaModeloChecklist a1 = p1.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    a1.getCodigo(),
                    true,
                    false,
                    null));

            // A2.
            final AlternativaModeloChecklist a2 = p1.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    a2.getCodigo(),
                    false,
                    true,
                    null));

            respostas.add(new ChecklistResposta(p1.getCodigo(), alternativas));
        }

        {
            // Responde a P2 - ela NÃO É single_choice.
            final PerguntaModeloChecklistVisualizacao p2 = modeloBuscado.getPerguntas().get(1);
            final List<ChecklistAlternativaResposta> alternativas = new ArrayList<>();

            // B1.
            final AlternativaModeloChecklist b1 = p2.getAlternativas().get(0);
            alternativas.add(new ChecklistAlternativaResposta(
                    b1.getCodigo(),
                    true,
                    false,
                    null));

            // B2.
            final AlternativaModeloChecklist b2 = p2.getAlternativas().get(1);
            alternativas.add(new ChecklistAlternativaResposta(
                    b2.getCodigo(),
                    true,
                    true,
                    "Está com problema..."));

            respostas.add(new ChecklistResposta(p2.getCodigo(), alternativas));
        }

        final ChecklistInsercao insercao = new ChecklistInsercao(
                5L,
                1L,
                1L,
                2272L,
                CPF_TOKEN,
                3195L,
                "PRO0001",
                TipoChecklist.SAIDA,
                112,
                10000,
                respostas,
                ProLogDateParser.toLocalDateTime("2019-10-314T09:35:10"),
                FonteDataHora.LOCAL_CELULAR,
                80,
                83,
                "device didID",
                "deviceImei",
                10000,
                11000);

        final ChecklistService checklistService = new ChecklistService();
        final Long codChecklistInserido = checklistService.insert(token, insercao);

        {
            // Compara as propriedades do checklist inserido com o buscado.
            final Checklist checklist = checklistService.getByCod(codChecklistInserido, token);

            assertThat(checklist).isNotNull();
            assertThat(checklist.getCodigo()).isEqualTo(codChecklistInserido);
            assertThat(checklist.getCodModelo()).isEqualTo(1L);
            assertThat(checklist.getCodVersaoModeloChecklist()).isEqualTo(1L);
            assertThat(checklist.getColaborador().getCodigo()).isEqualTo(2272L);
            assertThat(checklist.getColaborador().getCpf()).isEqualTo(Long.parseLong(CPF_TOKEN));
            assertThat(checklist.getPlacaVeiculo()).isEqualTo("PRO0001");
            assertThat(checklist.getTipo()).isEqualTo(TipoChecklist.SAIDA.asChar());
            assertThat(checklist.getKmAtualVeiculo()).isEqualTo(112);
            assertThat(checklist.getTempoRealizacaoCheckInMillis()).isEqualTo(10000);
            assertThat(checklist.getData()).isEqualTo(ProLogDateParser.toLocalDateTime("2019-10-314T09:35:10"));
            assertThat(checklist.getQtdItensOk()).isEqualTo(0);
            assertThat(checklist.getQtdItensNok()).isEqualTo(2);
            assertThat(checklist.getQtdAlternativasOk()).isEqualTo(1);
            assertThat(checklist.getQtdAlternativasNok()).isEqualTo(4);
            {
                // Compara a P1.
                final PerguntaRespostaChecklist p1 = checklist.getListRespostas().get(0);

                // A1.
                final AlternativaChecklist a1 = p1.getAlternativasResposta().get(0);
                assertThat(a1.getAlternativa()).isEqualTo("A1");
                assertThat(a1.getRespostaOutros()).isNull();
                assertThat(a1.isTipoOutros()).isFalse();
                assertThat(a1.isSelected()).isTrue();
                assertThat(a1.getOrdemExibicao()).isEqualTo(1);

                // A2.
                final AlternativaChecklist a2 = p1.getAlternativasResposta().get(1);
                assertThat(a2.getAlternativa()).isEqualTo("Outros");
                assertThat(a2.getRespostaOutros()).isNull();
                assertThat(a2.isTipoOutros()).isTrue();
                assertThat(a2.isSelected()).isFalse();
                assertThat(a2.getOrdemExibicao()).isEqualTo(2);
            }
            {
                // Compara a P2.
                final PerguntaRespostaChecklist p2 = checklist.getListRespostas().get(1);

                // B1.
                final AlternativaChecklist b1 = p2.getAlternativasResposta().get(0);
                assertThat(b1.getAlternativa()).isEqualTo("B1");
                assertThat(b1.getRespostaOutros()).isNull();
                assertThat(b1.isTipoOutros()).isFalse();
                assertThat(b1.isSelected()).isTrue();
                assertThat(b1.getOrdemExibicao()).isEqualTo(1);

                // B2.
                final AlternativaChecklist b2 = p2.getAlternativasResposta().get(1);
                assertThat(b2.getAlternativa()).isEqualTo("Outros");
                assertThat(b2.getRespostaOutros()).isEqualTo("Está com problema...");
                assertThat(b2.isTipoOutros()).isTrue();
                assertThat(b2.isSelected()).isTrue();
                assertThat(b2.getOrdemExibicao()).isEqualTo(2);
            }
        }
    }

    @Test
    public void insereChecklistOffline_buscaParaComparar_deveTerInfosIguais() {

    }
}