package br.com.zalf.prolog.webservice.commons.dashboard;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TableColumns {

    @NotNull
    private List<String> values;

    public TableColumns(@NotNull List<String> values) {
        this.values = values;
    }

    @NotNull
    public List<String> getValues() {
        return values;
    }

    public void setValues(@NotNull List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "TableColumns{" +
                "values=" + values +
                '}';
    }
}
