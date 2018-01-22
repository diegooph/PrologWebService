package br.com.zalf.prolog.webservice.commons.dashboard.components.table;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TableLine {

    @NotNull
    private List<TableColumn> tableColumns;

    public TableLine(@NotNull List<TableColumn> tableColumns) {
        this.tableColumns = tableColumns;
    }

    @NotNull
    public List<TableColumn> getTableColumns() {
        return tableColumns;
    }

    public void setTableColumns(@NotNull List<TableColumn> tableColumns) {
        this.tableColumns = tableColumns;
    }

    @Override
    public String toString() {
        return "TableLine{" +
                "tableColumns=" + tableColumns +
                '}';
    }
}
