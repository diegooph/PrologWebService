package br.com.zalf.prolog.webservice.entrega.mapa._model;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-05-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class PlanilhaMapaColunaHolder {
    @NotNull
    private final String[] colunas;

    @NotNull
    public final String get(final int index) {
        return StringUtils.trimToEmpty(colunas[index]);
    }
}
