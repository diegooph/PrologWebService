package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.report.Report;
import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.errorhandling.exception.TipoAfericaoNotSupported;
import br.com.zalf.prolog.webservice.frota.checklist.mudancaestrutura.ChecklistMigracaoEstruturaSuporte;
import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.TipoChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.farol.DeprecatedFarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistInsercao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistRealizacao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.realizacao.ModeloChecklistSelecao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.DiagramaVeiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.PosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.AfericaoFiltro;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.afericao.ArrayOfAfericaoFiltro;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.TipoVeiculoAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfFarolDia;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfVeiculoQuestao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ChecklistFiltro;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.*;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Subclasse de {@link Sistema} responsável por cuidar da integração com o AvaCorp para a empresa Avilan.
 */
public final class AvaCorpAvilan extends Sistema {

    /**
     * Caso venha %, significa que queremos todos os tipos,
     * para buscar de todos os tipos na integração, mandamos vazio.
     */
    private static final String FILTRO_TODOS = "%";
    @NotNull
    private final AvaCorpAvilanRequester requester;
    @Nullable
    private Colaborador colaborador;

    public AvaCorpAvilan(@NotNull final AvaCorpAvilanRequester requester,
                         @NotNull final SistemaKey sistemaKey,
                         @NotNull final IntegradorProLog integradorProLog,
                         @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
        this.requester = requester;
    }

