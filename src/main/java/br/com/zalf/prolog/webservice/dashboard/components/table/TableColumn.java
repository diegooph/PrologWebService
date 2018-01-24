package br.com.zalf.prolog.webservice.dashboard.components.table;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TableColumn {

    @NotNull
    private String columnValue;

    public TableColumn(@NotNull String columnValue) {
        this.columnValue = columnValue;
    }

    @NotNull
    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(@NotNull String columnValue) {
        this.columnValue = columnValue;
    }

    @Override
    public String toString() {
        return "TableColumn{" +
                "columnValue=" + columnValue +
                '}';
    }
}
