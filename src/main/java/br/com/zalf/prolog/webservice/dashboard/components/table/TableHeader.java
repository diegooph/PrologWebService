package br.com.zalf.prolog.webservice.dashboard.components.table;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TableHeader {

    @NotNull
    private List<TableItemHeader> itensHeader;

    public TableHeader(@NotNull List<TableItemHeader> itensHeader) {
        this.itensHeader = itensHeader;
    }

    @NotNull
    public List<TableItemHeader> getItensHeader() {
        return itensHeader;
    }

    public void setItensHeader(@NotNull List<TableItemHeader> itensHeader) {
        this.itensHeader = itensHeader;
    }

    @Override
    public String toString() {
        return "TableHeader{" +
                "itensHeader=" + itensHeader +
                '}';
    }
}
