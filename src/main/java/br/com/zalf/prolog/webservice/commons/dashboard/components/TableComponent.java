package br.com.zalf.prolog.webservice.commons.dashboard.components;

import br.com.zalf.prolog.webservice.commons.dashboard.TableHeader;
import br.com.zalf.prolog.webservice.commons.dashboard.TableLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TableComponent extends DashboardComponent {

    @NotNull
    private TableHeader tableHeader;
    @NotNull
    private List<TableLine> tableData;

    public TableComponent(@NotNull String titulo,
                          @Nullable String subtitulo,
                          @NotNull String descricao,
                          @NotNull TableHeader tableHeader,
                          @NotNull List<TableLine> tableData) {
        super(titulo, subtitulo, descricao);
        this.tableHeader = tableHeader;
        this.tableData = tableData;
    }

    @NotNull
    public TableHeader getTableHeader() {
        return tableHeader;
    }

    public void setTableHeader(@NotNull TableHeader tableHeader) {
        this.tableHeader = tableHeader;
    }

    @NotNull
    public List<TableLine> getTableData() {
        return tableData;
    }

    public void setTableData(@NotNull List<TableLine> tableData) {
        this.tableData = tableData;
    }

    @Override
    public String toString() {
        return "TableComponent{" +
                "tableHeader=" + tableHeader +
                ", tableData=" + tableData +
                '}';
    }
}
