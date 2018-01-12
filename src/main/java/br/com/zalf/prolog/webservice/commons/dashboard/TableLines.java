package br.com.zalf.prolog.webservice.commons.dashboard;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TableLines {

    @NotNull
    private List<TableColumns> tableColumns;

    public TableLines(@NotNull List<TableColumns> tableColumns) {
        this.tableColumns = tableColumns;
    }

    @NotNull
    public List<TableColumns> getTableColumns() {
        return tableColumns;
    }

    public void setTableColumns(@NotNull List<TableColumns> tableColumns) {
        this.tableColumns = tableColumns;
    }

    @Override
    public String toString() {
        return "TableLines{" +
                "tableColumns=" + tableColumns +
                '}';
    }
}
