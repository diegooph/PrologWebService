package br.com.zalf.prolog.webservice.integracao.api;

import br.com.zalf.prolog.webservice.errorhandling.exception.BloqueadoIntegracaoException;
import br.com.zalf.prolog.webservice.frota.checklist.offline.DadosChecklistOfflineChangedListener;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

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
}
