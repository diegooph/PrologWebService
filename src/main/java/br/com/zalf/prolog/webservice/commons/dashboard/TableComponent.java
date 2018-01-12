package br.com.zalf.prolog.webservice.commons.dashboard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TableComponent extends DashboardComponent {

    @NotNull
    private TableHeader tableHeader;
    @NotNull
    private TableLines tableData;

    public TableComponent(@NotNull String titulo,
                          @Nullable String subtitulo,
                          @NotNull String descricao,
                          @NotNull TableHeader tableHeader,
                          @NotNull TableLines tableData) {
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
    public TableLines getTableData() {
        return tableData;
    }

    public void setTableData(@NotNull TableLines tableData) {
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
