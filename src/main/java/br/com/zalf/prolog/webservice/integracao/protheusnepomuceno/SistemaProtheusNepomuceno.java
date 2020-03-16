package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.*;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data.ProtheusNepomucenoRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.transport.MetodoIntegrado;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        final SistemaProtheusNepomucenoDaoImpl sistemaProtheusNepomucenoDaoImpl = new SistemaProtheusNepomucenoDaoImpl();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            conn.setAutoCommit(false);
            final Long codEmpresa = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);
            final String codAuxiliarUnidade = sistemaProtheusNepomucenoDaoImpl.getCodAuxiliarUnidade(conn, codUnidade);

            // Deixamos para inserir a aferição no Prolog logo antes de enviar para o Protheus. Assim garantimos que
            // só teremos um rollback caso tenhamos erro no Protheus.
            final Long codAfericaoInserida =
                    sistemaProtheusNepomucenoDaoImpl.insert(conn, codUnidade, afericao);

            if (afericao instanceof AfericaoPlaca) {
                requester.insertAfericaoPlaca(
                        getIntegradorProLog()
                                .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.INSERT_AFERICAO_PLACA),
                        ProtheusNepomucenoConverter.convert(codAuxiliarUnidade, (AfericaoPlaca) afericao));
            } else {
                requester.insertAfericaoAvulsa(
                        getIntegradorProLog()
                                .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.INSERT_AFERICAO_AVULSA),
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
        final SistemaProtheusNepomucenoDaoImpl sistemaProtheusNepomucenoDaoImpl = new SistemaProtheusNepomucenoDaoImpl();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final Long codEmpresa = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidades.get(0));

            final Map<String, InfosUnidadeRestricao> unidadeRestricao =
                    sistemaProtheusNepomucenoDaoImpl.getInfosUnidadeRestricao(conn, codUnidades);
            final Map<String, InfosTipoVeiculoConfiguracaoAfericao> tipoVeiculoConfiguracao =
                    sistemaProtheusNepomucenoDaoImpl.getInfosTipoVeiculoConfiguracaoAfericao(conn, codUnidades);

            final List<VeiculoListagemProtheusNepomuceno> listagemVeiculos =
                    requester.getListagemVeiculosUnidadesSelecionadas(
                            getIntegradorProLog()
                                    .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.GET_VEICULOS_CRONOGRAMA_AFERICAO),
                            codFiliais);
            final List<String> placasNepomuceno = listagemVeiculos.stream()
                    .map(VeiculoListagemProtheusNepomuceno::getPlacaVeiculo)
                    .distinct()
                    .collect(Collectors.toList());

            final Map<String, InfosAfericaoRealizadaPlaca> afericaoRealizadaPlaca =
                    sistemaProtheusNepomucenoDaoImpl.getInfosAfericaoRealizadaPlaca(conn, placasNepomuceno);

            // Aqui começamos a montar o cronograma
            final Map<String, ModeloPlacasAfericao> modelosEstruturaVeiculo = new HashMap<>();
            final Map<String, List<ModeloPlacasAfericao.PlacaAfericao>> placasEstruturaVeiculo = new HashMap<>();
            final CronogramaAfericao cronogramaAfericao = new CronogramaAfericao();
            for (final VeiculoListagemProtheusNepomuceno veiculo : listagemVeiculos) {
                if (!modelosEstruturaVeiculo.containsKey(veiculo.getCodModeloVeiculo())) {
                    modelosEstruturaVeiculo.put(
                            veiculo.getCodModeloVeiculo(),
                            createModeloPlacasAfericao(veiculo, placasEstruturaVeiculo.get(veiculo.getCodModeloVeiculo())));
                }

                if (placasEstruturaVeiculo.containsKey(veiculo.getCodModeloVeiculo())) {
                    placasEstruturaVeiculo.get(veiculo.getCodModeloVeiculo()).add(
                            createPlacaAfericao(
                                    veiculo,
                                    unidadeRestricao,
                                    tipoVeiculoConfiguracao,
                                    afericaoRealizadaPlaca));
                } else {
                    placasEstruturaVeiculo.put(veiculo.getCodModeloVeiculo(), new ArrayList<>());
                }

            }
            final ArrayList<ModeloPlacasAfericao> modelosPlacasAfericao = new ArrayList<>(modelosEstruturaVeiculo.values());
            cronogramaAfericao.setModelosPlacasAfericao(modelosPlacasAfericao);
            int totalModelosSulcoOk = 0;
            int totalModelosPressaoOk = 0;
            int totalModelosSulcoPressaoOk = 0;
            for (final ModeloPlacasAfericao modeloPlacasAfericao : modelosPlacasAfericao) {
                totalModelosSulcoOk = totalModelosSulcoOk + modeloPlacasAfericao.getQtdModeloSulcoOk();
                totalModelosPressaoOk = totalModelosPressaoOk + modeloPlacasAfericao.getQtdModeloPressaoOk();
                totalModelosSulcoPressaoOk = totalModelosSulcoPressaoOk + modeloPlacasAfericao.getQtdModeloSulcoPressaoOk();
            }
            cronogramaAfericao.setTotalSulcosOk(totalModelosSulcoOk);
            cronogramaAfericao.setTotalPressaoOk(totalModelosPressaoOk);
            cronogramaAfericao.setTotalSulcoPressaoOk(totalModelosSulcoPressaoOk);
            cronogramaAfericao.setTotalVeiculos(listagemVeiculos.size());
            return cronogramaAfericao;
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    private ModeloPlacasAfericao createModeloPlacasAfericao(
            @NotNull final VeiculoListagemProtheusNepomuceno veiculo,
            @NotNull final List<ModeloPlacasAfericao.PlacaAfericao> placasAfericao) {
        final ModeloPlacasAfericao modeloPlacasAfericao = new ModeloPlacasAfericao();
        modeloPlacasAfericao.setNomeModelo(veiculo.getNomeModeloVeiculo());
        modeloPlacasAfericao.setPlacasAfericao(placasAfericao);
        int qtdModeloSulcoOk = 0;
        int qtdModeloPressaoOk = 0;
        int qtdModeloSulcoPressaoOk = 0;
        for (final ModeloPlacasAfericao.PlacaAfericao placa : placasAfericao) {
            if (placa.isAfericaoPressaoNoPrazo(placa.getMetaAfericaoPressao())
                    && placa.isAfericaoSulcoNoPrazo(placa.getMetaAfericaoSulco())) {
                qtdModeloSulcoPressaoOk++;
                qtdModeloPressaoOk++;
                qtdModeloSulcoOk++;
            } else if (placa.isAfericaoSulcoNoPrazo(placa.getMetaAfericaoSulco())) {
                qtdModeloSulcoOk++;
            } else {
                qtdModeloPressaoOk++;
            }
        }
        modeloPlacasAfericao.setQtdModeloSulcoOk(qtdModeloSulcoOk);
        modeloPlacasAfericao.setQtdModeloPressaoOk(qtdModeloPressaoOk);
        modeloPlacasAfericao.setQtdModeloSulcoPressaoOk(qtdModeloSulcoPressaoOk);
        modeloPlacasAfericao.setTotalVeiculosModelo(placasAfericao.size());
        return modeloPlacasAfericao;
    }

    private ModeloPlacasAfericao.PlacaAfericao createPlacaAfericao(
            @NotNull final VeiculoListagemProtheusNepomuceno veiculo,
            @NotNull final Map<String, InfosUnidadeRestricao> unidadeRestricao,
            @NotNull final Map<String, InfosTipoVeiculoConfiguracaoAfericao> tipoVeiculoConfiguracao,
            @NotNull final Map<String, InfosAfericaoRealizadaPlaca> afericaoRealizadaPlaca) {
        final ModeloPlacasAfericao.PlacaAfericao placaAfericao = new ModeloPlacasAfericao.PlacaAfericao();
        placaAfericao.setPlaca(veiculo.getPlacaVeiculo());
        final InfosAfericaoRealizadaPlaca infosAfericaoRealizadaPlaca =
                afericaoRealizadaPlaca.get(veiculo.getPlacaVeiculo());
        placaAfericao.setIntervaloUltimaAfericaoPressao(infosAfericaoRealizadaPlaca.getDiasUltimaAfericaoPressao());
        placaAfericao.setIntervaloUltimaAfericaoSulco(infosAfericaoRealizadaPlaca.getDiasUltimaAfericaoSulco());
        placaAfericao.setQuantidadePneus(veiculo.getQtsPneusAplicadosVeiculo());
        final InfosTipoVeiculoConfiguracaoAfericao infosTipoVeiculoConfiguracaoAfericao =
                tipoVeiculoConfiguracao.get(veiculo.getCodEstruturaVeiculo());
        placaAfericao.setPodeAferirSulco(infosTipoVeiculoConfiguracaoAfericao.isPodeAferirSulco());
        placaAfericao.setPodeAferirPressao(infosTipoVeiculoConfiguracaoAfericao.isPodeAferirPressao());
        placaAfericao.setPodeAferirSulcoPressao(infosTipoVeiculoConfiguracaoAfericao.isPodeAferirSulcoPressao());
        placaAfericao.setPodeAferirEstepe(infosTipoVeiculoConfiguracaoAfericao.isPodeAferirEstepes());
        final InfosUnidadeRestricao infosUnidadeRestricao = unidadeRestricao.get(veiculo.getCodEmpresaFilialVeiculo());
        placaAfericao.setMetaAfericaoSulco(infosUnidadeRestricao.getPeriodoDiasAfericaoSulco());
        placaAfericao.setMetaAfericaoPressao(infosUnidadeRestricao.getPeriodoDiasAfericaoPressao());
        return placaAfericao;
    }

    @Override
    @NotNull
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placaVeiculo,
                                                  @NotNull final String tipoAfericao) throws Throwable {
        return super.getNovaAfericaoPlaca(codUnidade, placaVeiculo, tipoAfericao);
    }

    @Override
    @NotNull
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        final SistemaProtheusNepomucenoDaoImpl sistemaProtheusNepomucenoDaoImpl = new SistemaProtheusNepomucenoDaoImpl();
        try {
            conn = connectionProvider.provideDatabaseConnection();

            // Busca o código auxiliar da unidade selecionada
            final String codAuxiliarUnidade = sistemaProtheusNepomucenoDaoImpl.getCodAuxiliarUnidade(conn, codUnidade);
            final Long codEmpresa = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);

            // Busca a lista de pneus em estoque do Protheus
            final List<PneuEstoqueProtheusNepomuceno> pneusEstoqueProtheus =
                    requester.getListagemPneusEmEstoque(
                            getIntegradorProLog()
                                    .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.GET_PNEUS_AFERICAO_AVULSA),
                            codAuxiliarUnidade);

            final List<String> codPneus =
                    pneusEstoqueProtheus.stream().map(PneuEstoqueProtheusNepomuceno::getCodPneu).collect(Collectors.toList());

            /**
             TODO:
             1 - Criar uma dao para buscar as infos de aferição com base nos pneus da lista codPneus.
             2 - Utilizar a function FUNC_PNEU_AFERICAO_GET_INFOS_AFERICOES_INTEGRADA e criar uma lista com o objeto de
             InfosAfericaoAvulsa.
             3 - Percorrer a lista de objetos de pneusEstoqueProtheus e montar o objeto PneuAfericaoAvulsa, cruzando com
             as informações da lista criada no item 2.
             */

            throw new IllegalStateException("erro pq sim");
        } catch (final Throwable t) {
            throw t;
        } finally {
            connectionProvider.closeResources(conn);
        }
//        return super.getPneusAfericaoAvulsa(codUnidade);
    }

    @Override
    @NotNull
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable {
        return null;
    }
}