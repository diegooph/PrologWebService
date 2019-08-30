package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

/**
 * Created on 30/08/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuNomenclaturaItemVisualizacao {




    @NotNull
    public static PneuNomenclaturaItem createDummy() {
        return new PneuNomenclaturaItem(
                1L,
                3L,
                5L,
                112L,
                "TDI",
                1L,
                30338922L,
                LocalDateTime.now());
    }
}
