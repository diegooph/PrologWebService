package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Created on 2020-03-17
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class MotivoInsercao {

    @NotNull(message = "O código da empresa é obrigatório.")
    private final long codEmpresaMotivo;

    @NotBlank(message = "A descrição do motivo não pode sem branca.")
    private final String descricaoMotivo;
    
}
