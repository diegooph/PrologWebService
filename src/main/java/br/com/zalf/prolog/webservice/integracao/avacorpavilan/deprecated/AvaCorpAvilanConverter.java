package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.AlternativaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.PerguntaRespostaChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolVeiculoDia;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.OLD.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ModeloPlacasAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.integracao.PosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.ItemOsIntegracao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.OsIntegracao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.afericao.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.ArrayOfPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.ArrayOfString;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.os._model.FechamentoOsAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.os._model.ItemOsAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.checklist.os._model.OsAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.data.TipoVeiculoAvilanProLog;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.MoreCollectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.AvaCorpAvilanUtils.*;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Transforma os objetos utilizados pelo AvaCorp em entidades do ProLog e vice-versa.
 */
@VisibleForTesting
public final class AvaCorpAvilanConverter {
    private static final int CODIGO_RESPOSTA_OK = 1;
    private static final int CODIGO_RESPOSTA_NOK = 2;

    private AvaCorpAvilanConverter() {
        throw new IllegalStateException(AvaCorpAvilanConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @VisibleForTesting
    public static List<Veiculo> convert(@NotNull final ArrayOfVeiculo arrayOfVeiculo,
                                        @NotNull final Long codUnidadeVeiculo) {
        checkNotNull(arrayOfVeiculo, "arrayOfVeiculo não pode ser null!");
        checkNotNull(codUnidadeVeiculo, "codUnidadeVeiculo não pode ser null!");

        final List<br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.Veiculo> veiculosAvilan
                = arrayOfVeiculo.getVeiculo();

        final List<Veiculo> veiculos = new ArrayList<>();
        veiculosAvilan.forEach(v -> veiculos.add(convert(v, codUnidadeVeiculo)));

        return veiculos;
    }

    @VisibleForTesting
    public static Veiculo convert(
            @NotNull final br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.Veiculo veiculoAvilan,
            @NotNull final Long codUnidadeVeiculo) {
        checkNotNull(veiculoAvilan, "veiculoAvilan não pode ser null!");
        checkNotNull(codUnidadeVeiculo, "codUnidadeVeiculo não pode ser null!");

        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(veiculoAvilan.getPlaca());
        veiculo.setKmAtual((long) veiculoAvilan.getMarcador());
        veiculo.setCodUnidadeAlocado(codUnidadeVeiculo);
        return veiculo;
    }

    @VisibleForTesting
    public static CronogramaAfericao convert(@NotNull final ArrayOfVeiculo arrayOfVeiculo,
                                             @NotNull final Restricao restricao,
                                             @NotNull final Long codUnidade) {
        checkNotNull(arrayOfVeiculo, "arrayOfVeiculo não pode ser null!");
        checkNotNull(restricao, "restricao não pode ser null!");
        checkNotNull(codUnidade, "codUnidade não pode ser null!");

        final CronogramaAfericao cronogramaAfericao = new CronogramaAfericao();
        cronogramaAfericao.setMetaAfericaoSulco(restricao.getPeriodoDiasAfericaoSulco());
        cronogramaAfericao.setMetaAfericaoPressao(restricao.getPeriodoDiasAfericaoPressao());
        final List<ModeloPlacasAfericao> modelos = new ArrayList<>();

        final Map<String, List<br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.Veiculo>> modelosVeiculos =
                arrayOfVeiculo
                        .getVeiculo()
                        .stream()
                        .collect(Collectors.groupingBy(v -> v.getModelo()));

        final LocalDateTime dataHoraUnidade;
        try {
            dataHoraUnidade = LocalDateTime.now(TimeZoneManager.getZoneIdForCodUnidade(codUnidade));
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        modelosVeiculos.forEach((modeloVeiculo, veiculos) -> {
            final ModeloPlacasAfericao modeloHolder = new ModeloPlacasAfericao();
            modeloHolder.setNomeModelo(modeloVeiculo);
            final List<ModeloPlacasAfericao.PlacaAfericao> placas = new ArrayList<>();
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < veiculos.size(); i++) {
                final br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.Veiculo v = veiculos.get(i);
                final ModeloPlacasAfericao.PlacaAfericao placaAfericao = new ModeloPlacasAfericao.PlacaAfericao();
                placaAfericao.setPlaca(v.getPlaca());
                placaAfericao.setCodUnidadePlaca(codUnidade);
                placaAfericao.setQuantidadePneus(v.getQuantidadePneu());
                placaAfericao.setFormaColetaDadosSulco(FormaColetaDadosAfericaoEnum.BLOQUEADO);
                placaAfericao.setFormaColetaDadosPressao(FormaColetaDadosAfericaoEnum.BLOQUEADO);
                placaAfericao.setFormaColetaDadosSulcoPressao(FormaColetaDadosAfericaoEnum.EQUIPAMENTO);
                placaAfericao.setPodeAferirEstepe(true);
                if (Strings.isNullOrEmpty(v.getDtUltimaAfericao())) {
                    // Veículo nunca foi aferido.
                    placaAfericao.setIntervaloUltimaAfericaoPressao(ModeloPlacasAfericao.PlacaAfericao
                            .INTERVALO_INVALIDO);
                    placaAfericao.setIntervaloUltimaAfericaoSulco(ModeloPlacasAfericao.PlacaAfericao
                            .INTERVALO_INVALIDO);
                } else {
                    placaAfericao.setIntervaloUltimaAfericaoPressao(AvaCorpAvilanUtils.calculateDaysBetweenDateAndNow
                            (v.getDtUltimaAfericao(), dataHoraUnidade));
                    placaAfericao.setIntervaloUltimaAfericaoSulco(AvaCorpAvilanUtils.calculateDaysBetweenDateAndNow(v
                            .getDtUltimaAfericao(), dataHoraUnidade));
                }
                placas.add(placaAfericao);
            }
            modeloHolder.setPlacasAfericao(placas);
            modelos.add(modeloHolder);
        });

        cronogramaAfericao.setModelosPlacasAfericao(modelos);

        return cronogramaAfericao;
    }

    @VisibleForTesting
    public static IncluirMedida2 convert(@NotNull final AfericaoPlaca afericaoPlaca) throws ParseException {
        checkNotNull(afericaoPlaca, "afericaoPlaca não pode ser null!");

        if (afericaoPlaca.getTipoMedicaoColetadaAfericao() != TipoMedicaoColetadaAfericao.SULCO_PRESSAO) {
            throw new IllegalStateException("Só é possível realizar aferições que sejam de Sulco e Pressão na " +
                    "integração com a Avilan. Tipo recebido: " + afericaoPlaca.getTipoMedicaoColetadaAfericao() +
                    " Veículo: " + afericaoPlaca.getVeiculo().getPlaca());
        }

        final IncluirMedida2 incluirMedida2 = new IncluirMedida2();

        // Seta valores.
        incluirMedida2.setCpfColaborador(String.valueOf(afericaoPlaca.getColaborador().getCpfAsString()));
        incluirMedida2.setVeiculo(afericaoPlaca.getVeiculo().getPlaca());
        incluirMedida2.setTipoMarcador(AvaCorpAvilanTipoMarcador.HODOMETRO);
        incluirMedida2.setMarcador(Math.toIntExact(afericaoPlaca.getVeiculo().getKmAtual()));
        incluirMedida2.setDataMedida(createDateTimePattern(Timestamp.valueOf(afericaoPlaca.getDataHora())));
        // Placas carreta 1, 2 e 3 nunca serão setadas. No ProLog apenas um veículo será aferido por vez. Caso a carreta
        // seja aferida, então a placa dela será setada em .setVeiculo().

        final ArrayOfMedidaPneu medidas = new ArrayOfMedidaPneu();
        for (final Pneu pneu : afericaoPlaca.getVeiculo().getListPneus()) {
            final MedidaPneu medidaPneu = new MedidaPneu();
            medidaPneu.setCalibragem(pneu.getPressaoAtualAsInt());
            medidaPneu.setNumeroFogoPneu(pneu.getCodigoCliente());
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
    public static Map<ModeloChecklist, List<String>> convert(final Long codUnidade, final ArrayOfQuestionarioVeiculos arrayOfQuestionarioVeiculos) {
        checkNotNull(arrayOfQuestionarioVeiculos, "arrayOfQuestionarioVeiculos não pode ser null!");

        final Map<ModeloChecklist, List<String>> map = new HashMap<>();

        for (final QuestionarioVeiculos questionarioVeiculos : arrayOfQuestionarioVeiculos.getQuestionarioVeiculos()) {
            // Cria modelo de checklist
            final ModeloChecklist modeloChecklist = new ModeloChecklist();
            final Questionario questionario = questionarioVeiculos.getQuestionario();
            modeloChecklist.setCodigo(Long.valueOf(questionario.getCodigoQuestionario()));
            modeloChecklist.setNome(questionario.getDescricao());
            modeloChecklist.setCodUnidade(codUnidade);

            // cria placa dos veículos
            final List<String> placasVeiculo = new ArrayList<>();
            questionarioVeiculos.getVeiculos().getVeiculo().forEach(veiculo -> placasVeiculo.add(veiculo.getPlaca()));

            map.put(modeloChecklist, placasVeiculo);
        }

        return map;
    }

    @VisibleForTesting
    public static NovoChecklistHolder convert(final ArrayOfVeiculoQuestao veiculosQuestoes,
                                              final Long codUnidade,
                                              final Map<Long, String> mapCodPerguntUrlImagem,
                                              final String placaVeiculo) {
        checkNotNull(veiculosQuestoes, "veiculosQuestoes não pode ser null!");
        checkNotNull(mapCodPerguntUrlImagem, "mapCodPerguntUrlImagem não pode ser null!");
        checkNotNull(placaVeiculo, "placaVeiculo não pode ser null!");

        final NovoChecklistHolder novoChecklistHolder = new NovoChecklistHolder();
        novoChecklistHolder.setCodUnidaedModeloChecklist(codUnidade);

        // Seta informações Veículo.
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(placaVeiculo);
        // Recebemos uma lista de VeiculoQuestao, pois se buscamos pela placa de um cavalo e existam carretas atreladas
        // a ele, as perguntas para essas carretas também são enviadas. Nós, porém, ignoramos qualquer outra placa.
        final VeiculoQuestao veiculoQuestao = veiculosQuestoes
                .getVeiculoQuestao()
                .stream()
                .filter(v -> v.getVeiculo().getPlaca().equals(placaVeiculo))
                .collect(MoreCollectors.onlyElement());
        // Seta o km do veículo.
        veiculo.setKmAtual((long) veiculoQuestao.getVeiculo().getMarcador());
        novoChecklistHolder.setVeiculo(veiculo);

        // Cria as perguntas/respostas do checklist.
        final List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
        final ArrayOfQuestao arrayOfQuestao = veiculoQuestao.getQuestoes();
        for (final Questao questao : arrayOfQuestao.getQuestao()) {
            // Todas as questões em uma mesma lista possuem o mesmo código de avaliação e nome.
            // Deixamos setar para cada iteração porque assim é mais fácil.
            novoChecklistHolder.setCodigoModeloChecklist((long) questao.getCodigoAvaliacao());
            novoChecklistHolder.setNomeModeloChecklist(questao.getDescricao());

            final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
            pergunta.setCodigo((long) questao.getSequenciaQuestao());
            pergunta.setPergunta(questao.getDescricao());
            pergunta.setUrl(mapCodPerguntUrlImagem.get((long) questao.getSequenciaQuestao()));

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
            // Poderia setar o código tanto da resposta OK quanto da NOK, pois na hora de sincronizar fixamos o código.
            alternativa.setCodigo(respostaNok.getCodigoResposta());
            alternativa.setPrioridade(PrioridadeAlternativa.BAIXA);
            alternativa.setTipo(Alternativa.TIPO_OUTROS);
            alternativa.setAlternativa("Outros");
            alternativas.add(alternativa);

            // Sempre single choice.
            pergunta.setSingleChoice(true);
            pergunta.setAlternativasResposta(alternativas);
            perguntas.add(pergunta);
        }
        novoChecklistHolder.setListPerguntas(perguntas);

        return novoChecklistHolder;
    }

    @VisibleForTesting
    public static RespostasAvaliacao convert(final Checklist checklist, final String cpf, final String dataNascimento) {
        checkNotNull(checklist, "checklist não pode ser null!");

        final RespostasAvaliacao respostasAvaliacao = new RespostasAvaliacao();
        respostasAvaliacao.setCpf(cpf);
        respostasAvaliacao.setDtNascimento(dataNascimento);
        respostasAvaliacao.setOdometro(Math.toIntExact(checklist.getKmAtualVeiculo()));
        respostasAvaliacao.setCodigoAvaliacao(Math.toIntExact(checklist.getCodModelo()));
        final ArrayOfRespostaAval arrayOfRespostaAval = new ArrayOfRespostaAval();
        for (final PerguntaRespostaChecklist resposta : checklist.getListRespostas()) {
            final RespostaAval respostaAval = new RespostaAval();
            respostaAval.setSequenciaQuestao(Math.toIntExact(resposta.getCodigo()));
            // Sempre terá apenas uma alternativa.
            final Alternativa alternativa = resposta.getAlternativasResposta().get(0);
            if (resposta.respondeuOk()) {
                respostaAval.setCodigoResposta(CODIGO_RESPOSTA_OK);
            } else {
                respostaAval.setCodigoResposta(CODIGO_RESPOSTA_NOK);
                respostaAval.setObservacao(alternativa.getRespostaOutros());
            }

            arrayOfRespostaAval.getRespostaAval().add(respostaAval);
        }
        respostasAvaliacao.setRespostas(arrayOfRespostaAval);
        return respostasAvaliacao;
    }

    @VisibleForTesting
    public static List<Pneu> convert(@Nonnull final PosicaoPneuMapper posicaoPneuMapper,
                                     @Nonnull final ArrayOfPneu arrayOfPneu) {
        checkNotNull(posicaoPneuMapper, "posicaoPneuMapper não pode ser null!");
        checkNotNull(arrayOfPneu, "arrayOfPneu não pode ser null!");
        final List<Pneu> pneus = new ArrayList<>();

        // Modelo de Pneu e Banda serão iguais para todos os pneus. Pneus sempre terão 4 sulcos.
        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setQuantidadeSulcos(4);
        final ModeloBanda modeloBanda = new ModeloBanda();
        modeloBanda.setQuantidadeSulcos(4);
        final Banda banda = new Banda();
        banda.setModelo(modeloBanda);

        for (int i = 0; i < arrayOfPneu.getPneu().size(); i++) {
            final br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.Pneu p = arrayOfPneu.getPneu().get(i);
            final PneuComum pneu = new PneuComum();
            pneu.setCodigo((long) i);
            pneu.setCodigoCliente(p.getNumeroFogo());
            pneu.setPosicao(posicaoPneuMapper.mapToProLog(p.getPosicao()));
            pneu.setPressaoCorreta(p.getPressaoRecomendada());
            // A vida atual do pneu começa em 1 quando ele é novo, porém, o getVidaPneu() retorna, na verdade, o
            // número de recapagens desse pneu, por isso somamos 1 ao total para ter a informação correta do modo
            // que é utilizado no ProLog.
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

    @VisibleForTesting
    public static DeprecatedFarolChecklist convert(final ArrayOfFarolDia farolDia) throws ParseException {
        checkNotNull(farolDia, "farolDia não pode ser null!");
        checkArgument(farolDia.getFarolDia().size() == 1, "farolDia não pode vir com mais de um elemento " +
                "pois estamos filtrando apenas por um único dia!");

        final List<DeprecatedFarolVeiculoDia> farolVeiculos = new ArrayList<>();
        final List<VeiculoChecklist> veiculosFarol = farolDia
                .getFarolDia()
                .get(0)
                .getVeiculosChecklist()
                .getVeiculoChecklist();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < veiculosFarol.size(); i++) {
            final VeiculoChecklist veiculoChecklist = veiculosFarol.get(i);
            // Cria Veículo.
            final Veiculo veiculo = new Veiculo();
            veiculo.setPlaca(veiculoChecklist.getPlaca());

            final List<Avaliacao> avaliacoes = veiculoChecklist.getAvaliacoes().getAvaliacao();
            Checklist checklistSaidaDia = null;
            Checklist checklistRetornoDia = null;
            if (avaliacoes != null && !avaliacoes.isEmpty()) {
                final List<Checklist> checklists = convertToChecklists(avaliacoes);
                checklistSaidaDia = getChecklistMaisAtualByTipo(checklists, Checklist.TIPO_SAIDA);
                checklistRetornoDia = getChecklistMaisAtualByTipo(checklists, Checklist.TIPO_RETORNO);
            }

            // Cria os itens críticos.
            List<ItemOrdemServico> itensCriticos = null;
            if (veiculoChecklist.getItensCriticos() != null) {
                itensCriticos = new ArrayList<>();
                final List<ItemCritico> itensAvilan = veiculoChecklist.getItensCriticos().getItemCritico();
                for (final ItemCritico itemCritico : itensAvilan) {
                    final ItemOrdemServico itemOrdemServico = new ItemOrdemServico();
                    itemOrdemServico.setStatus(ItemOrdemServico.Status.PENDENTE);
                    // TODO: Está com um bug onde retorna também os millis, por isso nós removemos caso venha.
                    if (itemCritico.getData().contains(".")) {
                        itemCritico.setData(itemCritico.getData().substring(0, itemCritico.getData().indexOf(".")));
                    }
                    itemOrdemServico.setDataApontamento(
                            AvaCorpAvilanUtils.createLocalDateTimePattern(itemCritico.getData()));

                    // Seta o nome do item com problema.
                    // Alternativa.
                    final AlternativaChecklist alternativa = new AlternativaChecklist();
                    alternativa.setAlternativa(itemCritico.getDescricao());
                    alternativa.setPrioridade(PrioridadeAlternativa.CRITICA);
                    final List<AlternativaChecklist> alternativas = new ArrayList<>();
                    alternativas.add(alternativa);
                    // Pergunta.
                    final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
                    pergunta.setAlternativasResposta(alternativas);
                    itemOrdemServico.setPergunta(pergunta);

                    itensCriticos.add(itemOrdemServico);
                }
            }

            farolVeiculos.add(new DeprecatedFarolVeiculoDia(veiculo, checklistSaidaDia, checklistRetornoDia, itensCriticos));
        }

        return new DeprecatedFarolChecklist(farolVeiculos);
    }

    @NotNull
    @VisibleForTesting
    public static List<String> convert(@NotNull final ArrayOfString placasVeiculos) {
        checkNotNull(placasVeiculos, "placasVeiculos não pode ser null!");

        // Não tem parse necessário nesse caso, basta retornarmos a própria lista de Strings que recebemos.
        return placasVeiculos.getString();
    }

    @NotNull
    @VisibleForTesting
    public static List<TipoVeiculo> convert(@NotNull final List<TipoVeiculoAvilanProLog> tiposVeiculosAvilanProLog) {
        checkNotNull(tiposVeiculosAvilanProLog, "tiposVeiculosAvilanProLog não pode ser null!");

        final List<TipoVeiculo> tiposVeiculosProLog = new ArrayList<>();
        for (final TipoVeiculoAvilanProLog tipoVeiculoAvilanProLog : tiposVeiculosAvilanProLog) {
            final TipoVeiculo tipoVeiculoProLog = new TipoVeiculo();
            tipoVeiculoProLog.setCodigo(tipoVeiculoAvilanProLog.getCodProLog());
            tipoVeiculoProLog.setNome(tipoVeiculoAvilanProLog.getDescricao());
            tiposVeiculosProLog.add(tipoVeiculoProLog);
        }

        return tiposVeiculosProLog;
    }

    @NotNull
    @VisibleForTesting
    public static Checklist convert(@NotNull final ChecklistFiltro checklistFiltro) throws ParseException {
        checkNotNull(checklistFiltro, "checklistFiltro não pode ser null!");

        final Checklist checklist = new Checklist();
        checklist.setCodigo((long) checklistFiltro.getCodigoChecklist());
        checklist.setCodModelo((long) checklistFiltro.getCodigoQuestionario());

        // Colaborador Checklist.
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(Long.parseLong(checklistFiltro.getColaborador().getCpf()));
        colaborador.setNome(checklistFiltro.getColaborador().getNome());
        checklist.setColaborador(colaborador);

        checklist.setData(createLocalDateTimePattern(checklistFiltro.getDataHoraRealizacao()));
        checklist.setKmAtualVeiculo(checklistFiltro.getOdometro());
        checklist.setPlacaVeiculo(checklistFiltro.getPlaca());
        checklist.setTipo(checklistFiltro.getTipo().asTipoProLog());
        checklist.setQtdItensOk(checklistFiltro.getQuantidadeRespostasOk());
        checklist.setQtdItensNok(checklistFiltro.getQuantidadeRespostasNaoOk());

        final AvaliacaoFiltro avaliacaoFiltro = checklistFiltro.getAvaliacao();
        // Como esse método é também usado para criar os checklists na busca de todos os checks, não haverá dados
        // da avaliação (respostas do check) nesse caso. Apenas quando é feita a busca do check pelo código. Por isso
        // verificamos.
        if (avaliacaoFiltro != null) {
            final List<QuestaoFiltro> questoes = avaliacaoFiltro.getQuestoes().getQuestaoFiltro();
            final List<PerguntaRespostaChecklist> perguntas = new ArrayList<>();
            for (final QuestaoFiltro questao : questoes) {
                final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
                pergunta.setPergunta(questao.getDescricao());

                final List<AlternativaFiltro> alternativasFiltro = questao
                        .getResposta()
                        .getAlternativas()
                        .getAlternativaFiltro();
                AlternativaFiltro alternativaOk = null;
                AlternativaFiltro alternativaNok = null;
                for (final AlternativaFiltro alternativaFiltro : alternativasFiltro) {
                    if (alternativaFiltro.getDescricao().trim().equalsIgnoreCase("OK")) {
                        alternativaOk = alternativaFiltro;
                    } else if (alternativaFiltro.getDescricao().trim().equalsIgnoreCase("NOK")) {
                        alternativaNok = alternativaFiltro;
                    }
                }

                if (alternativaOk == null || alternativaNok == null) {
                    throw new IllegalStateException("Resposta OK ou NOK não presente!!! Checklist fora do padrão.");
                }

                final AlternativaChecklist alternativaChecklist = new AlternativaChecklist();
                alternativaChecklist.setAlternativa("Outros");
                alternativaChecklist.setTipo(Alternativa.TIPO_OUTROS);
                // Caso tenha respondido como NOK na realização do checklist, o usuário foi obrigado a descrever o
                // problema. Essa descrição vem na observação da resposta.
                alternativaChecklist.setRespostaOutros(questao.getResposta().getObservacao());
                alternativaChecklist.selected = alternativaNok.isSelecionada();
                final List<AlternativaChecklist> alternativas = new ArrayList<>();
                alternativas.add(alternativaChecklist);
                pergunta.setAlternativasResposta(alternativas);

                // Sempre single choice.
                pergunta.setSingleChoice(true);
                perguntas.add(pergunta);
            }
            checklist.setListRespostas(perguntas);
        }

        return checklist;
    }

    @Nonnull
    @VisibleForTesting
    public static List<AfericaoPlaca> convertAfericoes(@NotNull final List<AfericaoFiltro> afericoesFiltro,
                                                       @NotNull final Long codUnidadeAfericao) throws ParseException {
        checkNotNull(afericoesFiltro, "afericoesFiltro não pode ser null!");

        final List<AfericaoPlaca> afericoes = new ArrayList<>();
        for (final AfericaoFiltro afericaoFiltro : afericoesFiltro) {
            afericoes.add(convertAfericaoSemPneus(afericaoFiltro, codUnidadeAfericao));
        }
        return afericoes;
    }


    @Nonnull
    @VisibleForTesting
    public static AfericaoPlaca convert(@Nonnull final PosicaoPneuMapper posicaoPneuMapper,
                                        @NotNull final AfericaoFiltro afericaoFiltro,
                                        @NotNull final Long codUnidadeAfericao) throws ParseException {
        checkNotNull(posicaoPneuMapper, "posicaoPneuMapper não pode ser null!");
        checkNotNull(afericaoFiltro, "afericaoFiltro não pode ser null!");

        final AfericaoPlaca afericaoPlaca = convertAfericaoSemPneus(afericaoFiltro, codUnidadeAfericao);

        // Pneus - Medidas.
        final List<Pneu> pneus = new ArrayList<>();
        final List<PneuFiltro> pneusAvilan = afericaoFiltro.getPneus().getPneuFiltro();
        for (int i = 0; i < pneusAvilan.size(); i++) {
            final PneuFiltro pneuFiltro = pneusAvilan.get(i);
            final PneuComum pneu = new PneuComum();
            pneu.setCodigo((long) i);
            pneu.setCodigoCliente(pneuFiltro.getNumeroFogo());
            pneu.setPosicao(posicaoPneuMapper.mapToProLog(pneuFiltro.getPosicao()));
            pneu.setPressaoAtual(pneuFiltro.getPressao());
            pneu.setPressaoCorreta(pneuFiltro.getPressaoRecomendada());
            final Sulcos sulcos = new Sulcos();
            sulcos.setExterno(pneuFiltro.getTrianguloPrimeiroSulco());
            sulcos.setCentralExterno(pneuFiltro.getTrianguloSegundoSulco());
            sulcos.setCentralInterno(pneuFiltro.getTrianguloTerceiroSulco());
            sulcos.setInterno(pneuFiltro.getTrianguloQuartoSulco());
            pneu.setSulcosAtuais(sulcos);
            pneus.add(pneu);
        }
        afericaoPlaca.getVeiculo().setListPneus(pneus);

        return afericaoPlaca;
    }

    @NotNull
    public static OsAvilan convert(@NotNull final OsIntegracao osIntegracao) {
        return createOsAvilan(osIntegracao);
    }

    @NotNull
    private static OsAvilan createOsAvilan(@NotNull final OsIntegracao osIntegracao) {
        return new OsAvilan(
                osIntegracao.getCodFilial(),
                osIntegracao.getCodUnidade(),
                osIntegracao.getCodOsProlog(),
                osIntegracao.getDataHoraAbertura(),
                osIntegracao.getDataHoraAbertura(),
                osIntegracao.getPlacaVeiculo(),
                osIntegracao.getKmVeiculoNaAbertura(),
                osIntegracao.getCpfColaboradorChecklist(),
                osIntegracao.getItensNok()
                        .stream()
                        .map(itemOsIntegracao -> createItemOsAvilan(osIntegracao, itemOsIntegracao))
                        .collect(Collectors.toList()));
    }

    @NotNull
    private static ItemOsAvilan createItemOsAvilan(@NotNull final OsIntegracao osIntegracao,
                                                   @NotNull final ItemOsIntegracao itemOsIntegracao) {
        return new ItemOsAvilan(
                osIntegracao.getCodFilial(),
                osIntegracao.getCodUnidade(),
                osIntegracao.getDataHoraAbertura(),
                itemOsIntegracao.getCodDefeito(),
                itemOsIntegracao.getDescricaoAlternativa(),
                // Se o item está fechado inserimos o serviço de fechamento.
                itemOsIntegracao.getDataHoraFechamento() != null
                        ? Collections.singletonList(createFechamentoOsAvilan(osIntegracao, itemOsIntegracao))
                        : Collections.emptyList());
    }

    @NotNull
    private static FechamentoOsAvilan createFechamentoOsAvilan(@NotNull final OsIntegracao osIntegracao,
                                                               @NotNull final ItemOsIntegracao itemOsIntegracao) {
        return new FechamentoOsAvilan(
                osIntegracao.getCodFilial(),
                osIntegracao.getCodUnidade(),
                itemOsIntegracao.getDataHoraFechamento(),
                itemOsIntegracao.getCodServico(),
                itemOsIntegracao.getDescricaoFechamentoItem());
    }

    /**
     * Não será mais considerado o tempo de realização do checklist na integração com a Avilan.
     */
    @Deprecated
    private static long parseTempoRealizacaoChecklist(@Nonnull final String tempoRealizacaoChecklist) {
        checkNotNull(tempoRealizacaoChecklist, "tempoRealizacaoChecklist não pode ser null!");

        final String[] s = tempoRealizacaoChecklist.split(":");
        final int horas = Integer.parseInt(s[0]);
        final int minutos = Integer.parseInt(s[1]);
        final int segundos = Integer.parseInt(s[2]);

        // Se durou mais que uma hora, tem algo de errado com essa duração, enviamos zero para não ser mostrado no
        // aplicativo ou web.
        return horas > 0 ? 0 : TimeUnit.HOURS.toMillis(horas)
                + TimeUnit.MINUTES.toMillis(minutos)
                + TimeUnit.SECONDS.toMillis(segundos);
    }

    @Nonnull
    private static AfericaoPlaca convertAfericaoSemPneus(@NotNull final AfericaoFiltro afericaoFiltro,
                                                         @NotNull final Long codUnidadeAfericao) throws ParseException {
        checkNotNull(afericaoFiltro, "afericaoFiltro não pode ser null!");
        final AfericaoPlaca afericaoPlaca = new AfericaoPlaca();
        afericaoPlaca.setCodigo((long) afericaoFiltro.getCodigoAfericao());
        afericaoPlaca.setCodUnidade(codUnidadeAfericao);
        afericaoPlaca.setKmMomentoAfericao(afericaoFiltro.getOdometro());
        afericaoPlaca.setFormaColetaDadosAfericao(FormaColetaDadosAfericaoEnum.EQUIPAMENTO);
        // Na integração todas as aferições devem ser de sulco e pressão, já que o Latromi não tem essa diferenciação.
        afericaoPlaca.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.SULCO_PRESSAO);

        if (afericaoFiltro.getDataRealizacao().length() > AvaCorpAvilanUtils.AVILAN_DATE_PATTERN_STRING_SIZE) {
            // Antes da integração, não era salvo no ERP da Avilan a hora da aferição, apenas a data. Se o tamanho da
            // String for menor ou igual ao pattern de data, então essa é uma aferição antiga que tem apenas a data
            // setada.
            afericaoPlaca.setDataHora(createLocalDateTimePattern(afericaoFiltro.getDataRealizacao()));
        } else {
            afericaoPlaca.setDataHora(createLocalDatePattern(afericaoFiltro.getDataRealizacao()).atStartOfDay());
        }

        final Colaborador colaborador = new Colaborador();
        if (afericaoFiltro.getColaborador() == null
                || Strings.isNullOrEmpty(afericaoFiltro.getColaborador().getNome())
                || Strings.isNullOrEmpty(afericaoFiltro.getColaborador().getCpf())) {
            // Antes da integração, não era salvo no ERP da Avilan quem fez a aferição. Aferições antigas não terão
            // colaborador vinculado.
            colaborador.setNome("Colaborador não informado");
            colaborador.setCpf(0L);
        } else {
            colaborador.setNome(afericaoFiltro.getColaborador().getNome());
            colaborador.setCpf(Long.parseLong(afericaoFiltro.getColaborador().getCpf()));
        }
        afericaoPlaca.setColaborador(colaborador);

        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(afericaoFiltro.getPlaca());
        afericaoPlaca.setVeiculo(veiculo);
        return afericaoPlaca;
    }

    @Nonnull
    private static List<Checklist> convertToChecklists(@NotNull final List<Avaliacao> avaliacoes) throws
            ParseException {
        checkNotNull(avaliacoes, "avaliacoes não pode ser null!");

        final List<Checklist> checklists = new ArrayList<>();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < avaliacoes.size(); i++) {
            final Avaliacao avaliacao = avaliacoes.get(i);
            final Checklist checklist = new Checklist();
            checklist.setCodigo((long) avaliacao.getCodigo());
            checklist.setTipo(avaliacao.getTipo().equals(AvacorpAvilanTipoChecklist.SAIDA)
                    ? Checklist.TIPO_SAIDA
                    : Checklist.TIPO_RETORNO);
            checklist.setData(createLocalDateTimePattern(avaliacao.getData()));
            final Colaborador colaborador = new Colaborador();
            colaborador.setNome(avaliacao.getUsuario());
            checklist.setColaborador(colaborador);
            checklists.add(checklist);
        }
        return checklists;
    }

    @Nullable
    private static Checklist getChecklistMaisAtualByTipo(@NotNull final List<Checklist> checklists,
                                                         final char tipoChecklist) {
        Checklist checklist = null;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < checklists.size(); i++) {
            final Checklist c = checklists.get(i);
            if (c.getTipo() == tipoChecklist) {
                if (checklist == null) {
                    checklist = c;
                } else if (c.getData().isAfter(checklist.getData())) {
                    checklist = c;
                }
            }
        }
        return checklist;
    }
}