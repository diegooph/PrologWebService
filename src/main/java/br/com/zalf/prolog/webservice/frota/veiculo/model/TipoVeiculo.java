package br.com.zalf.prolog.webservice.frota.veiculo.model;

/**
 * Created by jean on 25/05/16.
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class TipoVeiculo {
    private Long codEmpresa;
    private Long codDiagrama;
    private Long codigo;
    private String nome;
    private String codAuxiliar;
    private boolean motorizado;

    public TipoVeiculo(final Long codEmpresa,
                       final Long codDiagrama,
                       final Long codigo,
                       final String nome,
                       final String codAuxiliar) {
        this.codEmpresa = codEmpresa;
        this.codDiagrama = codDiagrama;
        this.codigo = codigo;
        this.nome = nome;
        this.codAuxiliar = codAuxiliar;
    }
}