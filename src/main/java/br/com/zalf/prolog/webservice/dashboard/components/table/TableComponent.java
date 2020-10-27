package br.com.zalf.prolog.webservice.dashboard.components.table;

import br.com.zalf.prolog.webservice.dashboard.ComponentDataHolder;
import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponent;
import br.com.zalf.prolog.webservice.dashboard.base.IdentificadorTipoComponente;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TableComponent extends DashboardComponent {
    @NotNull
    private TableHeader tableHeader;
    @NotNull
    private TableData tableData;
    @Nullable
    private TableFooter tableFooter;

    public static TableComponent createDefault(@NotNull final ComponentDataHolder component,
                                               @NotNull final TableHeader tableHeader,
                                               @NotNull final TableData tableData,
                                               @Nullable final TableFooter tableFooter) {
        return new TableComponent.Builder()
                .withCodigo(component.codigoComponente)
                .withTitulo(component.tituloComponente)
                .withSubtitulo(component.subtituloComponente)
                .withDescricao(component.descricaoComponente)
                .withCodTipoComponente(component.codigoTipoComponente)
                .withUrlEndpointDados(component.urlEndpointDados)
                .withQtdBlocosHorizontais(component.qtdBlocosHorizontais)
                .withQtdBlocosVerticais(component.qtdBlocosVerticais)
                .withOrdemExibicao(component.ordemExibicao)
                .withTableHeader(tableHeader)
                .withTableData(tableData)
                .withTableFooter(tableFooter)
                .build();
    }

    private TableComponent(@NotNull Integer codigo,
                           @NotNull String titulo,
                           @Nullable String subtitulo,
                           @NotNull String descricao,
                           @NotNull String urlEndpointDados,
                           @NotNull Integer codTipoComponente,
                           int qtdBlocosHorizontais,
                           int qtdBlocosVerticais,
                           int ordemExibicao,
                           @NotNull TableHeader tableHeader,
                           @NotNull TableData tableData,
                           @Nullable TableFooter tableFooter) {
        super(codigo, IdentificadorTipoComponente.TABELA, titulo, subtitulo, descricao, urlEndpointDados,
                codTipoComponente, qtdBlocosHorizontais, qtdBlocosVerticais, ordemExibicao);
        this.tableHeader = tableHeader;
        this.tableData = tableData;
        this.tableFooter = tableFooter;
    }

    @NotNull
    public TableHeader getTableHeader() {
        return tableHeader;
    }

    public void setTableHeader(@NotNull TableHeader tableHeader) {
        this.tableHeader = tableHeader;
    }

    @NotNull
    public TableData getTableData() {
        return tableData;
    }

    public void setTableData(@NotNull TableData tableData) {
        this.tableData = tableData;
    }

    @Nullable
    public TableFooter getTableFooter() {
        return tableFooter;
    }

    public void setTableFooter(final @Nullable TableFooter tableFooter) {
        this.tableFooter = tableFooter;
    }

    @Override
    public String toString() {
        return "TableComponent{" +
                "tableHeader=" + tableHeader +
                ", tableData=" + tableData +
                ", tableFooter=" + tableFooter +
                '}';
    }

    public static List<TableLine> createLinesFromMap(@NotNull final Map<String, Integer> map) {
        final List<TableLine> lines = new ArrayList<>(map.size());
        map.forEach((string, integer) -> {
            // Colunas.
            final List<TableColumn> columns = new ArrayList<>(2);
            columns.add(new TableColumn(string));
            columns.add(new TableColumn(String.valueOf(integer)));
            lines.add(new TableLine(columns));
        });
        return lines;
    }

    public static class Builder extends BaseComponentBuilder {
        private TableHeader tableHeader;
        private TableData tableData;
        private TableFooter tableFooter;

        public Builder() {
        }

        @Override
        public Builder withCodigo(@NotNull Integer codigo) {
            super.withCodigo(codigo);
            return this;
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
        public Builder withUrlEndpointDados(@NotNull String urlEndpointDados) {
            super.withUrlEndpointDados(urlEndpointDados);
            return this;
        }

        @Override
        public Builder withCodTipoComponente(@NotNull Integer codTipoComponente) {
            super.withCodTipoComponente(codTipoComponente);
            return this;
        }

        @Override
        public Builder withQtdBlocosHorizontais(int qtdBlocosHorizontais) {
            super.withQtdBlocosHorizontais(qtdBlocosHorizontais);
            return this;
        }

        @Override
        public Builder withQtdBlocosVerticais(int qtdBlocosVerticais) {
            super.withQtdBlocosVerticais(qtdBlocosVerticais);
            return this;
        }

        @Override
        public Builder withOrdemExibicao(int ordemExibicao) {
            super.withOrdemExibicao(ordemExibicao);
            return this;
        }

        public Builder withTableHeader(@NotNull TableHeader tableHeader) {
            this.tableHeader = tableHeader;
            return this;
        }

        public Builder withTableData(@NotNull TableData tableData) {
            this.tableData = tableData;
            return this;
        }

        public Builder withTableFooter(@Nullable TableFooter tableFooter) {
            this.tableFooter = tableFooter;
            return this;
        }

        @Override
        public TableComponent build() {
            ensureNotNullValues();
            Preconditions.checkNotNull(tableHeader, "tableHeader deve ser instanciada com 'withTableHeader'");
            Preconditions.checkNotNull(tableData, "tableData deve ser instanciada com 'withTableData'");
            return new TableComponent(
                    codigo,
                    titulo,
                    subtitulo,
                    descricao,
                    urlEndpointDados,
                    codTipoComponente,
                    qtdBlocosHorizontais,
                    qtdBlocosVerticais,
                    ordemExibicao,
                    tableHeader,
                    tableData,
                    tableFooter);
        }
    }
}