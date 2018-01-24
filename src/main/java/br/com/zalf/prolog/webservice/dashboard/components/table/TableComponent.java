package br.com.zalf.prolog.webservice.dashboard.components.table;

import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponent;
import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponentBuilder;
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

    public TableComponent(@NotNull String titulo, @Nullable String subtitulo, @NotNull String descricao, @NotNull String urlEndpointDados, @NotNull Integer codTipoComponente, int qtdBlocosHorizontais, int qtdBlocosVerticais, int ordem, @NotNull TableHeader tableHeader, @NotNull List<TableLine> tableData) {
        super(titulo, subtitulo, descricao, urlEndpointDados, codTipoComponente, qtdBlocosHorizontais, qtdBlocosVerticais, ordem);
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

    public static class Builder extends BaseComponentBuilder {
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

        @Override
        public DashboardComponentBuilder withUrlEndpointDados(@NotNull String urlEndpointDados) {
            return null;
        }

        @Override
        public DashboardComponentBuilder withCodTipoComponente(@NotNull Integer codTipoComponente) {
            return null;
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
            return null;
        }
    }
}
