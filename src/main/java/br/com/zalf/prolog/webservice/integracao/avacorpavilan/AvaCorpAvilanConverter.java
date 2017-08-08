package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.PlacaModeloHolder;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.ArrayOfMedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.MedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.MoreCollectors;
import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanUtils.createDatePattern;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Transforma os objetos utilizados pelo AvaCorp em entidades do ProLog e vice-versa.
 */
@VisibleForTesting
public final class AvaCorpAvilanConverter {

    private AvaCorpAvilanConverter() {
        throw new IllegalStateException(AvaCorpAvilanConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @VisibleForTesting
    public static List<Veiculo> convert(@NotNull final ArrayOfVeiculo arrayOfVeiculo) {
        checkNotNull(arrayOfVeiculo, "arrayOfVeiculo não pode ser null!");

        final List<br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo> veiculosAvilan
                = arrayOfVeiculo.getVeiculo();

        final List<Veiculo> veiculos = new ArrayList<>();
        veiculosAvilan.forEach(v -> veiculos.add(convert(v)));

        return veiculos;
    }

    @VisibleForTesting
    public static Veiculo convert(
            @NotNull final br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo veiculoAvilan) {
        checkNotNull(veiculoAvilan, "veiculoAvilan não pode ser null!");

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(veiculoAvilan.getPlaca());
        veiculo.setKmAtual(veiculoAvilan.getMarcador());
        return veiculo;
    }

    @VisibleForTesting
    public static CronogramaAfericao convert(@NotNull final ArrayOfVeiculo arrayOfVeiculo,
                                             @NotNull final Restricao restricao) {
        checkNotNull(arrayOfVeiculo, "arrayOfVeiculo não pode ser null!");
        checkNotNull(restricao, "restricao não pode ser null!");

        final CronogramaAfericao cronogramaAfericao = new CronogramaAfericao();
        cronogramaAfericao.setMeta(restricao.getPeriodoDiasAfericao());
        final List<PlacaModeloHolder> modelos = new ArrayList<>();

        Map<String, List<br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo>> modelosVeiculos =
                arrayOfVeiculo
                        .getVeiculo()
                        .stream()
                        .collect(Collectors.groupingBy(v -> v.getModelo()));

        modelosVeiculos.forEach((modeloVeiculo, veiculos) -> {
            final PlacaModeloHolder modeloHolder = new PlacaModeloHolder();
            modeloHolder.setModelo(modeloVeiculo);
            final List<PlacaModeloHolder.PlacaStatus> placas = new ArrayList<>();
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < veiculos.size(); i++) {
                final br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo v = veiculos.get(i);
                final PlacaModeloHolder.PlacaStatus placaStatus = new PlacaModeloHolder.PlacaStatus();
                placaStatus.placa = v.getPlaca();
                placaStatus.quantidadePneus = v.getQuantidadePneu();
                if (Strings.isNullOrEmpty(v.getDtUltimaAfericao())) {
                    // Veículo nunca foi aferido.
                    placaStatus.intervaloUltimaAfericao = PlacaModeloHolder.PlacaStatus.INTERVALO_INVALIDO;
                } else {
                    placaStatus.intervaloUltimaAfericao = AvaCorpAvilanUtils.calculateDaysBetweenDateAndNow(v.getDtUltimaAfericao());
                }
                placas.add(placaStatus);
            }
            modeloHolder.setPlacaStatus(placas);
            modelos.add(modeloHolder);
        });

        cronogramaAfericao.setPlacas(modelos);

        return cronogramaAfericao;
    }

    @VisibleForTesting
    public static IncluirMedida2 convert(@NotNull final Afericao afericao) {
        checkNotNull(afericao, "afericao não pode ser null!");

        final IncluirMedida2 incluirMedida2 = new IncluirMedida2();

        // seta valores
        incluirMedida2.setVeiculo(afericao.getVeiculo().getPlaca());
        incluirMedida2.setTipoMarcador(AvaCorpAvilanTipoMarcador.HODOMETRO);
        incluirMedida2.setMarcador(Math.toIntExact(afericao.getVeiculo().getKmAtual()));
        incluirMedida2.setDataMedida(createDatePattern(afericao.getDataHora()));
        // Placas carreta 1, 2 e 3 nunca serão setadas. No ProLog apenas um veículo será aferido por vez. Caso a carreta
        // seja aferida, então a placa dela será setada em .setVeiculo()

        ArrayOfMedidaPneu medidas = new ArrayOfMedidaPneu();
        for (Pneu pneu : afericao.getVeiculo().getListPneus()) {
            final MedidaPneu medidaPneu = new MedidaPneu();
            medidaPneu.setCalibragem(pneu.getPressaoAtual());
            medidaPneu.setNumeroFogoPneu(pneu.getCodigo());
            medidaPneu.setTriangulo1PrimeiroSulco(pneu.getSulcosAtuais().getExterno());
            medidaPneu.setTriangulo1SegundoSulco(pneu.getSulcosAtuais().getCentralExterno());
            medidaPneu.setTriangulo1TerceiroSulco(pneu.getSulcosAtuais().getCentralInterno());
            medidaPneu.setTriangulo1QuartoSulco(pneu.getSulcosAtuais().getInterno());
            medidas.getMedidaPneu().add(medidaPneu);
        }
        incluirMedida2.setMedidas(medidas);

        return incluirMedida2;
    }

    @VisibleForTesting
    public static Map<ModeloChecklist, List<String>> convert(ArrayOfQuestionarioVeiculos arrayOfQuestionarioVeiculos) {
        checkNotNull(arrayOfQuestionarioVeiculos, "arrayOfQuestionarioVeiculos não pode ser null!");

        final Map<ModeloChecklist, List<String>> map = new HashMap<>();

        for (QuestionarioVeiculos questionarioVeiculos : arrayOfQuestionarioVeiculos.getQuestionarioVeiculos()) {
            // Cria modelo de checklist
            final ModeloChecklist modeloChecklist = new ModeloChecklist();
            final Questionario questionario = questionarioVeiculos.getQuestionario();
            modeloChecklist.setCodigo(Long.valueOf(questionario.getCodigoQuestionario()));
            modeloChecklist.setNome(questionario.getDescricao());

            // cria placa dos veículos
            final List<String> placasVeiculo = new ArrayList<>();
            questionarioVeiculos.getVeiculos().getVeiculo().forEach(veiculo -> placasVeiculo.add(veiculo.getPlaca()));

            map.put(modeloChecklist, placasVeiculo);
        }

        return map;
    }

    @VisibleForTesting
    public static NovoChecklistHolder convert(ArrayOfVeiculoQuestao veiculosQuestoes, String placaVeiculo) {
        checkNotNull(veiculosQuestoes, "veiculosQuestoes não pode ser null!");
        checkNotNull(placaVeiculo, "placaVeiculo não pode ser null!");

        final NovoChecklistHolder novoChecklistHolder = new NovoChecklistHolder();

        // Seta informações Veículo
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(placaVeiculo);
        // Recebemos uma lista de VeiculoQuestao, pois se buscamos pela placa de um cavalo e existam carretas atreladas
        // a ele, as perguntas para essas carretas também são enviadas. Nós, porém, ignoramos qualquer outra placa.
        final VeiculoQuestao veiculoQuestao = veiculosQuestoes
                .getVeiculoQuestao()
                .stream()
                .filter(v -> v.getVeiculo().getPlaca().equals(placaVeiculo))
                .collect(MoreCollectors.onlyElement());
        // Seta o km do veículo
        veiculo.setKmAtual(veiculoQuestao.getVeiculo().getMarcador());
        novoChecklistHolder.setVeiculo(veiculo);

        // Cria as perguntas/respostas do checklist
        final List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
        final ArrayOfQuestao arrayOfQuestao = veiculoQuestao.getQuestoes();
        for (final Questao questao : arrayOfQuestao.getQuestao()) {
            // Todas as questões em uma mesma lista possuem o mesmo código de avaliação.
            // Deixamos setar para cada iteração porque assim é mais fácil
            novoChecklistHolder.setCodigoModeloChecklist((long) questao.getCodigoAvaliacao());

            final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
            pergunta.setCodigo((long) questao.getSequenciaQuestao());
            pergunta.setPergunta(questao.getDescricao());

            final List<AlternativaChecklist> alternativas = new ArrayList<>();

            Resposta respostaOk = null;
            Resposta respostaNok = null;
            for (int i = 0; i < questao.getRespostas().getResposta().size(); i++) {
                final Resposta resposta = questao.getRespostas().getResposta().get(i);
                if (resposta.getDescricao().trim().equalsIgnoreCase("OK")) {
                   respostaOk = resposta;
                } else if (resposta.getDescricao().trim().equalsIgnoreCase("NOK")) {
                    respostaNok = resposta;
                }
            }

            if (respostaOk == null || respostaNok == null) {
                throw new IllegalStateException("Resposta OK ou NOK não presente na pergunta");
            }

            // Nós vamos ignorar quaisquer alternativas que sejam enviadas e vamos sempre enviar apenas uma alternativa:
            // a de tipo OUTROS. Que permite ao colaborador digitar a resposta.
            final AlternativaChecklist alternativa = new AlternativaChecklist();
            alternativa.setOrdemExibicao(respostaOk.getCodigoResposta());
            alternativa.setCodigo(respostaNok.getCodigoResposta());
            alternativa.setTipo(Alternativa.TIPO_OUTROS);
            alternativas.add(alternativa);

            // Sempre single choice
            pergunta.setSingleChoice(true);
            pergunta.setAlternativasResposta(alternativas);
            perguntas.add(pergunta);
        }
        novoChecklistHolder.setListPerguntas(perguntas);

        return novoChecklistHolder;
    }

    @VisibleForTesting
    public static RespostasAvaliacao convert(Checklist checklist, String cpf, String dataNascimento) {
        checkNotNull(checklist, "checklist não pode ser null!");

        final RespostasAvaliacao respostasAvaliacao = new RespostasAvaliacao();
        respostasAvaliacao.setCpf(cpf);
        respostasAvaliacao.setDtNascimento(dataNascimento);
        respostasAvaliacao.setOdometro(Math.toIntExact(checklist.getKmAtualVeiculo()));
        respostasAvaliacao.setCodigoAvaliacao(Math.toIntExact(checklist.getCodModelo()));
        final ArrayOfRespostaAval arrayOfRespostaAval = new ArrayOfRespostaAval();
        for (PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {

            final RespostaAval respostaAval = new RespostaAval();
            respostaAval.setSequenciaQuestao(Math.toIntExact(resposta.getCodigo()));
            // Sempre terá apenas uma alternativa
            final Alternativa alternativa = resposta.getAlternativasResposta().get(0);
            if (resposta.respondeuOk()) {
                respostaAval.setCodigoResposta(alternativa.getOrdemExibicao());
            } else {
                respostaAval.setCodigoResposta(Math.toIntExact(alternativa.getCodigo()));
                respostaAval.setObservacao(alternativa.getRespostaOutros());
            }

            arrayOfRespostaAval.getRespostaAval().add(respostaAval);
        }
        respostasAvaliacao.setRespostas(arrayOfRespostaAval);
        return respostasAvaliacao;
    }

    @VisibleForTesting
    public static List<Pneu> convert(ArrayOfPneu arrayOfPneu) {
        checkNotNull(arrayOfPneu, "arrayOfPneu não pode ser null!");
        final List<Pneu> pneus = new ArrayList<>();

        // Modelo de Pneu e Banda serão iguais para todos os pneus. Pneus sempre terão 4 sulcos.
        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setQuantidadeSulcos(4);
        final ModeloBanda modeloBanda = new ModeloBanda();
        modeloBanda.setQuantidadeSulcos(4);
        final Banda banda = new Banda();
        banda.setModelo(modeloBanda);

        for (br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Pneu p : arrayOfPneu.getPneu()) {
            final Pneu pneu = new Pneu();
            pneu.setCodigo(p.getNumeroFogo());
            pneu.setPosicao(AvilanPosicaoPneuMapper.mapToProLog(p.getPosicao()));
            // A vida atual do pneu começa em 1 quando ele é novo, porém, o getVidaPneu() retorna, na verdade, o
            // número de recapagens desse pneu, por isso somamos 1 ao total para ter a informação correta do modo
            // que é utilizado no ProLog
            pneu.setVidaAtual(p.getVidaPneu() + 1);
            final Sulcos sulcos = new Sulcos();
            sulcos.setExterno(p.getSulco1());
            sulcos.setCentralExterno(p.getSulco2());
            sulcos.setCentralInterno(p.getSulco3());
            sulcos.setInterno(p.getSulco4());
            pneu.setSulcosAtuais(sulcos);
            pneu.setModelo(modeloPneu);
            pneu.setBanda(banda);
            pneus.add(pneu);
        }

        // Ordena lista pelas posições do ProLog.
        pneus.sort(Pneu.POSICAO_PNEU_COMPARATOR);

        return pneus;
    }
}