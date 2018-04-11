package br.com.zalf.prolog.webservice.imports.escala_diaria;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class EscalaDiariaDaoImpl implements EscalaDiariaDao {

    public EscalaDiariaDaoImpl() {
    }

    @Override
    public void insertOrUpdateEscalaDiaria(@NotNull final Long codUnidade,
                                           @NotNull final String fileName,
                                           @NotNull final InputStream fileInputStream)
            throws SQLException, IOException, ParseException {

    }

    @Override
    public void insertOrUpdateEscalaDiariaItem(@NotNull final EscalaDiariaItem escalaDiariaItem) throws SQLException {

    }

    @Override
    public List<EscalaDiaria> getEscalasDiarias(@NotNull final Long codUnidade,
                                                @NotNull final Long dataInicial,
                                                @NotNull final Long dataFinal) throws SQLException {
        return null;
    }

    @Override
    public void deleteEscalaDiariaItem(@NotNull final EscalaDiariaItem escalaDiariaItem) throws SQLException {

    }

    @Override
    public void deleteEscalaDiariaItens(@NotNull final List<EscalaDiariaItem> escalaDiariaItens) throws SQLException {

    }
}
