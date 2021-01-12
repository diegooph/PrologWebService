package br.com.zalf.prolog.webservice.gente.treinamento.relatorios;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.PrologDateParser;
import org.jetbrains.annotations.NotNull;

import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created on 14/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TreinamentoRelatorioService {

    private static final String TAG = TreinamentoRelatorioService.class.getSimpleName();
    @NotNull
    private final TreinamentoRelatorioDao treinamentoRelatorioDao = Injection.provideTreinamentoRelatorioDao();

    @NotNull
    public StreamingOutput getRelatorioEstratificadoPorColaboradorCsv(@NotNull final Long codUnidade,
                                                                      @NotNull final String dataInicial,
                                                                      @NotNull final String dataFinal) {
        return outputStream -> {
            try {
                treinamentoRelatorioDao
                        .getRelatorioEstratificadoPorColaboradorCsv(
                                outputStream,
                                codUnidade,
                                PrologDateParser.toLocalDate(dataInicial),
                                PrologDateParser.toLocalDate(dataFinal));
            } catch (final SQLException | IOException e) {
                Log.e(TAG, String.format("Erro ao buscar o relatório com a estratificação de visualização do treinamento (CSV). \n" +
                                "codUnidade: %d \n" +
                                "dataInicial: %s \n" +
                                "dataFinal: %s", codUnidade, dataInicial, dataFinal), e);
            }
        };
    }
}
