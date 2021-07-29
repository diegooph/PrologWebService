package br.com.zalf.prolog.webservice.v3.fleet.tire._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.inspection._model.InspectionMeasureEntity;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoDestinoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tire.pneuservico.PneuServicoRealizadoEntity;
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
    @NotNull
    private Long id;
    @Column(name = "cod_empresa", nullable = false)
    @NotNull
    private Long companyId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_unidade", referencedColumnName = "codigo")
    @NotNull
    private BranchEntity branchEntity;
    @Column(name = "codigo_cliente", nullable = false)
    @NotNull
    private String clientNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_modelo", referencedColumnName = "codigo")
    @NotNull
    private TireModelEntity tireModelEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_dimensao", referencedColumnName = "codigo")
    @NotNull
    private TireSizeEntity tireSizeEntity;
    @Column(name = "pressao_recomendada", nullable = false)
    @NotNull
    private Double recommendedPressure;
    @Column(name = "pressao_atual")
    @Nullable
    private Double currentPressure;
    @Column(name = "altura_sulco_interno")
    @Nullable
    private Double internalGroove;
    @Column(name = "altura_sulco_central_interno")
    @Nullable
    private Double middleInternalGroove;
    @Column(name = "altura_sulco_central_externo")
    @Nullable
    private Double middleExternalGroove;
    @Column(name = "altura_sulco_externo")
    @Nullable
    private Double externalGroove;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private StatusPneu tireStatus;
    @Column(name = "vida_atual")
    @NotNull
    private Integer timesRetreaded;
    @Column(name = "vida_total")
    @NotNull
    private Integer maxRetreads;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_modelo_banda", referencedColumnName = "codigo")
    @Nullable
    private TreadModelEntity treadModelEntity;
    @Column(name = "dot", length = 20)
    @Nullable
    private String dot;
    @Column(name = "valor", nullable = false)
    @NotNull
    private BigDecimal price;
    @Column(name = "data_hora_cadastro", columnDefinition = "timestamp with time zone default now()")
    @NotNull
    private OffsetDateTime createdAt;
    @Column(name = "pneu_novo_nunca_rodado", columnDefinition = "boolean default false", nullable = false)
    private boolean isTireNew;
    @Column(name = "cod_unidade_cadastro", nullable = false)
    @NotNull
    private Long branchIdRegister;
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_cadastro", nullable = false)
    @NotNull
    private OrigemAcaoEnum registerOrigin;
    @OneToMany(mappedBy = "tireEntity", fetch = FetchType.LAZY, targetEntity = InspectionMeasureEntity.class)
    @Nullable
    private Set<InspectionMeasureEntity> inspectionMeasureEntities;
    @OneToMany(mappedBy = "pneuServicoRealizado", fetch = FetchType.LAZY)
    @Nullable
    private Set<PneuServicoRealizadoEntity> servicosRealizados;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "veiculo_pneu",
               joinColumns = @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo"),
               inverseJoinColumns = @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo"))
    @Nullable
    private VehicleEntity vehicleApplied;
    @Formula(value = "(select vp.posicao from veiculo_pneu vp where vp.cod_pneu = codigo)")
    @Nullable
    private Integer positionApplied;
    @OneToMany(mappedBy = "pneu", fetch = FetchType.LAZY)
    @Nullable
    private Set<MovimentacaoEntity> movimentacoesPneu;

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
    public BigDecimal getValorUltimaBandaAplicada() {
        if (servicosRealizados == null) {
            return null;
        }
        return servicosRealizados.stream()
                .filter(PneuServicoRealizadoEntity::isIncrementaVida)
                .max(Comparator.comparing(PneuServicoRealizadoEntity::getCodigo))
                .map(PneuServicoRealizadoEntity::getCusto)
                .orElse(null);
    }

    @Nullable
    public MovimentacaoDestinoEntity getUltimaMovimentacaoByStatus(@NotNull final OrigemDestinoEnum statusPneu) {
        if (movimentacoesPneu == null) {
            return null;
        }
        return movimentacoesPneu.stream()
                .map(MovimentacaoEntity::getMovimentacaoDestino)
                .filter(destino -> destino.getTipoDestino().equals(statusPneu))
                .max(Comparator.comparing(MovimentacaoDestinoEntity::getCodMovimentacao))
                .orElse(null);
    }
}
