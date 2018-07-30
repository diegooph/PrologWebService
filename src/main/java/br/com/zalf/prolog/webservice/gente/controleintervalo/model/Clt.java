package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.commons.util.date.TimeRange;
import org.jetbrains.annotations.NotNull;

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
}