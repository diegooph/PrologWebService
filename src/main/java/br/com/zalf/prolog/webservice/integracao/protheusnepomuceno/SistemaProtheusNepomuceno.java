package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.BuildConfig;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.MetodoIntegrado;
import br.com.zalf.prolog.webservice.integracao.PosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.*;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data.ProtheusNepomucenoRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @NotNull
    @Override
    public Long insertTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        // Validamos se o codAuxiliar está dentro dos padrões. Uma exception personalizada é lançada caso não estiver
        // de acordo.
        validateCodAuxiliar(tipoVeiculo.getCodEmpresa(), tipoVeiculo.getCodigo(), tipoVeiculo.getCodAuxiliar());
        // Usamos o fluxo padrão do Prolog, apenas validamos antes
        return getIntegradorProLog().insertTipoVeiculo(tipoVeiculo);
    }

    @Override
    public void updateTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        // Validamos se o codAuxiliar está dentro dos padrões. Uma exception personalizada é lançada caso não estiver
        // de acordo.
        validateCodAuxiliar(tipoVeiculo.getCodEmpresa(), tipoVeiculo.getCodigo(), tipoVeiculo.getCodAuxiliar());
        // Usamos o fluxo padrão do Prolog, apenas validamos antes
        getIntegradorProLog().updateTipoVeiculo(tipoVeiculo);
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

            if (BuildConfig.DEBUG) {
                final List<VeiculoListagemProtheusNepomuceno> remove = listagemVeiculos.stream()
                        .filter(veiculo ->
                                !veiculo.getCodEstruturaVeiculo().equals("FA002:M0162")
                                        && !veiculo.getCodEstruturaVeiculo().equals("FA004:M0685"))
                        .collect(Collectors.toList());
                listagemVeiculos.removeAll(remove);
            }

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
                if (!placasEstruturaVeiculo.containsKey(veiculo.getCodModeloVeiculo())) {
                    placasEstruturaVeiculo.put(veiculo.getCodModeloVeiculo(), new ArrayList<>());
                }
                placasEstruturaVeiculo.get(veiculo.getCodModeloVeiculo()).add(
                        ProtheusNepomucenoConverter.createPlacaAfericaoProlog(
                                veiculo,
                                unidadeRestricao,
                                tipoVeiculoConfiguracao,
                                afericaoRealizadaPlaca));

                if (!modelosEstruturaVeiculo.containsKey(veiculo.getCodModeloVeiculo())) {
                    modelosEstruturaVeiculo.put(
                            veiculo.getCodModeloVeiculo(),
                            ProtheusNepomucenoConverter.createModeloPlacasAfericaoProlog(
                                    veiculo,
                                    placasEstruturaVeiculo.get(veiculo.getCodModeloVeiculo())));
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
                    sistema.getMapeamentoPosicoesProlog(conn, codEmpresa, veiculoAfericao.getCodEstruturaVeiculo()));
            final Short codDiagramaProlog =
                    sistema.getCodDiagramaByCodEstrutura(conn, codEmpresa, veiculoAfericao.getCodEstruturaVeiculo());
            final Veiculo veiculo =
                    ProtheusNepomucenoConverter
                            .createVeiculoProlog(
                                    codUnidade,
                                    codDiagramaProlog,
                                    veiculoAfericao,
                                    posicaoPneuMapper);
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
                                .createPneuAfericaoAvulsaProlog(
                                        codUnidade,
                                        pneuEstoqueNepomuceno,
                                        pneuInfoAfericaoAvulsa));
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
                    requester.getPneuEmEstoqueAfericaoAvulsa(
                            url,
                            codAuxiliarUnidade,
                            ProtheusNepomucenoEncoderDecoder.decode(codPneu));

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
                            codUnidade,
                            pneuEstoqueNepomuceno,
                            configuracaoAfericao,
                            pneuInfoAfericaoAvulsa);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    private void validateCodAuxiliar(@NotNull final Long codEmpresaTipoVeiculo,
                                     @Nullable final Long codTipoVeiculo,
                                     @Nullable final String codAuxiliarTipoVeiculo) throws Throwable {
        if (codAuxiliarTipoVeiculo == null) {
            // Não precisamos validar nada, se o código auxiliar for nulo.
            return;
        }
        // Validamos se o código a ser cadastrado está no padrão
        ProtheusNepomucenoUtils.validateCodAuxiliarTipoVeiculo(codAuxiliarTipoVeiculo);
        final List<String> codsAuxiliares =
                new SistemaProtheusNepomucenoDaoImpl()
                        .verificaCodAuxiliarTipoVeiculoValido(codEmpresaTipoVeiculo, codTipoVeiculo);
        final List<String> novosCodsAuxiliares =
                ProtheusNepomucenoUtils.getCodAuxiliarTipoVeiculoAsArray(codAuxiliarTipoVeiculo);
        // Estouramos uma exception com mensagem personalizada caso já existir qualquer código idêntico já mapeado.
        codsAuxiliares
                .stream()
                .filter(codAuxiliar -> {
                    for (final String novoCodAuxiliar : novosCodsAuxiliares) {
                        if (codAuxiliar.contains(novoCodAuxiliar) || novoCodAuxiliar.contains(codAuxiliar)) {
                            return true;
                        }
                    }
                    return false;
                })
                .findAny()
                .ifPresent(codAuxiliar -> {
                    throw new GenericException(
                            "O código auxiliar " + codAuxiliar +
                                    " já está cadastrado em outro Tipo de Veículo e não pode ser repetido");
                });
    }
}