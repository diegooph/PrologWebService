package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
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
@Table(schema = "public", name = "movimentacao_origem")
public final class MovimentacaoOrigemEntity {
    @Id
    @Column(name = "cod_movimentacao", nullable = false)
    private Long codMovimentacao;
    @Column(name = "cod_veiculo")
    private Long codVeiculo;
    @Column(name = "cod_diagrama")
    private Long codDiagrama;
    @Column(name = "km_veiculo")
    private Long kmColetadoVeiculo;
    @Column(name = "posicao_pneu_origem")
    private Long posicaoPneuOrigem;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_origem")
    private OrigemDestinoEnum tipoOrigem;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao")
    private MovimentacaoEntity movimentacao;
}
