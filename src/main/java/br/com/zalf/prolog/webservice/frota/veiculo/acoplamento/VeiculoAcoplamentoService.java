package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.database.transaction.DatabaseTransaction;
import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.HolderAcomplamentoValidacao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoAcoplamentoDiffChecker;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator.VeiculoAcoplamentoValidator;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

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
                    .runInTransaction(connection -> insertProcessoAcoplamentoInTransaction(codColaboradorRealizacao,
                                                                                           processoRealizacao,
                                                                                           connection));
        } catch (final Throwable throwable) {
            Log.e(TAG, "Erro ao realizar processo de acoplamento", throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao realizar engate/desengate, tente novamente.");
        }
    }

    @NotNull
    private Long insertProcessoAcoplamentoInTransaction(
            @NotNull final Long codColaboradorRealizacao,
            @NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao,
            @NotNull final Connection connection) {
        final VeiculoDao veiculoDao = Injection.provideVeiculoDao();
        final VeiculoAcoplamentoDao acoplamentoDao = Injection.provideVeiculoAcoplamentoDao(connection);

        final List<Long> codVeiculosProcesso = processoRealizacao.getCodVeiculosProcesso();
        final HolderAcomplamentoValidacao dadosBanco =
                acoplamentoDao.getHolderAcomplamentoValidacao(codVeiculosProcesso);
        final VeiculoAcoplamentoValidator validator = new VeiculoAcoplamentoValidator(dadosBanco, processoRealizacao);
        final VeiculoAcoplamentoDiffChecker diffChecker = new VeiculoAcoplamentoDiffChecker(processoRealizacao);

        validator.validate();
        if (diffChecker.nadaFoiAlterado()) {
            return processoRealizacao.getCodProcessoAcoplamentoEditado().get();
        }

        return new VeiculoAcoplamentoRealizacaoEngine(connection, acoplamentoDao, veiculoDao)
                .realizaProcessoAcoplamento(codColaboradorRealizacao, processoRealizacao);
    }
}
