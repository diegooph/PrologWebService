package br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.Servico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.VeiculoServico;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia._model.realizacao.PneuTransferenciaRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoCadastro;
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
            @NotNull final VeiculoCadastro veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(veiculo.getCodUnidadeAlocado())) {
            throw new BloqueadoIntegracaoException("Para inserir veículos utilize o seu sistema de gestão");
        }
        return getIntegradorProLog().insert(veiculo, checklistOfflineListener);
    }

    @Override
    public boolean update(
            @NotNull final String placaOriginal,
            @NotNull final Veiculo veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        final Long codUnidadePlaca =
                getIntegradorProLog().getVeiculoByPlaca(placaOriginal, false).getCodUnidadeAlocado();
        if (codUnidadePlaca == null) {
            throw new BloqueadoIntegracaoException(
                    "Não foi possivel encontrar o código da unidade para o veículo informado");
        }
        if (unidadeEstaComIntegracaoAtiva(codUnidadePlaca)) {
            throw new BloqueadoIntegracaoException(
                    "Para atualizar os dados do veículo utilize o seu sistema de gestão");
        }
        return getIntegradorProLog().update(placaOriginal, veiculo, checklistOfflineListener);
    }

    @Override
    public void updateStatus(
            @NotNull final Long codUnidade,
            @NotNull final String placa,
            @NotNull final Veiculo veiculo,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(codUnidade)) {
            throw new BloqueadoIntegracaoException(
                    "Para atualizar os dados do veículo utilize o seu sistema de gestão");
        }
        getIntegradorProLog().updateStatus(codUnidade, placa, veiculo, checklistOfflineListener);
    }

    @Override
    public boolean delete(
            @NotNull final String placa,
            @NotNull final DadosChecklistOfflineChangedListener checklistOfflineListener) throws Throwable {
        final Long codUnidadeVeiculo =
                getIntegradorProLog().getVeiculoByPlaca(placa, false).getCodUnidadeAlocado();
        if (unidadeEstaComIntegracaoAtiva(codUnidadeVeiculo)) {
            throw new BloqueadoIntegracaoException("Para deletar o veículo utilize o seu sistema de gestão");
        }
        return getIntegradorProLog().delete(placa, checklistOfflineListener);
    }

    @NotNull
    @Override
    public Long insert(@NotNull final Pneu pneu, @NotNull final Long codUnidade) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(codUnidade)) {
            throw new BloqueadoIntegracaoException("Para inserir pneus utilize o seu sistema de gestão");
        }
        return getIntegradorProLog().insert(pneu, codUnidade);
    }

    @NotNull
    @Override
    public List<Long> insert(@NotNull final List<Pneu> pneus) {
        // Esse método é usado para importação de planilhas, então não precisa de validação para Unidade nesse momento.
        throw new BloqueadoIntegracaoException("Para inserir pneus utilize o seu sistema de gestão");
    }

    @Override
    public void update(@NotNull final Pneu pneu,
                       @NotNull final Long codUnidade,
                       @NotNull final Long codOriginalPneu) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(codUnidade)) {
            throw new BloqueadoIntegracaoException("Para atualizar os dados do pneu utilize o seu sistema de gestão");
        }
        getIntegradorProLog().update(pneu, codUnidade, codOriginalPneu);
    }

    @NotNull
    @Override
    public Long insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                    @NotNull final OffsetDateTime dataHoraSincronizacao,
                                    final boolean isTransferenciaFromVeiculo) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(pneuTransferenciaRealizacao.getCodUnidadeOrigem())
                && unidadeEstaComIntegracaoAtiva(pneuTransferenciaRealizacao.getCodUnidadeDestino())) {
            throw new BloqueadoIntegracaoException("Para transferir pneus utilize o seu sistema de gestão");
        }
        return getIntegradorProLog()
                .insertTransferencia(pneuTransferenciaRealizacao, dataHoraSincronizacao, isTransferenciaFromVeiculo);
    }

    @NotNull
    @Override
    public Long insertProcessoTransferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo,
            @NotNull final DadosChecklistOfflineChangedListener dadosChecklistOfflineChangedListener) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(processoTransferenciaVeiculo.getCodUnidadeOrigem())
                && unidadeEstaComIntegracaoAtiva(processoTransferenciaVeiculo.getCodUnidadeDestino())) {
            throw new BloqueadoIntegracaoException("Para transferir veículos utilize o seu sistema de gestão");
        }
        return getIntegradorProLog()
                .insertProcessoTransferenciaVeiculo(processoTransferenciaVeiculo, dadosChecklistOfflineChangedListener);
    }

    @NotNull
    @Override
    public VeiculoServico getVeiculoAberturaServico(@NotNull final Long codServico,
                                                    @NotNull final String placaVeiculo) throws Throwable {
        final Long codUnidadeVeiculo =
                getIntegradorProLog().getVeiculoByPlaca(placaVeiculo, false).getCodUnidadeAlocado();
        if (unidadeEstaComIntegracaoAtiva(codUnidadeVeiculo) && getSistemaApiProLog().isServicoMovimentacao(codServico)) {
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
        boolean abrirServico;
        if (unidadeEstaComIntegracaoAtiva(codUnidade)) {
            // Se a unidade possui a integração ativada, precisamos saber se existe algo configurado para abertura
            // de serviços de pneus nesta unidade.
            abrirServico = configUnidadeDeveAbrirServicoPneu(codUnidade);
        } else {
            // Se a unidade não tem integração ativada, então não nos interessa qualquer outra coisa, devemos abrir
            // serviços de pneus.
            abrirServico = true;
        }
        return getIntegradorProLog().insertAfericao(codUnidade, afericao, abrirServico);
    }

    @Override
    public void fechaServico(@NotNull final Long codUnidade,
                             @NotNull final OffsetDateTime dataHorafechamentoServico,
                             @NotNull final Servico servico) throws Throwable {
        if (unidadeEstaComIntegracaoAtiva(codUnidade) && servico.getTipoServico().equals(TipoServico.MOVIMENTACAO)) {
            throw new BloqueadoIntegracaoException(
                    "O fechamento de serviço de movimentação está sendo integrado e ainda não está disponível.\n" +
                            "Por enquanto, utilize o seu sistema para movimentar os pneus.");
        }
        getIntegradorProLog().fechaServico(codUnidade, dataHorafechamentoServico, servico);
    }

    private boolean unidadeEstaComIntegracaoAtiva(@NotNull final Long codUnidade) throws Throwable {
        // Caso o código da unidade está contido na lista de unidades bloqueadas, significa que a unidade
        // NÃO ESTÁ integrada.
        return !getIntegradorProLog().getCodUnidadesIntegracaoBloqueada(getUserToken()).contains(codUnidade);
    }

    private boolean configUnidadeDeveAbrirServicoPneu(@NotNull final Long codUnidade) throws Throwable {
        return getIntegradorProLog().getConfigAberturaServicoPneuIntegracao(codUnidade);
    }

    @NotNull
    private SistemaApiProLogDao getSistemaApiProLog() {
        return new SistemaApiProLogDaoImpl();
    }
}
