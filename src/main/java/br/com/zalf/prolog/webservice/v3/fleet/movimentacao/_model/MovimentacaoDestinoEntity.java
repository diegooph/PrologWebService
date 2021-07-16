package br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VeiculoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo")
    private VeiculoEntity veiculo;
    @Column(name = "cod_diagrama")
    private Long codDiagrama;
    @Column(name = "km_veiculo")
    private Long kmColetadoVeiculo;
    @Column(name = "posicao_pneu_destino")
    private Long posicaoPneuDestino;
    @Column(name = "cod_motivo_descarte")
    private Long codMotivoDescarte;
    @Column(name = "cod_coleta")
    private String codColeta;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_recapadora_destino", referencedColumnName = "codigo")
    private RecapadoraEntity recapadora;
    @Column(name = "url_imagem_descarte_1")
    private String urlImagemDescarte1;
    @Column(name = "url_imagem_descarte_2")
    private String urlImagemDescarte2;
    @Column(name = "url_imagem_descarte_3")
    private String urlImagemDescarte3;
    @Column(name = "tipo_destino")
    private OrigemDestinoEnum tipoDestino;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao")
    private MovimentacaoEntity movimentacao;
}
