package br.com.zalf.prolog.webservice.gente.treinamento.relatorios;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Created on 14/03/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface TreinamentoRelatorioDao {

    void getRelatorioEstratificadoPorColaboradorCsv(@NotNull final OutputStream outputStream,
                                                    @NotNull final Long codUnidade,
                                                    @NotNull final LocalDate dataInicial,
                                                    @NotNull final LocalDate dataFinal) throws SQLException, IOException;
}
