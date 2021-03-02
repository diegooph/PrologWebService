package br.com.zalf.prolog.webservice.integracao.webfinatto;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.customfields.CampoPersonalizadoDao;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama.EixoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoDadosColetaKm;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.integracao.IntegracaoPosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.MetodoIntegrado;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.integrador._model.*;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.EmpresaWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.PneuWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.VeiculoWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.afericao.AfericaoPlacaWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.afericao.AfericaoPneuWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.error.SistemaWebFinattoException;
import br.com.zalf.prolog.webservice.integracao.webfinatto.data.SistemaWebFinattoRequester;
import br.com.zalf.prolog.webservice.integracao.webfinatto.utils.SistemaWebFinattoConverter;
import br.com.zalf.prolog.webservice.integracao.webfinatto.utils.WebFinattoEncoderDecoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SistemaWebFinatto extends Sistema {
    @NotNull
    private static final String TAG = SistemaWebFinatto.class.getSimpleName();
    @NotNull
    private final SistemaWebFinattoRequester requester;
    @NotNull
    private final IntegracaoDao integracaoDao;

    public SistemaWebFinatto(@NotNull final SistemaWebFinattoRequester requester,
                             @NotNull final SistemaKey sistemaKey,
                             @NotNull final RecursoIntegrado recursoIntegrado,
                             @NotNull final IntegradorProLog integradorProLog,
                             @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, recursoIntegrado, userToken);
        this.integracaoDao = Injection.provideIntegracaoDao();
        this.requester = requester;
    }

    @Override
    @NotNull
    public CronogramaAfericao getCronogramaAfericao(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, codUnidades);
            if (unidadeDeParaHolder.isEmpty()) {
                return SistemaWebFinattoConverter.createEmptyCronogramaAfericaoProlog();
            }
            final UnidadeRestricaoHolder unidadeRestricaoHolder =
                    integracaoDao.getUnidadeRestricaoHolder(conn, unidadeDeParaHolder.getCodUnidadesMapeadas());
            final TipoVeiculoConfigAfericaoHolder tipoVeiculoConfigAfericaoHolder =
                    integracaoDao.getTipoVeiculoConfigAfericaoHolder(conn,
                                                                     unidadeDeParaHolder.getCodUnidadesMapeadas());
            final List<VeiculoWebFinatto> veiculosByFiliais =
                    internalGetVeiculosByFiliais(conn,
                                                 unidadeDeParaHolder.getCodEmpresaProlog(),
                                                 unidadeDeParaHolder.getCodFiliais());
            final List<String> placas = veiculosByFiliais.stream()
                    .map(VeiculoWebFinatto::getPlacaVeiculo)
                    .distinct()
                    .collect(Collectors.toList());
            final AfericaoRealizadaPlacaHolder afericaoRealizadaPlacaHolder =
                    integracaoDao.getAfericaoRealizadaPlacaHolder(conn,
                                                                  unidadeDeParaHolder.getCodEmpresaProlog(),
                                                                  placas);

            return SistemaWebFinattoConverter.createCronogramaAfericaoProlog(veiculosByFiliais,
                                                                             unidadeRestricaoHolder,
                                                                             tipoVeiculoConfigAfericaoHolder,
                                                                             afericaoRealizadaPlacaHolder);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placaVeiculo,
                                                  @NotNull final String tipoAfericao) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, Collections.singletonList(codUnidade));
            final VeiculoWebFinatto veiculoByPlaca =
                    internalGetVeiculoByPlaca(conn,
                                              unidadeDeParaHolder.getCodEmpresaProlog(),
                                              unidadeDeParaHolder.getCodFiliais(),
                                              placaVeiculo);
            final Short codDiagramaProlog =
                    integracaoDao.getCodDiagramaByDeParaTipoVeiculo(conn,
                                                                    unidadeDeParaHolder.getCodEmpresaProlog(),
                                                                    veiculoByPlaca.getCodEstruturaVeiculo());
            if (codDiagramaProlog <= 0) {
                throw new SistemaWebFinattoException(
                        "Identificamos aque a estrutura (" + veiculoByPlaca.getCodEstruturaVeiculo() + ") " +
                                "não está configurada no Prolog.\n" +
                                "Por favor, solicite que esta esta estrutura seja cadastrada no Prolog para " +
                                "realizar a aferição.");
            }
            final ConfiguracaoNovaAfericaoPlaca configNovaAfericaoPlaca =
                    integracaoDao.getConfigNovaAfericaoPlaca(conn,
                                                             codUnidade,
                                                             veiculoByPlaca.getCodEstruturaVeiculo());
            final IntegracaoPosicaoPneuMapper posicaoPneuMapper = new IntegracaoPosicaoPneuMapper(
                    veiculoByPlaca.getCodEstruturaVeiculo(),
                    integracaoDao.getMapeamentoPosicoesPrologByDeParaTipoVeiculo(
                            conn,
                            unidadeDeParaHolder.getCodEmpresaProlog(),
                            veiculoByPlaca.getCodEstruturaVeiculo()));
            return SistemaWebFinattoConverter.createNovaAfericaoPlacaProlog(codUnidade,
                                                                            codDiagramaProlog,
                                                                            veiculoByPlaca,
                                                                            posicaoPneuMapper,
                                                                            configNovaAfericaoPlaca);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, Collections.singletonList(codUnidade));
            final List<PneuWebFinatto> pneusByFiliais =
                    internalGetPneusByFiliais(conn,
                                              unidadeDeParaHolder.getCodEmpresaProlog(),
                                              unidadeDeParaHolder.getCodFiliais());
            final List<String> codPneus = pneusByFiliais.stream()
                    .map(PneuWebFinatto::getCodigoCliente)
                    .collect(Collectors.toList());
            final AfericaoRealizadaAvulsaHolder afericaoRealizadaAvulsaHolder =
                    integracaoDao.getAfericaoRealizadaAvulsaHolder(conn, codUnidade, codPneus);
            return SistemaWebFinattoConverter.createPneusAfericaoAvulsa(codUnidade,
                                                                        pneusByFiliais,
                                                                        afericaoRealizadaAvulsaHolder);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, Collections.singletonList(codUnidade));
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           unidadeDeParaHolder.getCodEmpresaProlog(),
                                                           getSistemaKey(),
                                                           MetodoIntegrado.GET_PNEU_NOVA_AFERICAO_AVULSA);
            final PneuWebFinatto pneuByCodigo = requester.getPneuByCodigo(apiAutenticacaoHolder,
                                                                          unidadeDeParaHolder.getCodFiliais(),
                                                                          codPneu.toString());
            final AfericaoRealizadaAvulsaHolder afericaoRealizadaAvulsaHolder =
                    integracaoDao.getAfericaoRealizadaAvulsaHolder(conn,
                                                                   codUnidade,
                                                                   Collections.singletonList(pneuByCodigo.getCodPneu()));
            final ConfiguracaoNovaAfericaoAvulsa configuracaoAfericao =
                    integracaoDao.getConfigNovaAfericaoAvulsa(conn, codUnidade);
            return SistemaWebFinattoConverter.createNovaAfericaoAvulsa(codUnidade,
                                                                       pneuByCodigo,
                                                                       configuracaoAfericao,
                                                                       afericaoRealizadaAvulsaHolder);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            conn.setAutoCommit(false);
            final ZoneId zoneIdForCodUnidade = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, Collections.singletonList(codUnidade));
            final Long codAfericaoInserida =
                    integracaoDao.insertAfericao(conn, codUnidade, unidadeDeParaHolder.getCodFiliais(), afericao);
            if (afericao instanceof AfericaoPlaca) {
                internalInsertAfericaoPlaca(conn, unidadeDeParaHolder, zoneIdForCodUnidade, (AfericaoPlaca) afericao);
            } else {
                internalInsertAfericaoAvulsa(conn, unidadeDeParaHolder, zoneIdForCodUnidade, (AfericaoAvulsa) afericao);
            }
            conn.commit();
            return codAfericaoInserida;
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public List<VeiculoListagem> getVeiculosByUnidades(@NotNull final List<Long> codUnidades,
                                                       final boolean apenasAtivos,
                                                       @Nullable final Long codTipoVeiculo) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, codUnidades);
            final TipoVeiculoConfigAfericaoHolder tipoVeiculoConfigAfericaoHolder =
                    integracaoDao.getTipoVeiculoConfigAfericaoHolder(conn,
                                                                     unidadeDeParaHolder.getCodUnidadesMapeadas());
            final List<VeiculoWebFinatto> veiculosByFiliais =
                    internalGetVeiculosByFiliais(conn,
                                                 unidadeDeParaHolder.getCodEmpresaProlog(),
                                                 unidadeDeParaHolder.getCodFiliais());
            return SistemaWebFinattoConverter.createVeiculosListagem(unidadeDeParaHolder,
                                                                     tipoVeiculoConfigAfericaoHolder,
                                                                     veiculosByFiliais);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public VeiculoVisualizacao getVeiculoByCodigo(@NotNull final Long codVeiculo) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final Long codUnidadeProlog = WebFinattoEncoderDecoder.extraiCodUnidade(codVeiculo);
            final Long codVeiculoProlog = WebFinattoEncoderDecoder.extraiCodVeiculo(codVeiculo);
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, Collections.singletonList(codUnidadeProlog));

            final List<VeiculoWebFinatto> veiculosByFiliais =
                    internalGetVeiculosByFiliais(conn,
                                                 unidadeDeParaHolder.getCodEmpresaProlog(),
                                                 unidadeDeParaHolder.getCodFiliais());
            final VeiculoWebFinatto veiculoWebFinatto = veiculosByFiliais.stream()
                    .filter(veiculo -> veiculo.getCodVeiculo().equalsIgnoreCase(codVeiculoProlog.toString()))
                    .findAny()
                    .orElseThrow(() -> {
                        throw new SistemaWebFinattoException("Placa não encontrada para movimentar");
                    });
            final VeiculoWebFinatto veiculoByPlaca =
                    internalGetVeiculoByPlaca(conn,
                                              unidadeDeParaHolder.getCodEmpresaProlog(),
                                              unidadeDeParaHolder.getCodFiliais(),
                                              veiculoWebFinatto.getPlacaVeiculo());
            final TipoVeiculoConfigAfericaoHolder tipoVeiculoConfigAfericaoHolder =
                    integracaoDao.getTipoVeiculoConfigAfericaoHolder(conn,
                                                                     unidadeDeParaHolder.getCodUnidadesMapeadas());
            final IntegracaoPosicaoPneuMapper posicaoPneuMapper = new IntegracaoPosicaoPneuMapper(
                    veiculoByPlaca.getCodEstruturaVeiculo(),
                    integracaoDao.getMapeamentoPosicoesPrologByDeParaTipoVeiculo(
                            conn,
                            unidadeDeParaHolder.getCodEmpresaProlog(),
                            veiculoByPlaca.getCodEstruturaVeiculo()));
            return SistemaWebFinattoConverter.createVeiculoVisualizacao(unidadeDeParaHolder,
                                                                        tipoVeiculoConfigAfericaoHolder,
                                                                        posicaoPneuMapper,
                                                                        veiculoByPlaca);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public Veiculo getVeiculoByPlaca(@NotNull final String placa,
                                     @Nullable final Long codUnidade,
                                     final boolean withPneus) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, Collections.singletonList(codUnidade));
            final VeiculoWebFinatto veiculoByPlaca =
                    internalGetVeiculoByPlaca(conn,
                                              unidadeDeParaHolder.getCodEmpresaProlog(),
                                              unidadeDeParaHolder.getCodFiliais(),
                                              placa);
            final TipoVeiculoConfigAfericaoHolder tipoVeiculoConfigAfericaoHolder =
                    integracaoDao.getTipoVeiculoConfigAfericaoHolder(conn,
                                                                     unidadeDeParaHolder.getCodUnidadesMapeadas());
            final TipoVeiculoConfigAfericao tipoVeiculoConfigAfericao =
                    tipoVeiculoConfigAfericaoHolder.get(veiculoByPlaca.getCodEmpresaFilialVeiculo(),
                                                        veiculoByPlaca.getCodEstruturaVeiculo());
            final IntegracaoPosicaoPneuMapper posicaoPneuMapper =
                    new IntegracaoPosicaoPneuMapper(veiculoByPlaca.getCodEstruturaVeiculo(),
                                                    integracaoDao.getMapeamentoPosicoesPrologByDeParaTipoVeiculo(
                                                            conn,
                                                            unidadeDeParaHolder.getCodEmpresaProlog(),
                                                            veiculoByPlaca.getCodEstruturaVeiculo()));

            final Set<EixoVeiculo> eixosDiagrama = Injection.provideVeiculoDao()
                    .getEixosDiagrama(tipoVeiculoConfigAfericao.getCodDiagramaVeiculo(), conn);

            return SistemaWebFinattoConverter.createVeiculo(unidadeDeParaHolder,
                                                            tipoVeiculoConfigAfericao,
                                                            posicaoPneuMapper,
                                                            eixosDiagrama,
                                                            veiculoByPlaca);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public VeiculoDadosColetaKm getDadosColetaKmByCodigo(@NotNull final Long codVeiculo) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final Long codUnidadeProlog = WebFinattoEncoderDecoder.extraiCodUnidade(codVeiculo);
            final Long codVeiculoProlog = WebFinattoEncoderDecoder.extraiCodVeiculo(codVeiculo);
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, Collections.singletonList(codUnidadeProlog));

            final List<VeiculoWebFinatto> veiculosByFiliais =
                    internalGetVeiculosByFiliais(conn,
                                                 unidadeDeParaHolder.getCodEmpresaProlog(),
                                                 unidadeDeParaHolder.getCodFiliais());
            final VeiculoWebFinatto veiculoWebFinatto = veiculosByFiliais.stream()
                    .filter(veiculo -> veiculo.getCodVeiculo().equalsIgnoreCase(codVeiculoProlog.toString()))
                    .findAny()
                    .orElseThrow(() -> {
                        throw new SistemaWebFinattoException("Placa não encontrada para movimentar");
                    });
            return SistemaWebFinattoConverter.createVeiculoDadosColetaKm(veiculoWebFinatto);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public List<Pneu> getPneusByCodUnidadesByStatus(@NotNull final List<Long> codUnidades,
                                                    @NotNull final StatusPneu status) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, codUnidades);
            final List<PneuWebFinatto> pneusByFiliais =
                    internalGetPneusByFiliais(conn,
                                              unidadeDeParaHolder.getCodEmpresaProlog(),
                                              unidadeDeParaHolder.getCodFiliais());
            return SistemaWebFinattoConverter.createPneusByStatus(unidadeDeParaHolder,
                                                                  status,
                                                                  pneusByFiliais);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public Long insert(@NotNull final ServicoDao servicoDao,
                       @NotNull final CampoPersonalizadoDao campoPersonalizadoDao,
                       @NotNull final ProcessoMovimentacao processoMovimentacao,
                       @NotNull final OffsetDateTime dataHoraMovimentacao,
                       final boolean fecharServicosAutomaticamente) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            conn.setAutoCommit(false);
            final Long codUnidadeProlog = processoMovimentacao.getUnidade().getCodigo();
            final ZoneId zoneIdForCodUnidade = TimeZoneManager.getZoneIdForCodUnidade(codUnidadeProlog, conn);
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, Collections.singletonList(codUnidadeProlog));
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           unidadeDeParaHolder.getCodEmpresaProlog(),
                                                           getSistemaKey(),
                                                           MetodoIntegrado.INSERT_AFERICAO_PLACA);

            requester.insertProcessoMovimentacao(apiAutenticacaoHolder,
                                                 SistemaWebFinattoConverter.createProcessoMovimentacao(
                                                         unidadeDeParaHolder,
                                                         dataHoraMovimentacao,
                                                         zoneIdForCodUnidade,
                                                         processoMovimentacao));

            conn.commit();
            return 1L;
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @NotNull
    @Override
    public List<Empresa> getFiltros(@NotNull final Long cpf) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           3L,
                                                           getSistemaKey(),
                                                           MetodoIntegrado.GET_LOCAIS_DE_MOVIMENTO);
            final List<EmpresaWebFinatto> filtrosClientes = requester.getFiltrosClientes(apiAutenticacaoHolder);
            return SistemaWebFinattoConverter.createEmpresa(filtrosClientes);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    private void internalInsertAfericaoPlaca(@NotNull final Connection conn,
                                             @NotNull final UnidadeDeParaHolder unidadeDeParaHolder,
                                             @NotNull final ZoneId zoneIdForCodUnidade,
                                             @NotNull final AfericaoPlaca afericaoPlaca) throws Throwable {
        final ApiAutenticacaoHolder apiAutenticacaoHolder =
                integracaoDao.getApiAutenticacaoHolder(conn,
                                                       unidadeDeParaHolder.getCodEmpresaProlog(),
                                                       getSistemaKey(),
                                                       MetodoIntegrado.INSERT_AFERICAO_PLACA);
        final AfericaoPlacaWebFinatto afericaoPlacaWebFinatto =
                SistemaWebFinattoConverter.createAfericaoPlaca(unidadeDeParaHolder,
                                                               zoneIdForCodUnidade,
                                                               afericaoPlaca);
        requester.insertAfericaoPlaca(apiAutenticacaoHolder, afericaoPlacaWebFinatto);
    }

    private void internalInsertAfericaoAvulsa(@NotNull final Connection conn,
                                              @NotNull final UnidadeDeParaHolder unidadeDeParaHolder,
                                              @NotNull final ZoneId zoneIdForCodUnidade,
                                              @NotNull final AfericaoAvulsa afericaoAvulsa) throws Throwable {
        final ApiAutenticacaoHolder apiAutenticacaoHolder =
                integracaoDao.getApiAutenticacaoHolder(conn,
                                                       unidadeDeParaHolder.getCodEmpresaProlog(),
                                                       getSistemaKey(),
                                                       MetodoIntegrado.INSERT_AFERICAO_AVULSA);
        final AfericaoPneuWebFinatto afericaoPneuWebFinatto =
                SistemaWebFinattoConverter.createAfericaoAvulsa(unidadeDeParaHolder,
                                                                zoneIdForCodUnidade,
                                                                afericaoAvulsa);
        requester.insertAfericaoAvulsa(apiAutenticacaoHolder, afericaoPneuWebFinatto);
    }

    @NotNull
    private List<VeiculoWebFinatto> internalGetVeiculosByFiliais(@NotNull final Connection conn,
                                                                 @NotNull final Long codEmpresaProlog,
                                                                 @NotNull final String codFiliais) throws Throwable {
        final ApiAutenticacaoHolder apiAutenticacaoHolder =
                integracaoDao.getApiAutenticacaoHolder(conn,
                                                       codEmpresaProlog,
                                                       getSistemaKey(),
                                                       MetodoIntegrado.GET_VEICULOS_CRONOGRAMA_AFERICAO);
        return requester.getVeiculosByFiliais(apiAutenticacaoHolder,
                                              codFiliais,
                                              null);
    }

    @NotNull
    private VeiculoWebFinatto internalGetVeiculoByPlaca(@NotNull final Connection conn,
                                                        @NotNull final Long codEmpresaProlog,
                                                        @NotNull final String codFilial,
                                                        @NotNull final String placaVeiculo) throws Throwable {
        final ApiAutenticacaoHolder apiAutenticacaoHolder =
                integracaoDao.getApiAutenticacaoHolder(conn,
                                                       codEmpresaProlog,
                                                       getSistemaKey(),
                                                       MetodoIntegrado.GET_VEICULO_NOVA_AFERICAO_PLACA);
        return requester.getVeiculoByPlaca(apiAutenticacaoHolder,
                                           codFilial,
                                           placaVeiculo);
    }

    @NotNull
    private List<PneuWebFinatto> internalGetPneusByFiliais(@NotNull final Connection conn,
                                                           @NotNull final Long codEmpresaProlog,
                                                           @NotNull final String codFiliais) throws Throwable {
        final ApiAutenticacaoHolder apiAutenticacaoHolder =
                integracaoDao.getApiAutenticacaoHolder(conn,
                                                       codEmpresaProlog,
                                                       getSistemaKey(),
                                                       MetodoIntegrado.GET_PNEUS_AFERICAO_AVULSA);
        return requester.getPneusByFiliais(apiAutenticacaoHolder,
                                           codFiliais,
                                           "ESTOQUE",
                                           null);
    }
}
