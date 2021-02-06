package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.time.LocalDate;

/**
 * Created on 2020-09-03
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface AvaCorpAvilanDao {

    void getOrdensServicosPendentesSincroniaCsv(@NotNull final OutputStream outputStream,
                                                @Nullable final LocalDate dataInicial,
                                                @Nullable final LocalDate dataFinal) throws Throwable;
}
