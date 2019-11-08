package br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.VeiculoServico;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoCadastro;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.api.pneu.movimentacao.ApiMovimentacaoConverter;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.transport.MetodoIntegrado;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created on 14/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaApiProLog extends Sistema {
    @NotNull
    private final SistemaApiProLogRequester requester;

    public SistemaApiProLog(@NotNull final SistemaApiProLogRequester requester,
                            @NotNull final IntegradorProLog integradorProLog,
                            @NotNull final SistemaKey sistemaKey,
                            @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
        this.requester = requester;
    }

    @Override
    public boolean insert(
            @NotNull final VeiculoCadastro veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        throw new BloqueadoIntegracaoException("Para inserir veículos utilize o seu sistema de gestão");
    }

    @Override
    public boolean update(
            @NotNull final String placaOriginal,
            @NotNull final Veiculo veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        throw new BloqueadoIntegracaoException("Para atualizar os dados do veículo utilize o seu sistema de gestão");
    }

    @Override
    public void updateStatus(
            @NotNull final Long codUnidade,
            @NotNull final String placa,
            @NotNull final Veiculo veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        throw new BloqueadoIntegracaoException("Para atualizar os dados do veículo utilize o seu sistema de gestão");
    }

    @Override
    public boolean delete(
            @NotNull final String placa,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        throw new BloqueadoIntegracaoException("Para deletar o veículo utilize o seu sistema de gestão");
    }

    @NotNull
    @Override
    public Long insert(@NotNull final Pneu pneu, @NotNull final Long codUnidade) throws Throwable {
        throw new BloqueadoIntegracaoException("Para inserir pneus utilize o seu sistema de gestão");
    }

    @NotNull
    @Override
    public List<Long> insert(@NotNull final List<Pneu> pneus) throws Throwable {
        throw new BloqueadoIntegracaoException("Para inserir pneus utilize o seu sistema de gestão");
    }

    @Override
    public void update(@NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final Long codOriginalPneu) throws Throwable {
        throw new BloqueadoIntegracaoException("Para atualizar os dados do pneu utilize o seu sistema de gestão");
    }

    @NotNull
    @Override
    public Long insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                    @NotNull final OffsetDateTime dataHoraSincronizacao,
                                    final boolean isTransferenciaFromVeiculo) throws Throwable {
        throw new BloqueadoIntegracaoException("Para transferir pneus utilize o seu sistema de gestão");
    }

    @NotNull
    @Override
    public Long insertProcessoTransferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener dadosChecklistOfflineChangedListener) throws Throwable {
        throw new BloqueadoIntegracaoException("Para transferir veículos utilize o seu sistema de gestão");
    }

    @NotNull
    @Override
    public VeiculoServico getVeiculoAberturaServico(@NotNull final Long codServico,
                                                    @NotNull final String placaVeiculo) throws Throwable {
        if (getSistemaApiProLog().isServicoMovimentacao(codServico)) {
            throw new BloqueadoIntegracaoException(
                    "O fechamento de serviço de movimentação está sendo integrado e ainda não está disponível.\n" +
                            "Por enquanto, utilize o seu sistema para movimentar os pneus.");
        }
        return getIntegradorProLog().getVeiculoAberturaServico(codServico, placaVeiculo);
    }

    @NotNull
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws Throwable {
        // Neste cenário, a flag deveAbrirServico é setada como false pois não queremos serviços.
        return getIntegradorProLog().insertAfericao(codUnidade, afericao, false);
    }

    @Override
    public void fechaServico(@NotNull final Long codUnidade, @NotNull final Servico servico) throws Throwable {
        if (servico.getTipoServico().equals(TipoServico.MOVIMENTACAO)) {
            throw new BloqueadoIntegracaoException(
                    "O fechamento de serviço de movimentação está sendo integrado e ainda não está disponível.\n" +
                            "Por enquanto, utilize o seu sistema para movimentar os pneus.");
        }
        getIntegradorProLog().fechaServico(codUnidade, servico);
    }

    @NotNull
    @Override
    public Long insert(@NotNull final ServicoDao servicoDao,
                       @NotNull final ProcessoMovimentacao processoMovimentacao,
                       final boolean fecharServicosAutomaticamente) throws Throwable {
        // Garantimos que apenas movimentações válidas foram feitas para essa integração.
        for (final Movimentacao movimentacao : processoMovimentacao.getMovimentacoes()) {
            if (!movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.DESCARTE)
                    && !movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.VEICULO)
                    && !movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.ESTOQUE)
                    && !movimentacao.isFromOrigemToDestino(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.VEICULO)) {
                if (movimentacao.isFrom(OrigemDestinoEnum.ANALISE)) {
                    // Adaptamos o texto de retorno para o cenário onde a origem é Análise.
                    throw new BloqueadoIntegracaoException(
                            String.format(
                                    "ERRO!\nVocê está tentando mover um pneu da %s para o %s.\n" +
                                            "Essa opção de movimentação ainda está sendo integrada",
                                    OrigemDestinoEnum.ANALISE.asString(),
                                    movimentacao.getDestino().getTipo().asString()));
                } else if (movimentacao.isTo(OrigemDestinoEnum.ANALISE)) {
                    // Adaptamos o texto de retorno para o cenário onde o destino é Análise.
                    throw new BloqueadoIntegracaoException(
                            String.format(
                                    "ERRO!\nVocê está tentando mover um pneu do %s para a %s.\n" +
                                            "Essa opção de movimentação ainda está sendo integrada",
                                    movimentacao.getOrigem().getTipo().asString(),
                                    OrigemDestinoEnum.ANALISE.asString()));
                } else {
                    throw new BloqueadoIntegracaoException(
                            "ERRO!\nVocê está tentando realizar uma movimentação que ainda não está integrada");
                }
            }
        }

        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            conn.setAutoCommit(false);
            final Long codMovimentacao =
                    Injection
                            .provideMovimentacaoDao()
                            .insert(conn,
                                    servicoDao,
                                    processoMovimentacao,
                                    dataHoraMovimentacao,
                                    fecharServicosAutomaticamente);
            final long codUnidade = processoMovimentacao.getUnidade().getCodigo();
            requester.insertProcessoMovimentacao(
                    getIntegradorProLog().getUrl(
                            getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(codUnidade),
                            getSistemaKey(),
                            MetodoIntegrado.INSERT_MOVIMENTACAO),
                    getIntegradorProLog().getTokenIntegracaoByCodUnidadeProLog(codUnidade),
                    ApiMovimentacaoConverter.convert(processoMovimentacao, dataHoraMovimentacao));
            conn.commit();
            return codMovimentacao;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @NotNull
    private SistemaApiProLogDao getSistemaApiProLog() {
        return new SistemaApiProLogDaoImpl();
    }
}
