package br.com.zalf.prolog.webservice.dashboard.components.table;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 26/10/2020
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Data
public final class TableFooter {
    @Nullable
    private final TableItemFooter itensFooter;

    @Override
    public String toString() {
        return "TableFooter{" +
                "itensFooter=" + itensFooter +
                '}';
    }
}