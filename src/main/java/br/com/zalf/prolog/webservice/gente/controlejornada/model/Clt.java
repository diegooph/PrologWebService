package br.com.zalf.prolog.webservice.gente.controlejornada.model;

import br.com.zalf.prolog.webservice.commons.util.datetime.TimeRange;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;

/**
 * Essa classe guarda informações referentes a CLT (Consolidação das Leis do Trabalho) que precisamos utilizar para
 * algum cálculo no sistema.
 * <p>
 * Created on 24/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class Clt {
    @NotNull
    public static final TimeRange RANGE_HORAS_NOTURNAS = TimeRange.of(22, 5);

    @NotNull
    public static final LocalTime INICIO_HORAS_NOTURAS = LocalTime.of(22, 0, 0);
    @NotNull
    public static final LocalTime FIM_HORAS_NOTURAS = LocalTime.of(5, 0, 0);
}