package br.com.zalf.prolog.webservice.commons.report;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

/**
 * Created on 19/07/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class Transposer implements Transposable {

    @NotNull
    protected final ResultSet rSet;

    public Transposer(@NotNull final ResultSet rSet) {
        this.rSet = rSet;
    }
}
