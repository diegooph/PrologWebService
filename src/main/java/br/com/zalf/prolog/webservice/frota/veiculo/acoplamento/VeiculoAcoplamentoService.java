package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.transaction.DatabaseTransaction;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoAcoplamentoValidator;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-11-03
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoAcoplamentoService {
    private static final String TAG = VeiculoAcoplamentoService.class.getSimpleName();

    @NotNull
    public Long insertProcessoAcoplamento(@NotNull final Long codColaboradorRealizacao,
                                          @NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        try {
            return DatabaseTransaction.builder()
                    .withConnection(DatabaseConnection.getConnection())
                    .withCloseConnectionOnFinish(true)
                    .build()
                    .runInTransaction(connection -> {
                        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
                        final VeiculoAcoplamentoDao acoplamentoDao = Injection.provideVeiculoAcoplamentoDao(connection);
                        new VeiculoAcoplamentoValidator(connection,
                                                        acoplamentoDao,
                                                        veiculoDao).veiculoAcoplamentoValidator(processoRealizacao);
                        return new VeiculoAcoplamentoRealizacaoEngine(connection, acoplamentoDao, veiculoDao)
                                .realizaProcessoAcoplamento(codColaboradorRealizacao, processoRealizacao);
                    });
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao realizar processo de acoplamento", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao realizar engate/desengate, tente novamente.");
        }
    }
}
