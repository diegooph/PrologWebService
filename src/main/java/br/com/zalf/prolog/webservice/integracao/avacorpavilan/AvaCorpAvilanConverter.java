package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.checklist.model.*;
import br.com.zalf.prolog.webservice.frota.checklist.model.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.PlacaModeloHolder;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.ArrayOfMedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.IncluirMedida2;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.MedidaPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfPneu;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfString;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.TipoVeiculoAvilanProLog;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.MoreCollectors;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.com.zalf.prolog.webservice.integracao.avacorpavilan.AvaCorpAvilanUtils.createDatePattern;
import static com.google.common.base.Preconditions.checkArgument;
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
            medidaPneu.setCalibragem(pneu.getPressaoAtualAsInt());
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
            alternativa.setAlternativa("Outros");
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

    @VisibleForTesting
    public static FarolChecklist convert(ArrayOfFarolDia farolDia) throws ParseException {
        checkNotNull(farolDia, "farolDia não pode ser null!");
        checkArgument(farolDia.getFarolDia().size() == 1, "farolDia não pode vir com mais de um elemento " +
                "pois estamos filtrando apenas por um único dia!");

        final List<FarolVeiculoDia> farolVeiculos = new ArrayList<>();
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
                for (ItemCritico itemCritico : itensAvilan) {
                    final ItemOrdemServico itemOrdemServico = new ItemOrdemServico();
                    itemOrdemServico.setStatus(ItemOrdemServico.Status.PENDENTE);
                    itemOrdemServico.setDataApontamento(
                            AvaCorpAvilanUtils.createDateTimePattern(itemCritico.getData()));

                    // Seta o nome do item com problema.
                    // Alternativa.
                    final AlternativaChecklist alternativa = new AlternativaChecklist();
                    alternativa.setAlternativa(itemCritico.getDescricao());
                    final List<AlternativaChecklist> alternativas = new ArrayList<>();
                    alternativas.add(alternativa);
                    // Pergunta.
                    final PerguntaRespostaChecklist pergunta = new PerguntaRespostaChecklist();
                    pergunta.setPrioridade(PerguntaRespostaChecklist.CRITICA);
                    pergunta.setAlternativasResposta(alternativas);
                    itemOrdemServico.setPergunta(pergunta);

                    itensCriticos.add(itemOrdemServico);
                }
            }

            farolVeiculos.add(new FarolVeiculoDia(veiculo, checklistSaidaDia, checklistRetornoDia, itensCriticos));
        }

        return new FarolChecklist(farolVeiculos);
    }

    @NotNull
    @VisibleForTesting
    public static List<Checklist> getChecklists(@NotNull final List<ChecklistFiltro> checklistsFiltro)
            throws ParseException {
        checkNotNull(checklistsFiltro, "checklistsFiltro não pode ser null!");

        final List<Checklist> checklists = new ArrayList<>();

        for (final ChecklistFiltro checklistFiltro : checklistsFiltro) {
            final Checklist checklist = new Checklist();
            checklist.setCodigo((long) checklistFiltro.getCodigoChecklist());
            checklist.setCodModelo((long) checklistFiltro.getCodigoQuestionario());

            // Colaborador Checklist
            final Colaborador colaborador = new Colaborador();
            colaborador.setCpf(Long.parseLong(checklistFiltro.getColaborador().getCpf()));
            colaborador.setNome(checklistFiltro.getColaborador().getNome());

            checklist.setData(AvaCorpAvilanUtils.createDateTimePattern(checklistFiltro.getDataHoraRealizacao()));
            checklist.setKmAtualVeiculo(checklistFiltro.getOdometro());
            checklist.setPlacaVeiculo(checklistFiltro.getPlaca());
            checklist.setTipo(checklistFiltro.getTipo().asTipoProLog());
            checklist.setQtdItensOk(checklistFiltro.getQuantidadeRespostasOk());
            checklist.setQtdItensNok(checklistFiltro.getQuantidadeRespostasNaoOk());

            checklists.add(checklist);
        }

        return checklists;
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
        for (TipoVeiculoAvilanProLog tipoVeiculoAvilanProLog : tiposVeiculosAvilanProLog) {
            final TipoVeiculo tipoVeiculoProLog = new TipoVeiculo();
            tipoVeiculoProLog.setCodigo(tipoVeiculoAvilanProLog.getCodProLog());
            tipoVeiculoProLog.setNome(tipoVeiculoAvilanProLog.getDescricao());
            tiposVeiculosProLog.add(tipoVeiculoProLog);
        }

        return tiposVeiculosProLog;
    }

    @NotNull
    private static List<Checklist> convertToChecklists(@NotNull final List<Avaliacao> avaliacoes) throws ParseException {
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
            checklist.setData(AvaCorpAvilanUtils.createDateTimePattern(avaliacao.getData()));
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
                } else if (c.getData().after(checklist.getData())){
                    checklist = c;
                }
            }
        }
        return checklist;
    }
}