package br.com.zalf.prolog.webservice.entrega.mapa._model;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-05-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class PlanilhaMapaColunaHolder {
    private static final String EMPTY = Strings.EMPTY;
    @NotNull
    private final String[] colunas;

    @NotNull
    public final String get(final int index) {
        //noinspection ConstantConditions
        return colunas[index] == null ? EMPTY : colunas[index];
    }
}
