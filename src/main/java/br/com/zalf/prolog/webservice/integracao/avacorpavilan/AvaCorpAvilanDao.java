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
    /**
     * Método utilizado para buscar um csv com as Ordens de Serviços que não sincronizaram por algum motivo e estão como
     * pendentes.
     * Utilizamos os filtros de datas para buscar Ordens de Serviços abertas ou fechadas neste período.
     *
     * @param outputStream Arquivo onde os dados serão escritos para retornar.
     * @param dataInicial  Data inicial do filtro das Ordens de Serviços pendentes.
     * @param dataFinal    Data final do filtro das Ordens de Serviços pendentes.
     */
    void getOrdensServicosPendentesSincroniaCsv(@NotNull final OutputStream outputStream,
                                                @Nullable final LocalDate dataInicial,
                                                @Nullable final LocalDate dataFinal) throws Throwable;
}
