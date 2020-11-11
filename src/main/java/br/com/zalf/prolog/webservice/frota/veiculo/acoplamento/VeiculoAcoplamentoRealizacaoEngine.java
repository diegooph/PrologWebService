package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseUtils;
import br.com.zalf.prolog.webservice.errorhandling.sql.ClientSideErrorException;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoInsert;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created on 2020-11-11
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@AllArgsConstructor
public final class VeiculoAcoplamentoRealizacaoEngine {
    @NotNull
    private final Connection connection;
    @NotNull
    private final VeiculoAcoplamentoDao veiculoAcoplamentoDao;
    @NotNull
    private final VeiculoDao veiculoDao;

    @NotNull
    public Long realizaProcessoAcoplamento(@NotNull final Long codColaboradorRealizacao,
                                           @NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        try {
            connection.setAutoCommit(false);
            atualizaKms(processoRealizacao);
            removeAcoplamentoAtual(processoRealizacao);
            final Long codProcessoInserido = insertProcessoAcoplamento(codColaboradorRealizacao, processoRealizacao);
            insertHistoricoAcoplamentos(codProcessoInserido, processoRealizacao);
            insertEstadoAtualAcoplamentos(codProcessoInserido, processoRealizacao);
            connection.commit();
            return codProcessoInserido;
        } catch (final SQLException exception) {
            DatabaseUtils.safeRollback(connection);
            throw new IllegalStateException("Erro ao realizar processo de acoplamento.", exception);
        }
    }

    private void atualizaKms(@NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        processoRealizacao
                .getAcoesRealizadas()
                .stream()
                .filter(acaoRealizada -> acaoRealizada.getKmColetado() != null)
                .forEach(acaoRealizada -> veiculoDao.updateKmByCodVeiculo(
                        connection,
                        acaoRealizada.getCodVeiculo(),
                        acaoRealizada.getKmColetado()));
    }

    private void removeAcoplamentoAtual(@NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        processoRealizacao
                .estaEditandoProcessoAcoplamento()
                .ifPresent(codProcessoEditado -> veiculoAcoplamentoDao.removeAcoplamentoAtual(
                        connection,
                        codProcessoEditado));
    }

    @NotNull
    private Long insertProcessoAcoplamento(@NotNull final Long codColaboradorRealizacao,
                                           @NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        return veiculoAcoplamentoDao.insertProcessoAcoplamento(
                connection,
                VeiculoAcoplamentoProcessoInsert.of(
                        processoRealizacao.getCodUnidade(),
                        codColaboradorRealizacao,
                        Now.offsetDateTimeUtc(),
                        processoRealizacao.getObservacao()));
    }

    private void insertHistoricoAcoplamentos(@NotNull final Long codProcessoInserido,
                                             @NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        veiculoAcoplamentoDao.insertHistoricoAcoesRealizadas(
                connection,
                codProcessoInserido,
                processoRealizacao.getAcoesRealizadas());
    }

    private void insertEstadoAtualAcoplamentos(@NotNull final Long codProcessoInserido,
                                               @NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        processoRealizacao
                .getVeiculosAcopladosOuMantidos(codProcessoInserido)
                .ifPresent(veiculosAcopladosMantidos -> {
                    if (veiculosAcopladosMantidos.size() == 1) {
                        throw new ClientSideErrorException(
                                "Não é possível salvar uma composição de apenas um veículo.");
                    }

                    veiculoAcoplamentoDao.insertEstadoAtualAcoplamentos(
                            connection,
                            veiculosAcopladosMantidos);
                });
    }
}
