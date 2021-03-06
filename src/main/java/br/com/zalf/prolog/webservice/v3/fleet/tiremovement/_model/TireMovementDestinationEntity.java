package br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
public final class TireMovementDestinationEntity {
    @Id
    @Column(name = "cod_movimentacao", nullable = false)
    @NotNull
    private Long tireMovementId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo")
    @Nullable
    private VehicleEntity vehicleEntity;
    @Column(name = "cod_diagrama")
    @Nullable
    private Long vehicleLayoutId;
    @Column(name = "km_veiculo")
    @Nullable
    private Long vehicleKm;
    @Column(name = "posicao_pneu_destino")
    @Nullable
    private Long tirePosition;
    @Column(name = "cod_coleta")
    @Nullable
    private String additionalInformation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_recapadora_destino", referencedColumnName = "codigo")
    @Nullable
    private RetreaderEntity retreaderEntity;
    @Column(name = "cod_motivo_descarte")
    @Nullable
    private Long scrapReasonId;
    @Column(name = "url_imagem_descarte_1")
    @Nullable
    private String urlScrapImage1;
    @Column(name = "url_imagem_descarte_2")
    @Nullable
    private String urlScrapImage2;
    @Column(name = "url_imagem_descarte_3")
    @Nullable
    private String urlScrapImage3;
    @Column(name = "tipo_destino")
    @NotNull
    private OrigemDestinoEnum movementDestinationType;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao")
    @NotNull
    private TireMovementEntity tireMovementEntity;
}
