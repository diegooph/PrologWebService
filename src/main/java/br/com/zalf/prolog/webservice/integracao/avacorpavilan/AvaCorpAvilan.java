package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.errorhandling.exception.TipoAfericaoNotSupported;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.model.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.TipoAfericao;
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
    public List<TipoVeiculo> getTiposVeiculosByUnidade(@NotNull Long codUnidade) throws Exception {
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
    public Veiculo getVeiculoByPlaca(@NotNull String placa, boolean withPneus) throws Exception {
        throw new IllegalStateException("O sistema " + AvaCorpAvilan.class.getSimpleName() +
                " não possui integração com o ProLog.");
    }

    @NotNull
    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao)
            throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getSelecaoModeloChecklistPlacaVeiculo(getCpf(), getDataNascimento()));
    }

    @NotNull
    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo,
                                                      char tipoChecklist) throws Exception {
        final ArrayOfVeiculoQuestao questoesVeiculo = requester.getQuestoesVeiculo(
                Math.toIntExact(codModelo),
                placaVeiculo,
                AvacorpAvilanTipoChecklist.fromTipoProLog(tipoChecklist),
                getCpf(),
                getDataNascimento());
        final Map<Long, String> mapCodPerguntUrlImagem =
                getAvaCorpAvilanDao().getMapeamentoCodPerguntaUrlImagem(codModelo);
        return AvaCorpAvilanConverter.convert(questoesVeiculo, mapCodPerguntUrlImagem, placaVeiculo);
    }

    @NotNull
    @Override
    public Long insertChecklist(@NotNull Checklist checklist) throws Exception {
        return requester.insertChecklist(
                AvaCorpAvilanConverter.convert(checklist, getCpf(), getDataNascimento()),
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
    public FarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                            @NotNull final Date dataInicial,
                                            @NotNull final Date dataFinal,
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
    public CronogramaAfericao getCronogramaAfericao(@NotNull Long codUnidadeCronograma) throws Exception {
        /*
         * Por enquanto a Avilan não suporta (por conta da integração) que um usuário faça uma aferição de um veículo
         * que não esteja presente na mesma unidade dele.
         */
        final Long codUnidadeColaborador = getCodUnidade();
        if (!codUnidadeCronograma.equals(codUnidadeColaborador)) {
            throw new AvaCorpAvilanException(
                    "Você só pode aferir veículos da sua unidade",
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
    public NovaAfericao getNovaAfericao(@NotNull String placaVeiculo,
                                        @NotNull String tipoAfericao) throws Exception {
        /*
         * A Avilan não suporta afericões de Sulco e Pressão separadamente, então lançamos uma
         * exceção caso o tipo selecionado for diferentes de {@link TipoAfericao#SULCO_PRESSAO}
         */
        if (!tipoAfericao.equals(TipoAfericao.SULCO_PRESSAO.asString())) {
            throw new TipoAfericaoNotSupported(
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    "Avilan só aceita aferição de " + TipoAfericao.SULCO_PRESSAO.getLegibleString(),
                    "Usuários da Avilan não podem realizar aferições de Sulco ou Pressão separadamente.");
        }

        /*
         * Por enquanto a Avilan não suporta (por conta da integração) que um usuário faça uma aferição de um veículo
         * que não esteja presente na mesma unidade dele.
         */
        final Long codUnidade = getCodUnidade();
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
        final Restricao restricao = getIntegradorProLog().getRestricaoByCodUnidade(codUnidade);
        final Short codDiagrama = dao.getCodDiagramaVeiculoProLogByCodTipoVeiculoAvilan(codTipoVeiculo);
        final Optional<DiagramaVeiculo> optional = getIntegradorProLog().getDiagramaVeiculoByCodDiagrama(codDiagrama);
        if (!optional.isPresent()) {
            throw new IllegalStateException("Erro ao buscar diagrama de código: " + codDiagrama);
        }

        final Veiculo veiculo = AvaCorpAvilanConverter.convert(veiculoAvilan, codUnidade);
        veiculo.setDiagrama(optional.get());
        veiculo.setListPneus(pneus);

        // Cria NovaAfericao.
        final NovaAfericao novaAfericao = new NovaAfericao();
        novaAfericao.setVeiculo(veiculo);
        novaAfericao.setRestricao(restricao);
        novaAfericao.setEstepesVeiculo(veiculo.getEstepes());
        novaAfericao.setDeveAferirEstepes(true);
        veiculo.removeEstepes();
        return novaAfericao;
    }

    @Override
    public boolean insertAfericao(@NotNull Afericao afericao,
                                  @NotNull Long codUnidade) throws Exception {
        return requester.insertAfericao(AvaCorpAvilanConverter.convert(afericao), getCpf(), getDataNascimento());
    }

    @NotNull
    @Override
    public Afericao getAfericaoByCodigo(@NotNull Long codUnidade, @NotNull Long codAfericao) throws Exception {

        final AfericaoFiltro afericaoFiltro = requester.getAfericaoByCodigo(
                Math.toIntExact(codAfericao),
                getCpf(),
                getDataNascimento());

        final AvaCorpAvilanDao dao = getAvaCorpAvilanDao();
        final String codTipoVeiculoAvilan = afericaoFiltro.getTipo().getCodigo();
        final PosicaoPneuMapper posicaoPneuMapper = new PosicaoPneuMapper(
                dao.getPosicoesPneuAvilanProLogByCodTipoVeiculoAvilan(codTipoVeiculoAvilan));

        final Afericao afericao = AvaCorpAvilanConverter.convert(posicaoPneuMapper, afericaoFiltro, codUnidade);

        final Short codDiagrama = dao.getCodDiagramaVeiculoProLogByCodTipoVeiculoAvilan(codTipoVeiculoAvilan);
        final Optional<DiagramaVeiculo> optional = getIntegradorProLog().getDiagramaVeiculoByCodDiagrama(codDiagrama);
        if (!optional.isPresent()) {
            throw new IllegalStateException("Erro ao buscar diagrama de código: " + codDiagrama);
        }
        afericao.getVeiculo().setDiagrama(optional.get());
        return afericao;
    }

    @NotNull
    @Override
    public List<Afericao> getAfericoes(@NotNull Long codUnidade,
                                       @NotNull String codTipoVeiculo,
                                       @NotNull String placaVeiculo,
                                       long dataInicial,
                                       long dataFinal,
                                       int limit,
                                       long offset) throws Exception {
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
                AvaCorpAvilanUtils.createDatePattern(new Date(dataInicial)),
                AvaCorpAvilanUtils.createDatePattern(new Date(dataFinal)),
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