package br.com.zalf.prolog.webservice.commons.dashboard.components.table;

import br.com.zalf.prolog.webservice.commons.dashboard.base.DashboardComponent;
import br.com.zalf.prolog.webservice.commons.dashboard.base.DashboardComponentBuilder;
import com.google.common.base.Preconditions;
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

    TableComponent(@NotNull String titulo,
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

    public static class Builder implements DashboardComponentBuilder {
        private String titulo;
        private String subtitulo;
        private String descricao;
        private TableHeader tableHeader;
        private List<TableLine> tableData;

        public Builder() {}

        public Builder(@NotNull String titulo, @NotNull String descricao) {
            this.titulo = titulo;
            this.descricao = descricao;
        }

        @Override
        public Builder withTitulo(@NotNull String titulo) {
            this.titulo = titulo;
            return this;
        }

        @Override
        public Builder withSubtitulo(@Nullable String subtitulo) {
            this.subtitulo = subtitulo;
            return this;
        }

        @Override
        public Builder withDescricao(@NotNull String descricao) {
            this.descricao = descricao;
            return this;
        }

        public Builder withTableHeader(@NotNull TableHeader tableHeader) {
            this.tableHeader = tableHeader;
            return this;
        }

        public Builder withTableData(@NotNull List<TableLine> tableData) {
            this.tableData = tableData;
            return this;
        }

        @Override
        public DashboardComponent build() {
            Preconditions.checkNotNull(tableHeader, "tableHeader deve ser instanciada com 'withTableHeader'");
            Preconditions.checkNotNull(tableData, "tableData deve ser instanciada com 'withTableData'");
            return new TableComponent(titulo, subtitulo, descricao, tableHeader, tableData);
        }
    }
}
