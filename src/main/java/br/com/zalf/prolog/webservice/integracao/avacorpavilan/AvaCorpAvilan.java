package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.frota.checklist.model.Checklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.FarolChecklist;
import br.com.zalf.prolog.webservice.frota.checklist.model.NovoChecklistHolder;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.ModeloChecklist;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nonnull
    private final AvaCorpAvilanRequester requester;
    @Nullable
    private Colaborador colaborador;

    public AvaCorpAvilan(@Nonnull final AvaCorpAvilanRequester requester,
                         @Nonnull final SistemaKey sistemaKey,
                         @Nonnull final IntegradorProLog integradorProLog,
                         @Nonnull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
        this.requester = requester;
    }

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@Nonnull Long codUnidade) throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getVeiculosAtivos(getCpf(), getDataNascimento()));
    }

    @Override
    public List<TipoVeiculo> getTiposVeiculosByUnidade(@Nonnull Long codUnidade) throws Exception {
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

    @Override
    public List<String> getPlacasVeiculosByTipo(@Nonnull Long codUnidade, @Nonnull String codTipo) throws Exception {
        final ArrayOfVeiculo veiculosAtivos = requester.getVeiculosAtivos(getCpf(), getDataNascimento());
        final List<String> placas = new ArrayList<>();
        if (codTipo.equals(FILTRO_TODOS)) {
            veiculosAtivos.getVeiculo().forEach(veiculo -> placas.add(veiculo.getPlaca()));
        } else {
            final String codTipoAvilan =
                    getAvaCorpAvilanDao().getCodTipoVeiculoAvilanByCodTipoVeiculoProLog(Long.parseLong(codTipo));
            veiculosAtivos.getVeiculo().forEach(veiculo -> {
                if (veiculo.getTipo().getCodigo().equals(codTipoAvilan)) {
                    placas.add(veiculo.getPlaca());
                }
            });
        }
        return placas;
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@Nonnull Long codUnidade,
                                                                                    @Nonnull Long codFuncao)
            throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getSelecaoModeloChecklistPlacaVeiculo(getCpf(), getDataNascimento()));
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@Nonnull Long codUnidade,
                                                      @Nonnull Long codModelo,
                                                      @Nonnull String placaVeiculo,
                                                      char tipoChecklist) throws Exception {
        final ArrayOfVeiculoQuestao questoesVeiculo = requester.getQuestoesVeiculo(
                Math.toIntExact(codModelo),
                placaVeiculo,
                AvacorpAvilanTipoChecklist.fromTipoProLog(tipoChecklist),
                getCpf(),
                getDataNascimento());
        return AvaCorpAvilanConverter.convert(questoesVeiculo, placaVeiculo);
    }

    @Override
    public Long insertChecklist(@Nonnull Checklist checklist) throws Exception {
        return requester.insertChecklist(
                AvaCorpAvilanConverter.convert(checklist, getCpf(), getDataNascimento()),
                getCpf(),
                getDataNascimento());
    }

    @Nonnull
    @Override
    public Checklist getChecklistByCodigo(@Nonnull Long codChecklist) throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getChecklistByCodigo(
                Math.toIntExact(codChecklist),
                getCpf(),
                getDataNascimento()));
    }

    @Nonnull
    @Override
    public List<Checklist> getChecklistsByColaborador(@Nonnull final Long cpf,
                                                      @Nullable Long dataInicialLong,
                                                      @Nullable Long dataFinalLong,
                                                      final int limit,
                                                      final long offset, boolean resumido) throws Exception {
        Date dataInicial;
        Date dataFinal;
        if (dataInicialLong == null || dataFinalLong == null) {
            dataInicial = new Date(System.currentTimeMillis());
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataInicial);
            calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
            dataFinal = new Date(System.currentTimeMillis());
        } else {
            dataInicial = new Date(dataInicialLong);
            dataFinal = new Date(dataFinalLong);
        }

        final FilialUnidadeAvilanProLog filialUnidade = getAvaCorpAvilanDao()
                .getFilialUnidadeAvilanByCodUnidadeProLog(getCodUnidade());
        final List<ChecklistFiltro> checklistsFiltro = requester.getChecklistsByColaborador(
                filialUnidade.getCodFilialAvilan(),
                filialUnidade.getCodUnidadeAvilan(),
                "",
                "",
                AvaCorpAvilanUtils.createDatePattern(dataInicial),
                AvaCorpAvilanUtils.createDatePattern(dataFinal),
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

    @Nonnull
    @Override
    public List<Checklist> getTodosChecklists(@Nonnull final Long codUnidade,
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

    @Nonnull
    @Override
    public FarolChecklist getFarolChecklist(@Nonnull final Long codUnidade,
                                            @Nonnull final Date dataInicial,
                                            @Nonnull final Date dataFinal,
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

    @Override
    public CronogramaAfericao getCronogramaAfericao(@Nonnull Long codUnidade) throws Exception {
        final Restricao restricao = getIntegradorProLog().getRestricaoByCodUnidade(codUnidade);
        final ArrayOfVeiculo arrayOfVeiculo = requester.getVeiculosAtivos(getCpf(), getDataNascimento());
        final AfericaoVeiculosExclusionStrategy exclusionStrategy = new AfericaoVeiculosExclusionStrategy();
        return AvaCorpAvilanConverter.convert(exclusionStrategy.applyStrategy(arrayOfVeiculo), restricao);
    }

    @Override
    public NovaAfericao getNovaAfericao(@Nonnull String placaVeiculo) throws Exception {
        final br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo veiculoAvilan =
                requester.getVeiculoAtivo(placaVeiculo, getCpf(), getDataNascimento());

        final AvaCorpAvilanDaoImpl dao = getAvaCorpAvilanDao();
        final String codTipoVeiculo = veiculoAvilan.getTipo().getCodigo();
        final List<Pneu> pneus = AvaCorpAvilanConverter.convert(
                new PosicaoPneuMapper(dao.getPosicoesPneuAvilanProLogByCodTipoVeiculoAvilan(codTipoVeiculo)),
                requester.getPneusVeiculo(placaVeiculo, getCpf(), getDataNascimento()));
        final Restricao restricao = getIntegradorProLog().getRestricaoByCodUnidade(getCodUnidade());
        final Short codDiagrama = dao.getCodDiagramaVeiculoProLogByCodTipoVeiculoAvilan(codTipoVeiculo);
        final Optional<DiagramaVeiculo> optional = getIntegradorProLog().getDiagramaVeiculoByCodDiagrama(codDiagrama);
        if (!optional.isPresent()) {
            throw new IllegalStateException("Erro ao buscar diagrama de código: " + codDiagrama);
        }

        final Veiculo veiculo = AvaCorpAvilanConverter.convert(veiculoAvilan);
        veiculo.setDiagrama(optional.get());
        veiculo.setListPneus(pneus);

        // Cria NovaAfericao.
        final NovaAfericao novaAfericao = new NovaAfericao();
        novaAfericao.setVeiculo(veiculo);
        novaAfericao.setRestricao(restricao);
        novaAfericao.setEstepesVeiculo(veiculo.getEstepes());
        veiculo.removeEstepes();
        return novaAfericao;
    }

    @Override
    public boolean insertAfericao(@Nonnull Afericao afericao,
                                  @Nonnull Long codUnidade) throws Exception {
        return requester.insertAfericao(AvaCorpAvilanConverter.convert(afericao), getCpf(), getDataNascimento());
    }

    @Nonnull
    @Override
    public Afericao getAfericaoByCodigo(@Nonnull Long codUnidade, @Nonnull Long codAfericao) throws Exception {

        final AfericaoFiltro afericaoFiltro = requester.getAfericaoByCodigo(
                Math.toIntExact(codAfericao),
                getCpf(),
                getDataNascimento());

        final AvaCorpAvilanDao dao = getAvaCorpAvilanDao();
        final String codTipoVeiculoAvilan = afericaoFiltro.getTipo().getCodigo();
        final PosicaoPneuMapper posicaoPneuMapper = new PosicaoPneuMapper(
                dao.getPosicoesPneuAvilanProLogByCodTipoVeiculoAvilan(codTipoVeiculoAvilan));

        final Afericao afericao = AvaCorpAvilanConverter.convert(posicaoPneuMapper, afericaoFiltro);

        final Short codDiagrama = dao.getCodDiagramaVeiculoProLogByCodTipoVeiculoAvilan(codTipoVeiculoAvilan);
        final Optional<DiagramaVeiculo> optional = getIntegradorProLog().getDiagramaVeiculoByCodDiagrama(codDiagrama);
        if (!optional.isPresent()) {
            throw new IllegalStateException("Erro ao buscar diagrama de código: " + codDiagrama);
        }
        afericao.getVeiculo().setDiagrama(optional.get());
        return afericao;
    }

    @Override
    public List<Afericao> getAfericoes(@Nonnull Long codUnidade,
                                       @Nonnull String codTipoVeiculo,
                                       @Nonnull String placaVeiculo,
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
                placaVeiculo.equals("%") ? "" : placaVeiculo,
                AvaCorpAvilanUtils.createDatePattern(new Date(dataInicial)),
                AvaCorpAvilanUtils.createDatePattern(new Date(dataFinal)),
                limit,
                Math.toIntExact(offset),
                getCpf(),
                getDataNascimento());

        return AvaCorpAvilanConverter.convertAfericoes(afericoes.getAfericaoFiltro());
    }

    @Nonnull
    private AvaCorpAvilanDaoImpl getAvaCorpAvilanDao() {
        return new AvaCorpAvilanDaoImpl();
    }

    @Nonnull
    private List<Checklist> paginateAndConvertChecklists(@Nonnull final List<ChecklistFiltro> checklists,
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

    @Nonnull
    private String getCpf() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        return colaborador.getCpfAsString();
    }

    @Nonnull
    private String getDataNascimento() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        return AvaCorpAvilanUtils.createDatePattern(colaborador.getDataNascimento());
    }

    @Nonnull
    private Long getCodUnidade() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        return colaborador.getUnidade().getCodigo();
    }

    @Nonnull
    private <T> List<T> paginate(@Nonnull final List<T> data,
                                 final int limit,
                                 final long offset) {
        return data
                .stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }
}