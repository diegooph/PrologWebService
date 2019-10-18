package br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.Movimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.servico.ServicoDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.TipoServico;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.VeiculoServico;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.model.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Created on 14/09/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class SistemaApiProLog extends Sistema {
    public SistemaApiProLog(@NotNull final IntegradorProLog integradorProLog,
                            @NotNull final SistemaKey sistemaKey,
                            @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
    }

    @Override
    public boolean insert(
            @NotNull final Long codUnidade,
            @NotNull final Veiculo veiculo,
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
        // Neste cenário, a flag deveAbrirServico é setada como false pois não queremos serviços
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
        for (final Movimentacao movimentacao : processoMovimentacao.getMovimentacoes()) {
            if (!movimentacao.getOrigem().getTipo().equals(OrigemDestinoEnum.ESTOQUE)
                    || !movimentacao.getDestino().getTipo().equals(OrigemDestinoEnum.DESCARTE)) {
                throw new BloqueadoIntegracaoException("É permitido apenas movimentações do ESTOQUE para o DESCARTE.\n" +
                        "As demais movimentações ainda estão sendo integradas.");
            }
        }
        // Apenas processamos movimentações com origem ESTOQUE e destino DESCARTE.
        return getIntegradorProLog().insert(servicoDao, processoMovimentacao, fecharServicosAutomaticamente);
    }

    @NotNull
    private SistemaApiProLogDao getSistemaApiProLog() {
        return new SistemaApiProLogDaoImpl();
    }
}
