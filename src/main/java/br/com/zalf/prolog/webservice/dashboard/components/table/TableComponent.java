package br.com.zalf.prolog.webservice.dashboard.components.table;

import br.com.zalf.prolog.webservice.dashboard.base.BaseComponentBuilder;
import br.com.zalf.prolog.webservice.dashboard.base.DashboardComponent;
import com.google.common.base.Preconditions;
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
    private TableData tableData;

    private TableComponent(@NotNull String titulo,
                           @Nullable String subtitulo,
                           @NotNull String descricao,
                           @NotNull String urlEndpointDados,
                           @NotNull Integer codTipoComponente,
                           int qtdBlocosHorizontais,
                           int qtdBlocosVerticais,
                           int ordemExibicao,
                           @NotNull TableHeader tableHeader,
                           @NotNull TableData tableData) {
        super(titulo, subtitulo, descricao, urlEndpointDados, codTipoComponente, qtdBlocosHorizontais, qtdBlocosVerticais, ordemExibicao);
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
    public TableData getTableData() {
        return tableData;
    }

    public void setTableData(@NotNull TableData tableData) {
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
        private TableHeader tableHeader;
        private TableData tableData;

        public Builder() {

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

        @Override
        public TableComponent build() {
            ensureNotNullValues();
            Preconditions.checkNotNull(tableHeader, "tableHeader deve ser instanciada com 'withTableHeader'");
            Preconditions.checkNotNull(tableData, "tableData deve ser instanciada com 'withTableData'");
            return new TableComponent(
                    titulo,
                    subtitulo,
                    descricao,
                    urlEndpointDados,
                    codTipoComponente,
                    qtdBlocosHorizontais,
                    qtdBlocosVerticais,
                    ordemExibicao,
                    tableHeader,
                    tableData);
        }
    }
}