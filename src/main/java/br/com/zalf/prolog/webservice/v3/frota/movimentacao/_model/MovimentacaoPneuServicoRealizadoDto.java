package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Created on 2021-05-01
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class MovimentacaoPneuServicoRealizadoDto {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nomeServico;
    @NotNull
    private final Boolean incrementaVida;
    @NotNull
    private final BigDecimal custoServico;
    @NotNull
    private final Integer vidaPneuMomentoServico;
}
