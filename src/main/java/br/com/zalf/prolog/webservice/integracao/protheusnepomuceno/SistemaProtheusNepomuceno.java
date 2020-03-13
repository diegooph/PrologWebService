package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.PneuEstoqueProtheusNepomuceno;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data.ProtheusNepomucenoRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.transport.MetodoIntegrado;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
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
        return super.getCronogramaAfericao(codUnidades);
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