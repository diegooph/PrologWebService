package br.com.zalf.prolog.webservice.entrega.escaladiaria;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface EscalaDiariaDao {

    void insertOrUpdateEscalaDiaria(@NotNull final String token,
                                    @NotNull final Long codUnidade,
                                    @NotNull final List<EscalaDiariaItem> escalaDiariaItens) throws SQLException;

    void insertEscalaDiariaItem(@NotNull final String token,
                                @NotNull final Long codUnidade,
                                @NotNull final EscalaDiariaItem escalaDiariaItem) throws SQLException;

    void updateEscalaDiariaItem(@NotNull final String token,
                                @NotNull final Long codUnidade,
                                @NotNull final EscalaDiariaItem escalaDiariaItem) throws SQLException;

    List<EscalaDiaria> getEscalasDiarias(@NotNull final Long codUnidade,
                                         @NotNull final LocalDate dataInicial,
                                         @NotNull final LocalDate dataFinal) throws SQLException;

    EscalaDiariaItem getEscalaDiariaItem(@NotNull final Long codUnidade,
                                         @NotNull final Long codEscala) throws SQLException;
    
    void deleteEscalaDiariaItens(@NotNull final Long codUnidade,
                                 @NotNull final List<Long> codEscalas) throws SQLException;
}
