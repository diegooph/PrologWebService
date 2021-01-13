package br.com.zalf.prolog.webservice.frota.veiculo.model;

/**
 * Created by jean on 25/05/16.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TipoVeiculo {
    private Long codEmpresa;
    private Long codDiagrama;
    private Long codigo;
    private String nome;
    private String codAuxiliar;
    private Boolean motorizado;
}