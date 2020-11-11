package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.DatabaseUtils;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamento;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoAcoplamentoService {
    private static final String TAG = VeiculoAcoplamentoService.class.getSimpleName();

    @NotNull
    public Long realizaProcessoAcoplamento(@NotNull final Long codColaborador,
                                           @NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        final VeiculoAcoplamentoDao dao = new VeiculoAcoplamentoDaoImpl();
        // 0 - Validações?

        final Connection connection = DatabaseConnection.getConnection();
        try {
            connection.setAutoCommit(false);

            // 1 - Atualiza KMs - Deve acontecer antes de remover o acoplamento atual, pois se baseará nele para a
            // propagação dos KMs.
            final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
            processoRealizacao
                    .getAcoplamentos()
                    .stream()
                    .filter(VeiculoAcoplamento::coletouKm)
                    .forEach(veiculoAcoplamento -> {
                        // TODO: Utilizar VeiculoService para atualizar os KMs.
                    });

            // 2 - Remove os veículos do processo editado atual.
            processoRealizacao
                    .estaEditandoProcessoAcoplamento()
                    .ifPresent(codProcessoEditado -> dao.removeAcoplamentoAtual(connection, codProcessoEditado));

            // 3 - Inserir processo acoplamento.
            final Long codProcessoInserido = dao.insertProcessoAcoplamento(
                    connection,
                    processoRealizacao.getCodUnidade(),
                    codColaborador,
                    Now.offsetDateTimeUtc(),
                    processoRealizacao.getObservacao());

            // 4 - Inserir histórico acoplamentos.
            dao.insertHistoricoAcoplamentos(connection, codProcessoInserido, processoRealizacao.getAcoplamentos());

            // 5 - Inserir processo atual.
            dao.insertEstadoAtualAcoplamentos(
                    connection,
                    codProcessoInserido,
                    processoRealizacao.getCodUnidade(),
                    processoRealizacao.getAcoplamentos());

            // 6 - Commita e seja feliz.
            connection.commit();

            return codProcessoInserido;
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao realizar processo de acoplamento", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao realizar engate/desengate, tente novamente.");
        } finally {
            DatabaseUtils.safeRollback(connection);
            DatabaseConnection.close(connection);
        }
    }
}
