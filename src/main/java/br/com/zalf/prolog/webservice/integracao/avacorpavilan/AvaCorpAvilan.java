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
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.ArrayOfVeiculo;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.TipoVeiculoAvilan;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfFarolDia;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ArrayOfVeiculoQuestao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ChecklistFiltro;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.AvaCorpAvilanDao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.AvaCorpAvilanDaoImpl;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.AvaCorpAvilanSincronizadorTiposVeiculos;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.data.TipoVeiculoAvilanProLog;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.requester.AvaCorpAvilanRequester;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Subclasse de {@link Sistema} responsável por cuidar da integração com o AvaCorp para a empresa Avilan.
 */
public final class AvaCorpAvilan extends Sistema {
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

    @Override
    public List<Veiculo> getVeiculosAtivosByUnidade(@NotNull Long codUnidade) throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getVeiculosAtivos(cpf(), dataNascimento()));
    }

    @Override
    public List<TipoVeiculo> getTiposVeiculosByUnidade(@NotNull Long codUnidade) throws Exception {
        final List<TipoVeiculoAvilan> tiposVeiculosAvilan = requester
                .getTiposVeiculo(cpf(), dataNascimento())
                .getTipoVeiculo();

        // Sincroniza os tipos buscados com o nosso banco de dados.
        final List<TipoVeiculoAvilanProLog> tiposVeiculosAvilanProLog =
                new AvaCorpAvilanSincronizadorTiposVeiculos(new AvaCorpAvilanDaoImpl()).sync(tiposVeiculosAvilan);

        return AvaCorpAvilanConverter.convert(tiposVeiculosAvilanProLog);
    }

    @Override
    public List<String> getPlacasVeiculosByTipo(@NotNull Long codUnidade, @NotNull String codTipo) throws Exception {
        // Caso venha %, significa que queremos todos os tipos, para buscar de todos os tipos na integração, mandamos
        // vazio.
        if (codTipo.equals("%")) {
            codTipo = "";
        } else {
            final AvaCorpAvilanDao avaCorpAvilanDao = new AvaCorpAvilanDaoImpl();
            codTipo = avaCorpAvilanDao.getCodTipoVeiculoAvilanByCodTipoVeiculoProLog(Long.parseLong(codTipo));
        }
        return AvaCorpAvilanConverter.convert(requester.getPlacasVeiculoByTipo(codTipo, cpf(), dataNascimento()));
    }

    @Override
    public Map<ModeloChecklist, List<String>> getSelecaoModeloChecklistPlacaVeiculo(@NotNull Long codUnidade,
                                                                                    @NotNull Long codFuncao)
            throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getSelecaoModeloChecklistPlacaVeiculo(cpf(), dataNascimento()));
    }

    @Override
    public NovoChecklistHolder getNovoChecklistHolder(@NotNull Long codUnidade,
                                                      @NotNull Long codModelo,
                                                      @NotNull String placaVeiculo,
                                                      char tipoChecklist) throws Exception {
        final ArrayOfVeiculoQuestao questoesVeiculo = requester.getQuestoesVeiculo(
                Math.toIntExact(codModelo),
                placaVeiculo,
                AvacorpAvilanTipoChecklist.fromTipoProLog(tipoChecklist),
                cpf(),
                dataNascimento());
        return AvaCorpAvilanConverter.convert(questoesVeiculo, placaVeiculo);
    }

    @Override
    public Long insertChecklist(@NotNull Checklist checklist) throws Exception {
        return requester.insertChecklist(
                AvaCorpAvilanConverter.convert(checklist, cpf(), dataNascimento()),
                cpf(),
                dataNascimento());
    }

    @Override
    public Checklist getChecklistByCodigo(Long codChecklist) throws Exception {
        return AvaCorpAvilanConverter.convert(requester.getChecklistByCodigo(
                Math.toIntExact(codChecklist),
                cpf(),
                dataNascimento()));
    }

    @Override
    public List<Checklist> getAll(@NotNull final Date dataInicial,
                                  @NotNull final Date dataFinal,
                                  @NotNull final String equipe,
                                  @NotNull final Long codUnidade,
                                  @NotNull final String placa,
                                  final long limit,
                                  final long offset,
                                  final boolean resumido) throws Exception {
        final String codUnidadeAvilan = getIntegradorProLog().getCodUnidadeClienteByCodUnidadeProLog(codUnidade);
        List<ChecklistFiltro> checklists = requester.getChecklists(
                Integer.parseInt(codUnidadeAvilan),
                "",
                placa.equals("%") ? "" : placa,
                AvaCorpAvilanUtils.createDatePattern(dataInicial),
                AvaCorpAvilanUtils.createDatePattern(dataFinal),
                cpf(),
                dataNascimento()).getChecklistFiltro();

        // Realizamos a paginação antes de transformar, respeitando limit e offset recebidos.
        checklists = checklists
                .stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());

        return AvaCorpAvilanConverter.getChecklists(checklists);
    }

    @Override
    public FarolChecklist getFarolChecklist(@NotNull final Long codUnidade,
                                            @NotNull final Date dataInicial,
                                            @NotNull final Date dataFinal,
                                            final boolean itensCriticosRetroativos) throws Exception {
        final String codUnidadeAvilan = getIntegradorProLog().getCodUnidadeClienteByCodUnidadeProLog(codUnidade);
        final ArrayOfFarolDia farolChecklist = requester.getFarolChecklist(
                Integer.parseInt(codUnidadeAvilan),
                AvaCorpAvilanUtils.createDatePattern(dataInicial),
                AvaCorpAvilanUtils.createDatePattern(dataFinal),
                itensCriticosRetroativos,
                cpf(),
                dataNascimento());
        return AvaCorpAvilanConverter.convert(farolChecklist);
    }

    @Override
    public CronogramaAfericao getCronogramaAfericao(@NotNull Long codUnidade) throws Exception {
        final Restricao restricao = getIntegradorProLog().getRestricaoByCodUnidade(codUnidade);
        final ArrayOfVeiculo arrayOfVeiculo = requester.getVeiculosAtivos(cpf(), dataNascimento());
        return AvaCorpAvilanConverter.convert(arrayOfVeiculo, restricao);
    }

    @Override
    public NovaAfericao getNovaAfericao(@NotNull String placaVeiculo) throws Exception {
        br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.Veiculo veiculoAtivo =
                requester.getVeiculoAtivo(placaVeiculo, cpf(), dataNascimento());
        final Veiculo veiculo = AvaCorpAvilanConverter.convert(veiculoAtivo);
        final List<Pneu> pneus = AvaCorpAvilanConverter.convert(
                requester.getPneusVeiculo(placaVeiculo, cpf(), dataNascimento()));
        final Restricao restricao = getIntegradorProLog().getRestricaoByCodUnidade(codUnidade());
//        final Long codDiagrama = DiagramaVeiculoProviderFactory
//                .getDiagramaVeiculoProvider(getSistemaKey())
//                .getCodDiagramaBy("C2RRR");
        final DiagramaVeiculo diagramaVeiculo = getIntegradorProLog().getDiagramaVeiculoByPlaca(veiculo.getPlaca());
        if (diagramaVeiculo == null) {
            throw new IllegalStateException("Diagrama não encontrado para a placa: " + placaVeiculo);
        }
        veiculo.setDiagrama(diagramaVeiculo);
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
    public boolean insertAfericao(@NotNull Afericao afericao,
                                  @NotNull Long codUnidade) throws Exception {
        return requester.insertAfericao(AvaCorpAvilanConverter.convert(afericao), cpf(), dataNascimento());
    }

    @Override
    public List<Afericao> getAfericoes(@NotNull Long codUnidade,
                                       @NotNull String codTipoVeiculo,
                                       @NotNull String placaVeiculo,
                                       long dataInicial,
                                       long dataFinal,
                                       long limit,
                                       long offset) throws Exception {

        final String codUnidadeAvilan = getIntegradorProLog().getCodUnidadeClienteByCodUnidadeProLog(codUnidade);

        // Caso venha %, significa que queremos todos os tipos, para buscar de todos os tipos na integração, mandamos
        // vazio.
        if (codTipoVeiculo.equals("%")) {
            codTipoVeiculo = "";
        } else {
            final AvaCorpAvilanDao avaCorpAvilanDao = new AvaCorpAvilanDaoImpl();
            codTipoVeiculo = avaCorpAvilanDao.getCodTipoVeiculoAvilanByCodTipoVeiculoProLog(Long.parseLong(codTipoVeiculo));
        }

        //noinspection unchecked
        return (List<Afericao>) requester.getAfericoes(
                Integer.parseInt(codUnidadeAvilan),
                codTipoVeiculo,
                placaVeiculo.equals("%") ? "" : placaVeiculo,
                AvaCorpAvilanUtils.createDatePattern(new Date(dataInicial)),
                AvaCorpAvilanUtils.createDatePattern(new Date(dataFinal)),
                cpf(),
                dataNascimento());
    }

    private String cpf() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        // Preenche com 0 a esquerda caso CPF tenha menos do que 11 caracteres.
        return String.format("%011d",  colaborador.getCpf());
    }

    private String dataNascimento() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        return AvaCorpAvilanUtils.createDatePattern(colaborador.getDataNascimento());
    }

    private Long codUnidade() throws Exception {
        if (colaborador == null) {
            colaborador = getIntegradorProLog().getColaboradorByToken(getUserToken());
        }

        return colaborador.getUnidade().getCodigo();
    }
}