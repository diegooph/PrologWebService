package br.com.zalf.prolog.webservice.v3.frota.movimentacao.movimentacaoservico._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created on 2021-06-24
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@IdClass(MovimentacaoPneuServicoRealizadoRecapadoraPk.class)
@Table(schema = "public", name = "movimentacao_pneu_servico_realizado_recapadora_data")
public class MovimentacaoPneuServicoRealizadoRecapadoraEntity {
    @Id
    @Column(name = "cod_movimentacao", nullable = false)
    private Long codMovimentacao;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao", referencedColumnName = "cod_movimentacao", nullable = false)
    private MovimentacaoPneuServicoRealizadoEntity movimentacaoPneuServicoRealizadoEntity;
    @Id
    @Column(name = "cod_servico_realizado_movimentacao", nullable = false)
    private Long codServicoRealizadoMovimentacao;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_servico_realizado_movimentacao", referencedColumnName = "cod_servico_realizado", nullable = false)
    private MovimentacaoPneuServicoRealizadoEntity movimentacaoPneuServicoRealizado;
    @Id
    @Column(name = "cod_recapadora", nullable = false)
    private Long codRecapadora;
}
