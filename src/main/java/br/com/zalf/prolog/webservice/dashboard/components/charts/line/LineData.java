package br.com.zalf.prolog.webservice.dashboard.components.charts.line;

import br.com.zalf.prolog.webservice.dashboard.base.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 18/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class LineData extends Data {

    @NotNull
    private final List<LineGroup> lineGroups;

    public LineData(@NotNull final List<LineGroup> lineGroups) {
        this.lineGroups = lineGroups;
    }

    @NotNull
    public List<LineGroup> getLineGroups() {
        return lineGroups;
    }
}