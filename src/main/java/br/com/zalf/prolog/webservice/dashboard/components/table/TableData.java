package br.com.zalf.prolog.webservice.dashboard.components.table;

import br.com.zalf.prolog.webservice.dashboard.base.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 1/26/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class TableData extends Data {

    @NotNull
    private List<TableLine> tableLines;

    public TableData(@NotNull List<TableLine> tableLines) {
        this.tableLines = tableLines;
    }

    @NotNull
    public List<TableLine> getTableLines() {
        return tableLines;
    }

    public void setTableLines(@NotNull List<TableLine> tableLines) {
        this.tableLines = tableLines;
    }

    @Override
    public String toString() {
        return "TableData{" +
                "tableLines=" + tableLines +
                '}';
    }
}