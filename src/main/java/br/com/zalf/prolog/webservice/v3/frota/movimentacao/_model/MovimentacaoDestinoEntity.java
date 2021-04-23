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
@Table(schema = "public", name = "movimentacao_destino")
public final class MovimentacaoDestinoEntity {
    @Id
    @Column(name = "cod_movimentacao", nullable = false)
    private Long codMovimentacao;
    @Column(name = "cod_veiculo")
    private Long codVeiculo;
    @Column(name = "cod_diagrama")
    private Long codDiagrama;
    @Column(name = "km_veiculo")
    private Long kmColetadoVeiculo;
    @Column(name = "posicao_pneu_destino")
    private Long posicaoPneuDestino;
    @Column(name = "cod_motivo_descarte")
    private Long codMotivoDescarte;
    @Column(name = "cod_coleta")
    private Long codColeta;
    @Column(name = "cod_recapadora_destino")
    private Long codRecapadoraDestino;
    @Column(name = "url_imagem_descarte_1")
    private Long urlImagemDescarte1;
    @Column(name = "url_imagem_descarte_2")
    private Long urlImagemDescarte2;
    @Column(name = "url_imagem_descarte_3")
    private Long urlImagemDescarte3;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_destino")
    private OrigemDestinoEnum tipoDestino;
    @MapsId
    @OneToOne
    @JoinColumn(name = "cod_movimentacao")
    private MovimentacaoEntity movimentacao;
}
