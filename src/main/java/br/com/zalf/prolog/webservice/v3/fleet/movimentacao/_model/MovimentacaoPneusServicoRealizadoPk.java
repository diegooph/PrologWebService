package br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Created on 2021-04-23
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public final class MovimentacaoPneusServicoRealizadoPk implements Serializable {
    @NotNull
    private Long codMovimentacao;
    @NotNull
    private Long codServicoRealizado;
}
