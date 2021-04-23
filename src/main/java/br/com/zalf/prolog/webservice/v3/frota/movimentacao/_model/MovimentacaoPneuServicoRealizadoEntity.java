package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created on 2021-04-23
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@IdClass(MovimentacaoPneusServicoRealizadoPk.class)
@Table(schema = "public", name = "movimentacao_pneu_servico_realizado")
public final class MovimentacaoPneuServicoRealizadoEntity {
    @Id
    @Column(name = "cod_movimentacao", nullable = false)
    private Long codMovimentacao;
    @Id
    @Column(name = "cod_servico_realizado", nullable = false)
    private Long codServicoRealizado;
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao")
    private MovimentacaoEntity movimentacao;
}
