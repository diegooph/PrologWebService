package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class MotivoTrocaInsercao {

    @NotNull
    private final Long codEmpresaMotivoTroca;

    @NotNull
    private final Long codMotivoTroca;

    @NotNull
    private final String descricaoMotivoTroca;

    @NotNull
    private final Boolean ativoMotivoTroca;

    @NotNull
    private final LocalDateTime dataHoraInsercaoMotivoTroca;

}