    @NotNull
    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade, @Nullable Boolean ativos) throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getVeiculosAtivos(getCpf(), getDataNascimento()), codUnidade);
    }

    @NotNull
    @Override
    public List<TipoVeiculo> getTiposVeiculosByEmpresa(@NotNull Long codEmpresa) throws Throwable {
        final ArrayOfVeiculo veiculosAtivos = requester.getVeiculosAtivos(getCpf(), getDataNascimento());
        final List<TipoVeiculoAvilan> tiposVeiculosAvilan = new ArrayList<>();

        // Adiciona os tipos diferentes na listagem de tipos de veículo da Avilan.
        veiculosAtivos.getVeiculo().forEach(veiculo -> {
            if (!tiposVeiculosAvilan.contains(veiculo.getTipo())) {
                tiposVeiculosAvilan.add(veiculo.getTipo());
            }
        });

        // Sincroniza os tipos buscados com o nosso banco de dados.
        final List<TipoVeiculoAvilanProLog> tiposVeiculosProLog =
                new AvaCorpAvilanSincronizadorTiposVeiculos(getAvaCorpAvilanDao()).sync(tiposVeiculosAvilan);

        final List<TipoVeiculoAvilanProLog> tiposPrologFiltrados = new ArrayList<>();

        // O veículo pode ter sido salvo no banco do ProLog e posteriormente desativado na Avilan esse método separa
        // em uma lista apenas os veiculos que estejam ativos no ProLog e na Avilan.
        for (TipoVeiculoAvilanProLog tipoVeiculoAvilanProLog : tiposVeiculosProLog) {
            for (TipoVeiculoAvilan tipoVeiculoAvilan : tiposVeiculosAvilan) {
                if (tipoVeiculoAvilan.getCodigo().equals(tipoVeiculoAvilanProLog.getCodigoAvilan())) {
                    tiposPrologFiltrados.add(tipoVeiculoAvilanProLog);
                }
            }
        }

        return AvaCorpAvilanConverter.convert(tiposPrologFiltrados);
    }

    @NotNull
    @Override
    public List<String> getPlacasVeiculosByTipo(@NotNull Long codUnidade, @NotNull String codTipo) throws Exception {
        // Caso venha %, significa que queremos todos os tipos, para buscar de todos os tipos na integração, mandamos
        // vazio.
        final AvaCorpAvilanDaoImpl dao = getAvaCorpAvilanDao();
        if (codTipo.equals(FILTRO_TODOS)) {
            codTipo = "";
        } else {
            codTipo = dao.getCodTipoVeiculoAvilanByCodTipoVeiculoProLog(Long.parseLong(codTipo));
        }

        return AvaCorpAvilanConverter.convert(requester.getPlacasVeiculoByTipo(
                codTipo,
                getCpf(),
                getDataNascimento()));
    }

    @NotNull
    @Override
    public Veiculo getVeiculoByPlaca(@NotNull final String placa, final boolean withPneus) throws Exception {
        throw new IllegalStateException("O sistema " + AvaCorpAvilan.class.getSimpleName() +
                " não possui integração com o ProLog.");
    }

    @NotNull
    @Override
    public List<ModeloChecklistSelecao> getModelosSelecaoRealizacao(@NotNull final Long codUnidade,
                                                                    @NotNull final Long codCargo) throws Throwable {
        return ChecklistMigracaoEstruturaSuporte.toEstruturaNovaSelecaoModelo(
                AvaCorpAvilanConverter.convert(requester.getSelecaoModeloChecklistPlacaVeiculo(
                        getCpf(),
                        getDataNascimento())));
    }

    @Override
    public @NotNull ModeloChecklistRealizacao getModeloChecklistRealizacao(
            final @NotNull Long codModeloChecklist,
            final @NotNull Long codVeiculo,
            final @NotNull String placaVeiculo,
            final @NotNull TipoChecklist tipoChecklist) throws Throwable {
        final ArrayOfVeiculoQuestao questoesVeiculo = requester.getQuestoesVeiculo(
                Math.toIntExact(codModeloChecklist),
                placaVeiculo,
                AvacorpAvilanTipoChecklist.fromTipoProLog(tipoChecklist.asChar()),
                getCpf(),
                getDataNascimento());
        final Map<Long, String> mapCodPerguntUrlImagem =
                getAvaCorpAvilanDao().getMapeamentoCodPerguntaUrlImagem(codModeloChecklist);

        return ChecklistMigracaoEstruturaSuporte.toEstruturaNovaRealizacaoModelo(
                AvaCorpAvilanConverter.convert(
                questoesVeiculo,
                mapCodPerguntUrlImagem,
                placaVeiculo));
    }

    @NotNull
    @Override
    public Long insertChecklist(@NotNull final ChecklistInsercao checklist,
                                final boolean foiOffline,
                                final boolean deveAbrirOs) throws Throwable {
        if (checklist.getKmColetadoVeiculo() == 0) {
            throw new AvaCorpAvilanException(
                    "O KM enviado não pode ser 0!",
                    "A integração com a Avilan não aceita mais KMs 0");
        }

        return requester.insertChecklist(
                AvaCorpAvilanConverter.convert(checklist.getChecklistAntigo(), getCpf(), getDataNascimento()),
                getCpf(),
                getDataNascimento());
    }

    @NotNull
    @Override
    public Checklist getChecklistByCodigo(@NotNull Long codChecklist) throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getChecklistByCodigo(
                Math.toIntExact(codChecklist),
                getCpf(),
                getDataNascimento()));
    }

    @NotNull
    @Override
    public List<Checklist> getChecklistsByColaborador(@NotNull final Long cpf,
                                                      @NotNull Long dataInicialLong,
                                                      @NotNull Long dataFinalLong,
                                                      final int limit,
                                                      final long offset,
                                                      boolean resumido) throws Exception {
        final FilialUnidadeAvilanProLog filialUnidade = getAvaCorpAvilanDao()
                .getFilialUnidadeAvilanByCodUnidadeProLog(getCodUnidade());
        final List<ChecklistFiltro> checklistsFiltro = requester.getChecklistsByColaborador(
                filialUnidade.getCodFilialAvilan(),
                filialUnidade.getCodUnidadeAvilan(),
                "",
                "",
                AvaCorpAvilanUtils.createDatePattern(new Date(dataInicialLong)),
                AvaCorpAvilanUtils.createDatePattern(new Date(dataFinalLong)),
                getCpf(),
                getDataNascimento()).getChecklistFiltro();

        final List<ChecklistFiltro> checksColaborador = new ArrayList<>();
        for (ChecklistFiltro checklist : checklistsFiltro) {
            if (checklist.getColaborador().getCpf().equals(getCpf())) {
                checksColaborador.add(checklist);
            }
        }

        final List<Checklist> checklists = paginateAndConvertChecklists(checksColaborador, limit, offset, resumido);
        return Checklist.sortByDate(checklists, false);
    }

    @NotNull
    @Override
    public List<Checklist> getTodosChecklists(@NotNull final Long codUnidade,
                                              @Nullable final Long codEquipe,
                                              @Nullable final Long codTipoVeiculo,
                                              @Nullable final String placaVeiculo,
                                              final long dataInicial,
                                              final long dataFinal,
                                              final int limit,
                                              final long offset,
                                              final boolean resumido) throws Exception {
        final AvaCorpAvilanDaoImpl dao = getAvaCorpAvilanDao();
        final FilialUnidadeAvilanProLog filialUnidade = dao.getFilialUnidadeAvilanByCodUnidadeProLog(codUnidade);

        final String cpf = getCpf();
        final String dataNascimento = getDataNascimento();
        final List<ChecklistFiltro> checklistsFiltro = requester.getChecklists(
                filialUnidade.getCodFilialAvilan(),
                filialUnidade.getCodUnidadeAvilan(),
                codTipoVeiculo != null ? dao.getCodTipoVeiculoAvilanByCodTipoVeiculoProLog(codTipoVeiculo) : "",
                placaVeiculo != null ? placaVeiculo : "",
                AvaCorpAvilanUtils.createDatePattern(new Date(dataInicial)),
                AvaCorpAvilanUtils.createDatePattern(new Date(dataFinal)),
                cpf,
                dataNascimento).getChecklistFiltro();

        final List<Checklist> checklists = paginateAndConvertChecklists(checklistsFiltro, limit, offset, resumido);
        return Checklist.sortByDate(checklists, false);
    }

    @NotNull
    @Override
    public DeprecatedFarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                                      @NotNull final LocalDate dataInicial,
                                                      @NotNull final LocalDate dataFinal,
                                                      final boolean itensCriticosRetroativos) throws Exception {
        final FilialUnidadeAvilanProLog filialUnidade = getAvaCorpAvilanDao()
                .getFilialUnidadeAvilanByCodUnidadeProLog(getCodUnidade());

        final ArrayOfFarolDia farolChecklist = requester.getFarolChecklist(
                filialUnidade.getCodFilialAvilan(),
                filialUnidade.getCodUnidadeAvilan(),
                AvaCorpAvilanUtils.createDatePattern(dataInicial),
                AvaCorpAvilanUtils.createDatePattern(dataFinal),
                itensCriticosRetroativos,
                getCpf(),
                getDataNascimento());
        return AvaCorpAvilanConverter.convert(farolChecklist);
    }

    @NotNull
    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull final Long codUnidadeCronograma) throws Throwable {
        /*
         * Por enquanto a Avilan não suporta (por conta da integração) que um usuário faça uma aferição de um veículo
         * que não esteja presente na mesma unidade dele.
         */
        final Long codUnidadeColaborador = getCodUnidade();
        if (!codUnidadeCronograma.equals(codUnidadeColaborador)) {
            throw new BloqueadoIntegracaoException("Você só pode aferir veículos da sua unidade",
                    String.format("Unidade cronograma: %s -- Unidade colaborador: %d", codUnidadeCronograma, codUnidadeColaborador));
        }

        final Restricao restricao = getIntegradorProLog().getRestricaoByCodUnidade(codUnidadeCronograma);
        final ArrayOfVeiculo arrayOfVeiculo = requester.getVeiculosAtivos(getCpf(), getDataNascimento());
        final AfericaoVeiculosExclusionStrategy exclusionStrategy = new AfericaoVeiculosExclusionStrategy();
        final CronogramaAfericao cronograma =
                AvaCorpAvilanConverter.convert(exclusionStrategy.applyStrategy(arrayOfVeiculo), restricao, codUnidadeCronograma);
        cronograma.removerPlacasNaoAferiveis(cronograma);
        cronograma.removerModelosSemPlacas(cronograma);
        cronograma.calcularQuatidadeSulcosPressaoOk(cronograma);
        cronograma.calcularTotalVeiculos(cronograma);
        return cronograma;
    }

    @NotNull
    @Override
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placaVeiculo,
                                                  @NotNull final String tipoAfericao) throws Throwable {
        /*
         * A Avilan não suporta afericões de Sulco e Pressão separadamente, então lançamos uma
         * exceção caso o tipo selecionado for diferentes de {@link TipoAfericao#SULCO_PRESSAO}
         */
        if (!tipoAfericao.equals(TipoMedicaoColetadaAfericao.SULCO_PRESSAO.asString())) {
            throw new TipoAfericaoNotSupported(
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    "Avilan só aceita aferição de " + TipoMedicaoColetadaAfericao.SULCO_PRESSAO.getLegibleString(),
                    "Usuários da Avilan não podem realizar aferições de Sulco ou Pressão separadamente.");
        }

        /*
         * Por enquanto a Avilan não suporta (por conta da integração) que um usuário faça uma aferição de um veículo
         * que não esteja presente na mesma unidade dele.
         */
        final List<String> placas = getPlacasVeiculosByTipo(codUnidade, FILTRO_TODOS);
        if (!placas.contains(placaVeiculo)) {
            throw new AvaCorpAvilanException(
                    "Você só pode aferir veículos da sua unidade",
                    String.format("Placa: %s -- Unidade: %d", placaVeiculo, codUnidade));
        }

        final br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo veiculoAvilan =
                requester.getVeiculoAtivo(placaVeiculo, getCpf(), getDataNascimento());

        final AvaCorpAvilanDaoImpl dao = getAvaCorpAvilanDao();
        final String codTipoVeiculo = veiculoAvilan.getTipo().getCodigo();
        final List<Pneu> pneus = AvaCorpAvilanConverter.convert(
                new PosicaoPneuMapper(dao.getPosicoesPneuAvilanProLogByCodTipoVeiculoAvilan(codTipoVeiculo)),
                requester.getPneusVeiculo(placaVeiculo, getCpf(), getDataNascimento()));
        final Short codDiagrama = dao.getCodDiagramaVeiculoProLogByCodTipoVeiculoAvilan(codTipoVeiculo);
        final Optional<DiagramaVeiculo> optional = getIntegradorProLog().getDiagramaVeiculoByCodDiagrama(codDiagrama);
        if (!optional.isPresent()) {
            throw new IllegalStateException("Erro ao buscar diagrama de código: " + codDiagrama);
        }

        final Veiculo veiculo = AvaCorpAvilanConverter.convert(veiculoAvilan, codUnidade);
        veiculo.setDiagrama(optional.get());
        veiculo.setListPneus(pneus);

        // Cria NovaAfericao.
        final NovaAfericaoPlaca novaAfericao = new NovaAfericaoPlaca();
        final ConfiguracaoNovaAfericao config = getIntegradorProLog().getConfiguracaoNovaAfericao(veiculo.getPlaca());
        novaAfericao.setVeiculo(veiculo);
        novaAfericao.setRestricao(Restricao.createRestricaoFrom(config));
        novaAfericao.setEstepesVeiculo(veiculo.getEstepes());
        novaAfericao.setDeveAferirEstepes(true);
        novaAfericao.setVariacaoAceitaSulcoMenorMilimetros(config.getVariacaoAceitaSulcoMenorMilimetros());
        novaAfericao.setVariacaoAceitaSulcoMaiorMilimetros(config.getVariacaoAceitaSulcoMaiorMilimetros());
        return novaAfericao;
    }

    @NotNull
    @Override
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        throw new BloqueadoIntegracaoException("A Avilan só suporta aferição de uma placa e não de pneu avulso");
    }

    @NotNull
    @Override
    public Report getAfericoesAvulsas(@NotNull final Long codUnidade,
                                      @Nullable final Long codColaborador,
                                      @NotNull final LocalDate dataInicial,
                                      @NotNull final LocalDate dataFinal) throws Throwable {
        throw new BloqueadoIntegracaoException("A Avilan só suporta aferição de uma placa e não de pneu avulso");
    }

    @NotNull
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade, @NotNull final Afericao afericao) throws Throwable {
        if (afericao instanceof AfericaoPlaca) {
            final AfericaoPlaca afericaoPlaca = (AfericaoPlaca) afericao;
            if (afericaoPlaca.getKmMomentoAfericao() == 0) {
                throw new AvaCorpAvilanException(
                        "O KM enviado não pode ser 0!",
                        "A integração com a Avilan não aceita mais KMs 0");
            }

            final Long codAfericao = requester.insertAfericao(
                    AvaCorpAvilanConverter.convert(afericaoPlaca),
                    getCpf(),
                    getDataNascimento());
            if (codAfericao != null && codAfericao != 0) {
                return codAfericao;
            } else {
                throw new AvaCorpAvilanException("Falha na integração", "Erro ao inserir aferição para a unidade: " + codUnidade);
            }
        } else {
            throw new BloqueadoIntegracaoException("A Avilan só suporta aferição de uma placa e não de pneu avulso");
        }
    }

    @NotNull
    @Override
    public AfericaoPlaca getAfericaoByCodigo(@NotNull Long codUnidade, @NotNull Long codAfericao) throws Throwable {

        final AfericaoFiltro afericaoFiltro = requester.getAfericaoByCodigo(
                Math.toIntExact(codAfericao),
                getCpf(),
                getDataNascimento());

        final AvaCorpAvilanDao dao = getAvaCorpAvilanDao();
        final String codTipoVeiculoAvilan = afericaoFiltro.getTipo().getCodigo();
        final PosicaoPneuMapper posicaoPneuMapper = new PosicaoPneuMapper(
                dao.getPosicoesPneuAvilanProLogByCodTipoVeiculoAvilan(codTipoVeiculoAvilan));

        final AfericaoPlaca afericaoPlaca = AvaCorpAvilanConverter.convert(posicaoPneuMapper, afericaoFiltro, codUnidade);

        final Short codDiagrama = dao.getCodDiagramaVeiculoProLogByCodTipoVeiculoAvilan(codTipoVeiculoAvilan);
        final Optional<DiagramaVeiculo> optional = getIntegradorProLog().getDiagramaVeiculoByCodDiagrama(codDiagrama);
        if (!optional.isPresent()) {
            throw new IllegalStateException("Erro ao buscar diagrama de código: " + codDiagrama);
        }
        afericaoPlaca.getVeiculo().setDiagrama(optional.get());
        return afericaoPlaca;
    }

    @NotNull
    @Override
    public List<AfericaoPlaca> getAfericoesPlacas(@NotNull Long codUnidade,
                                                  @NotNull String codTipoVeiculo,
                                                  @NotNull String placaVeiculo,
                                                  @NotNull LocalDate dataInicial,
                                                  @NotNull LocalDate dataFinal,
                                                  int limit,
                                                  long offset) throws Throwable {
        // Caso venha %, significa que queremos todos os tipos, para buscar de todos os tipos na integração, mandamos
        // vazio.
        final AvaCorpAvilanDaoImpl dao = getAvaCorpAvilanDao();
        if (codTipoVeiculo.equals("%")) {
            codTipoVeiculo = "";
        } else {
            codTipoVeiculo = dao.getCodTipoVeiculoAvilanByCodTipoVeiculoProLog(Long.parseLong(codTipoVeiculo));
        }

        final FilialUnidadeAvilanProLog filialUnidade = dao.getFilialUnidadeAvilanByCodUnidadeProLog(codUnidade);

        final ArrayOfAfericaoFiltro afericoes = requester.getAfericoes(
                filialUnidade.getCodFilialAvilan(),
                filialUnidade.getCodUnidadeAvilan(),
                codTipoVeiculo,
                placaVeiculo.equals(FILTRO_TODOS) ? "" : placaVeiculo,
                AvaCorpAvilanUtils.createDatePattern(dataInicial),
                AvaCorpAvilanUtils.createDatePattern(dataFinal),
                limit,
                Math.toIntExact(offset),
                getCpf(),
                getDataNascimento());

        return AvaCorpAvilanConverter.convertAfericoes(afericoes.getAfericaoFiltro(), codUnidade);
    }

    @NotNull
    private AvaCorpAvilanDaoImpl getAvaCorpAvilanDao() {
        return new AvaCorpAvilanDaoImpl();
    }

    @NotNull
    private List<Checklist> paginateAndConvertChecklists(@NotNull final List<ChecklistFiltro> checklists,
                                                         final int limit,
                                                         final long offset,
                                                         final boolean resumido) {

        // Realizamos a paginação antes de transformar, respeitando limit e offset recebidos.
        return checklists
                .stream()
                .skip(offset)
                .limit(limit)
                .map(checklistFiltro -> {
                    try {
                        if (resumido) {
                            return AvaCorpAvilanConverter.convert(checklistFiltro);
                        } else {
                            return getChecklistByCodigo((long) checklistFiltro.getCodigoChecklist());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @NotNull
    private String getCpf() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        return colaborador.getCpfAsString();
    }

    @NotNull
    private String getDataNascimento() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        return AvaCorpAvilanUtils.createDatePattern(colaborador.getDataNascimento());
    }

    @NotNull
    private Long getCodUnidade() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        return colaborador.getUnidade().getCodigo();
    }

    @NotNull
    private <T> List<T> paginate(@NotNull final List<T> data,
                                 final int limit,
                                 final long offset) {
        return data
                .stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }
}