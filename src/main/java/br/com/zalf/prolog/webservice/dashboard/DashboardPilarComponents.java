package br.com.zalf.prolog.webservice.dashboard;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2/7/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class DashboardPilarComponents {
    private final int codPilarProLog;
    @NotNull
    private final List<DashboardComponentResumido> componentesResumidosPilar;

    public DashboardPilarComponents(final int codPilarProLog,
                                    @NotNull final List<DashboardComponentResumido> componentesResumidosPilar) {
        this.codPilarProLog = codPilarProLog;
        this.componentesResumidosPilar = componentesResumidosPilar;
    }

    public int getCodPilarProLog() {
        return codPilarProLog;
    }

    @NotNull
    public List<DashboardComponentResumido> getComponentesResumidosPilar() {
        return componentesResumidosPilar;
    }
}