package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import br.com.zalf.prolog.webservice.v3.LocalDateTimeUtcAttributeConverter;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.InspectionEntity;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.InspectionMeasureEntity;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedEntity;
import br.com.zalf.prolog.webservice.v3.fleet.processeskm._model.KmCollectedVehicle;
import br.com.zalf.prolog.webservice.v3.fleet.tire._model.TireEntity;
import br.com.zalf.prolog.webservice.v3.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

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
@Table(schema = "public", name = "afericao_manutencao")
public final class TireMaintenanceEntity implements KmCollectedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @Column(name = "cod_unidade", nullable = false)
    private Long branchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_afericao", nullable = false)
    private InspectionEntity inspectionEntity;
    @Column(name = "km_momento_conserto")
    private Long vehicleKmAtResolution;
    @Column(name = "tipo_servico", nullable = false)
    private TipoServico maintenanceType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo")
    private TireEntity tire;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpf_mecanico", referencedColumnName = "cpf")
    private UserEntity resolverUser;
    @Convert(converter = LocalDateTimeUtcAttributeConverter.class)
    @Column(name = "data_hora_resolucao")
    private LocalDateTime resolvedAt;
    @Column(name = "qt_apontamentos", nullable = false, columnDefinition = "default 1")
    private Integer amountTimesPointed;
    @Column(name = "psi_apos_conserto")
    private Double tirePressureAfterMaintenance;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_alternativa", referencedColumnName = "codigo")
    private TireMaintenanceProblemEntity tireMaintenanceProblemEntity;
    @Column(name = "tempo_realizacao_millis")
    private Long resolutionAmountTimeInMilliseconds;
    @Column(name = "fechado_automaticamente_movimentacao", nullable = false, columnDefinition = "default false")
    private Boolean resolvedAutomaticallyByTireMove;
    @Column(name = "fechado_automaticamente_integracao", nullable = false, columnDefinition = "default false")
    private Boolean resolvedAutomaticallyByIntegration;
    @Column(name = "fechado_automaticamente_afericao", nullable = false, columnDefinition = "default false")
    private Boolean resolvedAutomaticallyByTireInspection;
    @Formula("(fechado_automaticamente_movimentacao " +
                     "or fechado_automaticamente_integracao " +
                     "or fechado_automaticamente_afericao)")
    private boolean isResolvedAutomatically;
    @Column(name = "forma_coleta_dados_fechamento")
    private FormaColetaDadosAfericaoEnum dataInspectionType;

    @NotNull
    @Override
    public KmCollectedVehicle getKmCollectedVehicle() {
        if (vehicleKmAtResolution == null) {
            throw new IllegalStateException("O KM n??o pode ser null!");
        }
        return KmCollectedVehicle.of(inspectionEntity.getVehicleEntity().getId(), vehicleKmAtResolution);
    }

    @NotNull
    public Optional<TireMaintenanceProblemEntity> getTireMaintenanceProblemEntity() {
        return Optional.ofNullable(tireMaintenanceProblemEntity);
    }

    @NotNull
    public Optional<UserEntity> getResolverUser() {
        return Optional.ofNullable(resolverUser);
    }

    @Transient
    @NotNull
    public TireMaintenanceStatus getTireMaintenanceStatus() {
        return isResolvedAutomatically() || resolverUser != null
                ? TireMaintenanceStatus.RESOLVED
                : TireMaintenanceStatus.OPEN;
    }

    @NotNull
    public Optional<InspectionMeasureEntity> getValorAfericaoRelatedToPneu() {
        return inspectionEntity.getInspectionMeasureEntities().stream()
                .filter(measureEntity -> Objects.equals(measureEntity.getTireEntity(), tire))
                .findFirst();
    }
}
