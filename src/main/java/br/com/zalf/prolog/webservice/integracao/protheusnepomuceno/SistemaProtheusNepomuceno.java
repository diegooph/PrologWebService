package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.PosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.*;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data.ProtheusNepomucenoRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.transport.MetodoIntegrado;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 3/10/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SistemaProtheusNepomuceno extends Sistema {
    @NotNull
    private final ProtheusNepomucenoRequesterImpl requester;

    public SistemaProtheusNepomuceno(@NotNull final ProtheusNepomucenoRequesterImpl requester,
                                     @NotNull final SistemaKey sistemaKey,
                                     @NotNull final IntegradorProLog integradorProLog,
                                     @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
        this.requester = requester;
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
            // buscamos as informações logo no começo do processo, assim, se der erro nada mais é executado.
            final SistemaProtheusNepomucenoDaoImpl sistema = new SistemaProtheusNepomucenoDaoImpl();
            final Long codEmpresaProlog = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);
            final String codAuxiliarUnidade = getIntegradorProLog().getCodAuxiliarByCodUnidadeProlog(conn, codUnidade);

            // Deixamos para inserir a aferição no Prolog logo antes de enviar para o Protheus. Assim garantimos que
            // só teremos um rollback caso tenhamos erro no Protheus.
            final Long codAfericaoInserida = sistema.insert(conn, codUnidade, afericao);

            if (afericao instanceof AfericaoPlaca) {
                requester.insertAfericaoPlaca(
                        getIntegradorProLog()
                                .getUrl(conn, codEmpresaProlog, getSistemaKey(), MetodoIntegrado.INSERT_AFERICAO_PLACA),
                        ProtheusNepomucenoConverter.convert(codAuxiliarUnidade, (AfericaoPlaca) afericao));
            } else {
                final String url = getIntegradorProLog()
                        .getUrl(conn, codEmpresaProlog, getSistemaKey(), MetodoIntegrado.INSERT_AFERICAO_AVULSA);
                requester.insertAfericaoAvulsa(
                        url,
                        ProtheusNepomucenoConverter.convert(codAuxiliarUnidade, (AfericaoAvulsa) afericao));
            }
            conn.commit();
            return codAfericaoInserida;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public CronogramaAfericao getCronogramaAfericao(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            // Podemos, com toda certeza, utilizar codUnidades.get(0) pois no mínimo teremos uma unidade nesta lista.
            final Long codEmpresa = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidades.get(0));

            final SistemaProtheusNepomucenoDaoImpl sistema = new SistemaProtheusNepomucenoDaoImpl();
            final Map<String, InfosUnidadeRestricao> unidadeRestricao =
                    sistema.getInfosUnidadeRestricao(conn, codUnidades);
            final Map<String, InfosTipoVeiculoConfiguracaoAfericao> tipoVeiculoConfiguracao =
                    sistema.getInfosTipoVeiculoConfiguracaoAfericao(conn, codUnidades);

            final String url = getIntegradorProLog()
                    .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.GET_VEICULOS_CRONOGRAMA_AFERICAO);
            final String codFiliais = sistema.getCodFiliais(conn, codUnidades);
            final List<VeiculoListagemProtheusNepomuceno> listagemVeiculos =
                    requester.getListagemVeiculosUnidadesSelecionadas(url, codFiliais);
            final List<String> placasNepomuceno = listagemVeiculos.stream()
                    .map(VeiculoListagemProtheusNepomuceno::getPlacaVeiculo)
                    .distinct()
                    .collect(Collectors.toList());

            final Map<String, InfosAfericaoRealizadaPlaca> afericaoRealizadaPlaca =
                    sistema.getInfosAfericaoRealizadaPlaca(conn, codEmpresa, placasNepomuceno);

            // Aqui começamos a montar o cronograma
            final Map<String, ModeloPlacasAfericao> modelosEstruturaVeiculo = new HashMap<>();
            final Map<String, List<ModeloPlacasAfericao.PlacaAfericao>> placasEstruturaVeiculo = new HashMap<>();
            for (final VeiculoListagemProtheusNepomuceno veiculo : listagemVeiculos) {
                if (!modelosEstruturaVeiculo.containsKey(veiculo.getCodModeloVeiculo())) {
                    modelosEstruturaVeiculo.put(
                            veiculo.getCodModeloVeiculo(),
                            ProtheusNepomucenoConverter.createModeloPlacasAfericaoProlog(
                                    veiculo,
                                    placasEstruturaVeiculo.get(veiculo.getCodModeloVeiculo())));
                }

                if (placasEstruturaVeiculo.containsKey(veiculo.getCodModeloVeiculo())) {
                    placasEstruturaVeiculo.get(veiculo.getCodModeloVeiculo()).add(
                            ProtheusNepomucenoConverter.createPlacaAfericaoProlog(
                                    veiculo,
                                    unidadeRestricao,
                                    tipoVeiculoConfiguracao,
                                    afericaoRealizadaPlaca));
                } else {
                    placasEstruturaVeiculo.put(veiculo.getCodModeloVeiculo(), new ArrayList<>());
                }

            }
            return ProtheusNepomucenoConverter
                    .createCronogramaAfericaoProlog(modelosEstruturaVeiculo, listagemVeiculos.size());
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
            final SistemaProtheusNepomucenoDaoImpl sistema = new SistemaProtheusNepomucenoDaoImpl();
            final Long codEmpresa = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);

            final String url = getIntegradorProLog()
                    .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.GET_VEICULO_NOVA_AFERICAO_PLACA);
            final String codEmpresaFilial = getIntegradorProLog().getCodAuxiliarByCodUnidadeProlog(conn, codUnidade);
            final VeiculoAfericaoProtheusNepomuceno veiculoAfericao =
                    requester.getPlacaPneusAfericaoPlaca(url, codEmpresaFilial, placaVeiculo);

            final ConfiguracaoNovaAfericaoPlaca configuracaoAfericao =
                    sistema.getConfigNovaAfericaoPlaca(
                            conn,
                            codUnidade,
                            veiculoAfericao.getCodEstruturaVeiculo());
            final PosicaoPneuMapper posicaoPneuMapper = new PosicaoPneuMapper(
                    sistema
                            .getMapeamentoPosicoesProlog(conn, codEmpresa, veiculoAfericao.getCodEstruturaVeiculo()));
            final Short codDiagramaPlaca =
                    sistema
                            .getCodDiagramaByCodEstrutura(conn, codEmpresa, veiculoAfericao.getCodEstruturaVeiculo());
            final Veiculo veiculo =
                    ProtheusNepomucenoConverter
                            .createVeiculoProlog(codUnidade, codDiagramaPlaca, veiculoAfericao, posicaoPneuMapper);
            return ProtheusNepomucenoConverter.createNovaAfericaoPlacaProlog(veiculo, configuracaoAfericao);
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
            final SistemaProtheusNepomucenoDaoImpl sistema = new SistemaProtheusNepomucenoDaoImpl();
            final String codAuxiliarUnidade = getIntegradorProLog().getCodAuxiliarByCodUnidadeProlog(conn, codUnidade);
            final Long codEmpresaProlog = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);

            // Busca a lista de pneus em estoque do Protheus.
            final String url = getIntegradorProLog()
                    .getUrl(conn, codEmpresaProlog, getSistemaKey(), MetodoIntegrado.GET_PNEUS_AFERICAO_AVULSA);
            final List<PneuEstoqueProtheusNepomuceno> pneusEstoqueNepomuceno =
                    requester.getListagemPneusEmEstoque(url, codAuxiliarUnidade);

            final List<String> codPneus = pneusEstoqueNepomuceno.stream()
                    .map(PneuEstoqueProtheusNepomuceno::getCodigoCliente)
                    .collect(Collectors.toList());

            // Busca as infos de aferição com base nos pneus da lista codPneus.
            final List<InfosAfericaoAvulsa> infosAfericaoAvulsa =
                    sistema.getInfosAfericaoAvulsa(conn, codUnidade, codPneus);

            final List<PneuAfericaoAvulsa> pneusAfericaoAvulsa = new ArrayList<>();
            for (PneuEstoqueProtheusNepomuceno pneuEstoqueNepomuceno : pneusEstoqueNepomuceno) {
                final InfosAfericaoAvulsa pneuInfoAfericaoAvulsa = infosAfericaoAvulsa.stream()
                        .filter(infoPneu ->
                                infoPneu.getCodPneuCliente().equals(pneuEstoqueNepomuceno.getCodigoCliente()))
                        .findFirst()
                        .orElse(null);
                pneusAfericaoAvulsa.add(
                        ProtheusNepomucenoConverter
                                .createPneuAfericaoAvulsaProlog(pneuEstoqueNepomuceno, pneuInfoAfericaoAvulsa));
            }
            return pneusAfericaoAvulsa;
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
            final SistemaProtheusNepomucenoDaoImpl sistema = new SistemaProtheusNepomucenoDaoImpl();
            final String codAuxiliarUnidade = getIntegradorProLog().getCodAuxiliarByCodUnidadeProlog(conn, codUnidade);
            final Long codEmpresaProlog = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);

            // Busca a lista de pneus em estoque do Protheus.
            final String url = getIntegradorProLog()
                    .getUrl(conn, codEmpresaProlog, getSistemaKey(), MetodoIntegrado.GET_PNEU_NOVA_AFERICAO_AVULSA);
            final PneuEstoqueProtheusNepomuceno pneuEstoqueNepomuceno =
                    requester.getPneuEmEstoqueAfericaoAvulsa(url, codAuxiliarUnidade, String.valueOf(codPneu));

            // Busca as infos de aferição com base nos pneus da lista codPneus.
            final List<InfosAfericaoAvulsa> infosAfericaoAvulsa =
                    sistema.getInfosAfericaoAvulsa(
                            conn,
                            codUnidade,
                            Collections.singletonList(pneuEstoqueNepomuceno.getCodigoCliente()));

            final InfosAfericaoAvulsa pneuInfoAfericaoAvulsa = infosAfericaoAvulsa.stream()
                    .filter(infoPneu ->
                            infoPneu.getCodPneuCliente().equals(pneuEstoqueNepomuceno.getCodigoCliente()))
                    .findFirst()
                    .orElse(null);

            final ConfiguracaoNovaAfericaoAvulsa configuracaoAfericao =
                    sistema.getConfigNovaAfericaoAvulsa(conn, codUnidade);

            return ProtheusNepomucenoConverter
                    .createNovaAfericaoAvulsaProlog(
                            pneuEstoqueNepomuceno,
                            configuracaoAfericao,
                            pneuInfoAfericaoAvulsa);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }
}