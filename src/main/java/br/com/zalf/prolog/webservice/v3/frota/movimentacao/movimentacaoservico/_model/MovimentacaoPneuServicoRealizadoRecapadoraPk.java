package br.com.zalf.prolog.webservice.v3.frota.movimentacao.movimentacaoservico._model;

import lombok.*;

import java.io.Serializable;

/**
 * Created on 2021-06-28
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MovimentacaoPneuServicoRealizadoRecapadoraPk implements Serializable {
    private Long codMovimentacao;
    private Long codServicoRealizadoMovimentacao;
    private Long codRecapadora;
}
