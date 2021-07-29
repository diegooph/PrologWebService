package br.com.zalf.prolog.webservice.v3.fleet.movimentacao.movimentacaoservico._model;

import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico.PneuServicoRealizadoEntity;
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
@IdClass(MovimentacaoPneuServicoRealizadoPk.class)
@Table(schema = "public", name = "movimentacao_pneu_servico_realizado_data")
public class MovimentacaoPneuServicoRealizadoEntity {
    public static final String FONTE_MOVIMENTACAO = "FONTE_MOVIMENTACAO";
    @Id
    @Column(name = "cod_movimentacao", nullable = false)
    private Long codMovimentacao;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao", referencedColumnName = "codigo", nullable = false)
    private MovimentacaoEntity movimentacaoEntity;
    @Id
    @Column(name = "cod_servico_realizado", nullable = false)
    private Long codServicoRealizado;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_servico_realizado", referencedColumnName = "codigo", nullable = false)
    private PneuServicoRealizadoEntity pneuServicoRealizadoEntity;
    @Column(name = "fonte_servico_realizado", columnDefinition = "FONTE_MOVIMENTACAO", nullable = false)
    private String fonteServicoRealizado;
}
