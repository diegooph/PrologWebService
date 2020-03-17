package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuComum;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuEstoque;
import br.com.zalf.prolog.webservice.frota.pneu._model.Sulcos;
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
import java.time.LocalDateTime;
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
        try {
            conn = connectionProvider.provideDatabaseConnection();
            // Podemos, com toda certeza, utilizar codUnidades.get(0) pois no mínimo teremos uma unidade nesta lista.
            final Long codEmpresa = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidades.get(0));

            final SistemaProtheusNepomucenoDaoImpl sistemaProtheusNepomucenoDaoImpl =
                    new SistemaProtheusNepomucenoDaoImpl();
            final Map<String, InfosUnidadeRestricao> unidadeRestricao =
                    sistemaProtheusNepomucenoDaoImpl.getInfosUnidadeRestricao(conn, codUnidades);
            final Map<String, InfosTipoVeiculoConfiguracaoAfericao> tipoVeiculoConfiguracao =
                    sistemaProtheusNepomucenoDaoImpl.getInfosTipoVeiculoConfiguracaoAfericao(conn, codUnidades);

            final String url = getIntegradorProLog()
                    .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.GET_VEICULOS_CRONOGRAMA_AFERICAO);
            final String codFiliais = sistemaProtheusNepomucenoDaoImpl.getCodFiliais(conn, codUnidades);
            final List<VeiculoListagemProtheusNepomuceno> listagemVeiculos =
                    requester.getListagemVeiculosUnidadesSelecionadas(url, codFiliais);
            final List<String> placasNepomuceno = listagemVeiculos.stream()
                    .map(VeiculoListagemProtheusNepomuceno::getPlacaVeiculo)
                    .distinct()
                    .collect(Collectors.toList());

            final Map<String, InfosAfericaoRealizadaPlaca> afericaoRealizadaPlaca =
                    sistemaProtheusNepomucenoDaoImpl.getInfosAfericaoRealizadaPlaca(conn, codEmpresa, placasNepomuceno);

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
        final SistemaProtheusNepomucenoDaoImpl sistemaProtheusNepomucenoDaoImpl = new SistemaProtheusNepomucenoDaoImpl();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final Long codEmpresa = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);

            final String url = getIntegradorProLog()
                    .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.GET_VEICULO_NOVA_AFERICAO_PLACA);
            final String codFilial = sistemaProtheusNepomucenoDaoImpl.getCodAuxiliarUnidade(conn, codUnidade);
            final VeiculoAfericaoProtheusNepomuceno veiculoAfericao =
                    requester.getPlacaPneusAfericaoPlaca(url, codFilial, placaVeiculo);

            final ConfiguracaoNovaAfericaoPlaca configuracaoAfericao =
                    sistemaProtheusNepomucenoDaoImpl.getConfigNovaAfericaoPlaca(
                            conn,
                            codUnidade,
                            veiculoAfericao.getCodEstruturaVeiculo());
            final PosicaoPneuMapper posicaoPneuMapper = new PosicaoPneuMapper(
                    sistemaProtheusNepomucenoDaoImpl
                            .getMapeamentoPosicoesProlog(conn, codEmpresa, veiculoAfericao.getCodEstruturaVeiculo()));
            final Short codDiagramaPlaca =
                    sistemaProtheusNepomucenoDaoImpl
                            .getCodDiagramaByCodEstrutura(conn, codEmpresa, veiculoAfericao.getCodEstruturaVeiculo());
            final Veiculo veiculo =
                    ProtheusNepomucenoConverter
                            .createVeiculoProlog(codUnidade,codDiagramaPlaca, veiculoAfericao, posicaoPneuMapper);
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
        final SistemaProtheusNepomucenoDaoImpl sistemaProtheusNepomucenoDaoImpl = new SistemaProtheusNepomucenoDaoImpl();
        try {
            conn = connectionProvider.provideDatabaseConnection();

            // Busca o código auxiliar da unidade selecionada.
            final String codAuxiliarUnidade = sistemaProtheusNepomucenoDaoImpl.getCodAuxiliarUnidade(conn, codUnidade);
            final Long codEmpresa = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);

            // Busca a lista de pneus em estoque do Protheus.
            final List<PneuEstoqueProtheusNepomuceno> pneusEstoqueProtheus =
                    requester.getListagemPneusEmEstoque(
                            getIntegradorProLog()
                                    .getUrl(conn, codEmpresa, getSistemaKey(), MetodoIntegrado.GET_PNEUS_AFERICAO_AVULSA),
                            codAuxiliarUnidade);

            // Cria um array contendo apenas os códigos de pneus.
            final List<String> codPneus =
                    pneusEstoqueProtheus.stream().map(PneuEstoqueProtheusNepomuceno::getCodPneu).collect(Collectors.toList());

            // Busca as infos de aferição com base nos pneus da lista codPneus.
            final List<InfosAfericaoAvulsa> listagemInfosAfericaoAvulsa =
                    sistemaProtheusNepomucenoDaoImpl.getInfosAfericaoAvulsa(conn, codUnidade, codPneus);

            // Cria a variável de retorno com a lista de objetos de PneuAfericaoAvulsa.
            final List<PneuAfericaoAvulsa> pneusAfericaoAvulsa = new ArrayList<>();

            for (PneuEstoqueProtheusNepomuceno pneuEstoqueProtheusNepomuceno : pneusEstoqueProtheus){
                // Cria um objeto de PneuAfericaoAvulsa.
                final PneuAfericaoAvulsa pneuAfericaoAvulsa = new PneuAfericaoAvulsa();

                // Cria um objeto de Pneu.
                final PneuEstoque pneu = new PneuEstoque();
                pneu.setCodigo(Long.valueOf(pneuEstoqueProtheusNepomuceno.getCodPneu()));
                pneu.setCodigoCliente(pneuEstoqueProtheusNepomuceno.getCodigoCliente());
                pneu.setPressaoCorreta(pneuEstoqueProtheusNepomuceno.getPressaoRecomendadaPneu());
                pneu.setPressaoAtual(pneuEstoqueProtheusNepomuceno.getPressaoAtualPneu());
                pneu.setVidaAtual(pneuEstoqueProtheusNepomuceno.getVidaAtualPneu());
                pneu.setVidasTotal(pneuEstoqueProtheusNepomuceno.getVidaTotalPneu());

                final Sulcos sulcos = new Sulcos();
                sulcos.setInterno(pneuEstoqueProtheusNepomuceno.getSulcoInternoPneu());
                sulcos.setCentralInterno(pneuEstoqueProtheusNepomuceno.getSulcoCentralInternoPneu());
                sulcos.setCentralExterno(pneuEstoqueProtheusNepomuceno.getSulcoCentralExternoPneu());
                sulcos.setExterno(pneuEstoqueProtheusNepomuceno.getSulcoExternoPneu());
                pneu.setSulcosAtuais(sulcos);

                // Seta o objeto de Pneu ao Objeto PneuAfericaoAvulsa.
                pneuAfericaoAvulsa.setPneu(pneu);

                // Verifica se o pneu existe na listagem de informações das aferições avulsas realizadas.
                final Optional<InfosAfericaoAvulsa> optionalInfosAfericaoAvulsa =
                listagemInfosAfericaoAvulsa.stream()
                        .filter(InfosAfericaoAvulsa -> InfosAfericaoAvulsa.getCodPneuProlog()
                                .equals(pneuEstoqueProtheusNepomuceno.getCodPneu()))
                        .findFirst();

                if(optionalInfosAfericaoAvulsa.isPresent()){
                    pneuAfericaoAvulsa.setDataHoraUltimaAfericao(LocalDateTime.parse(optionalInfosAfericaoAvulsa.get().getDataHoraUltimaAfericao()));
                    pneuAfericaoAvulsa.setNomeColaboradorAfericao(optionalInfosAfericaoAvulsa.get().getNomeColaboradorAfericao());
                    pneuAfericaoAvulsa.setTipoMedicaoColetadaUltimaAfericao(TipoMedicaoColetadaAfericao.fromString(optionalInfosAfericaoAvulsa.get().getTipoMedicaoColetadaAfericao().toString()));
                    pneuAfericaoAvulsa.setCodigoUltimaAfericao(optionalInfosAfericaoAvulsa.get().getCodUltimaAfericao());
                    pneuAfericaoAvulsa.setTipoProcessoAfericao(optionalInfosAfericaoAvulsa.get().getTipoProcessoColetaAfericao());
                    pneuAfericaoAvulsa.setPlacaAplicadoQuandoAferido(optionalInfosAfericaoAvulsa.get().getPlacaAplicadoQuandoAferido());
                }

                pneusAfericaoAvulsa.add(pneuAfericaoAvulsa);
            }

            return pneusAfericaoAvulsa;
        } catch (final Throwable t) {
            throw t;
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
        return null;
    }
}