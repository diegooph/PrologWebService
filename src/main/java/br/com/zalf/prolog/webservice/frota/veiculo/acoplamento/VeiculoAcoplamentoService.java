package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
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
    private final VeiculoAcoplamentoDao dao = new VeiculoAcoplamentoDaoImpl();

    @NotNull
    public Long insertProcessoAcoplamento(@NotNull final Long codColaboradorRealizacao,
                                          @NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        final Connection connection = DatabaseConnection.getConnection();
        try {
            return new VeiculoAcoplamentoRealizacaoEngine(connection, dao, Injection.provideVeiculoDao())
                    .realizaProcessoAcoplamento(codColaboradorRealizacao, processoRealizacao);
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao realizar processo de acoplamento", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao realizar engate/desengate, tente novamente.");
        } finally {
            DatabaseConnection.close(connection);
        }
    }
}
