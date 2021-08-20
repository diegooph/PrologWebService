package br.com.zalf.prolog.webservice.v3.fleet.tire._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.InspectionMeasureEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model.TireSizeEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementDestinationEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice._model.TireServiceEntity;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created on 2021-03-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "pneu", schema = "public")
public final class TireEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @Column(name = "cod_empresa", nullable = false)
    private Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_unidade", referencedColumnName = "codigo")
    private BranchEntity branchEntity;
    @Column(name = "codigo_cliente", nullable = false)
    private String clientNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_modelo", referencedColumnName = "codigo")
    private TireModelEntity tireModelEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_dimensao", referencedColumnName = "codigo")
    private TireSizeEntity tireSizeEntity;
    @Column(name = "pressao_recomendada", nullable = false)
    private Double recommendedPressure;
    @Column(name = "pressao_atual")
    private Double currentPressure;
    @Column(name = "altura_sulco_interno")
    private Double internalGroove;
    @Column(name = "altura_sulco_central_interno")
    private Double middleInternalGroove;
    @Column(name = "altura_sulco_central_externo")
    private Double middleExternalGroove;
    @Column(name = "altura_sulco_externo")
    private Double externalGroove;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPneu tireStatus;
    @Column(name = "vida_atual")
    private Integer timesRetreaded;
    @Column(name = "vida_total")
    private Integer maxRetreads;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_modelo_banda", referencedColumnName = "codigo")
    private TreadModelEntity treadModelEntity;
    @Column(name = "dot", length = 20)
    private String dot;
    @Column(name = "valor", nullable = false)
    private BigDecimal price;
    @Column(name = "data_hora_cadastro", columnDefinition = "timestamp with time zone default now()")
    private OffsetDateTime createdAt;
    @Column(name = "pneu_novo_nunca_rodado", columnDefinition = "boolean default false", nullable = false)
    private boolean isTireNew;
    @Column(name = "cod_unidade_cadastro", nullable = false)
    private Long branchIdRegister;
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_cadastro", nullable = false)
    private OrigemAcaoEnum registerOrigin;
    @OneToMany(mappedBy = "tireEntity", fetch = FetchType.LAZY, targetEntity = InspectionMeasureEntity.class)
    private Set<InspectionMeasureEntity> inspectionMeasureEntities;
    @OneToMany(mappedBy = "tireServiceTypeEntity", fetch = FetchType.LAZY)
    private Set<TireServiceEntity> tireServiceEntities;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "veiculo_pneu",
               joinColumns = @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo"),
               inverseJoinColumns = @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo"))
    private VehicleEntity vehicleApplied;
    @Formula(value = "(select vp.posicao from veiculo_pneu vp where vp.cod_pneu = codigo)")
    private Integer positionApplied;
    @OneToMany(mappedBy = "tireEntity", fetch = FetchType.LAZY)
    private Set<TireMovementEntity> tireMovementEntities;

    public boolean isRetreaded() {
        return timesRetreaded > 1;
    }

    @NotNull
    public Integer getPreviousRetread() {
        return timesRetreaded - 1;
    }

    @Transient
    @Nullable
    public Double getLowerGroove() {
        return Stream.of(internalGroove, middleInternalGroove, middleExternalGroove, externalGroove)
                .filter(Objects::nonNull)
                .min(Double::compareTo)
                .orElse(null);
    }

    @Nullable
    public BigDecimal getPriceLastTreadApplied() {
        if (tireServiceEntities == null) {
            return null;
        }
        return tireServiceEntities.stream()
                .filter(TireServiceEntity::isIncreaseLifeCycle)
                .max(Comparator.comparing(TireServiceEntity::getId))
                .map(TireServiceEntity::getServiceCost)
                .orElse(null);
    }

    @Nullable
    public TireMovementDestinationEntity getLastTireMovementByStatus(@NotNull final OrigemDestinoEnum tireStatus) {
        if (tireMovementEntities == null) {
            return null;
        }
        return tireMovementEntities.stream()
                .map(TireMovementEntity::getTireMovementDestinationEntity)
                .filter(destination -> destination.getMovementDestinationType().equals(tireStatus))
                .max(Comparator.comparing(TireMovementDestinationEntity::getTireMovementId))
                .orElse(null);
    }
}
