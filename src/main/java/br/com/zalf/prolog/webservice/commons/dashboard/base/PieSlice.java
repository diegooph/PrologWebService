package br.com.zalf.prolog.webservice.commons.dashboard.base;

import br.com.zalf.prolog.webservice.commons.dashboard.Color;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 1/24/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface PieSlice {

    @NotNull
    String getSliceDescription();

    @NotNull
    Color getSliceColor();
}