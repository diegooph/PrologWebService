package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "movimentacao_destino")
public final class MovimentacaoDestinoEntity {
    @Id
    @Column(name = "cod_movimentacao", nullable = false)
    private Long codMovimentacao;
    @Column(name = "cod_veiculo")
    private Long codVeiculo;
    @Column(name = "km_veiculo")
    private Long kmColetadoVeiculo;
    @MapsId
    @OneToOne
    @JoinColumn(name = "cod_movimentacao")
    private MovimentacaoEntity movimentacao;
}
