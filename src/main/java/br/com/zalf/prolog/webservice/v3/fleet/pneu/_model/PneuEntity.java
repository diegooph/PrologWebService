package br.com.zalf.prolog.webservice.v3.fleet.pneu._model;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.fleet.afericao.valores._model.AfericaoPneuValorEntity;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoDestinoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.movimentacao._model.MovimentacaoEntity;
import br.com.zalf.prolog.webservice.v3.fleet.pneu.pneuservico.PneuServicoRealizadoEntity;
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
@Entity
@Table(name = "pneu", schema = "public")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PneuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_empresa", nullable = false)
    private Long codEmpresa;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_unidade", referencedColumnName = "codigo")
    private BranchEntity unidade;
    @Column(name = "codigo_cliente", nullable = false)
    private String codigoCliente;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_modelo", referencedColumnName = "codigo")
    private ModeloPneuEntity modeloPneu;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_dimensao", referencedColumnName = "codigo")
    private DimensaoPneuEntity dimensaoPneu;
    @Column(name = "pressao_recomendada", nullable = false)
    private Double pressaoRecomendada;
    @Column(name = "pressao_atual")
    private Double pressaoAtual;
    @Column(name = "altura_sulco_interno")
    private Double alturaSulcoInterno;
    @Column(name = "altura_sulco_central_interno")
    private Double alturaSulcoCentralInterno;
    @Column(name = "altura_sulco_central_externo")
    private Double alturaSulcoCentralExterno;
    @Column(name = "altura_sulco_externo")
    private Double alturaSulcoExterno;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPneu status;
    @Column(name = "vida_atual")
    private Integer vidaAtual;
    @Column(name = "vida_total")
    private Integer vidaTotal;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_modelo_banda", referencedColumnName = "codigo")
    private ModeloBandaEntity modeloBanda;
    @Column(name = "dot", length = 20)
    private String dot;
    @Column(name = "valor", nullable = false)
    private BigDecimal valor;
    @Column(name = "data_hora_cadastro", columnDefinition = "timestamp with time zone default now()")
    private OffsetDateTime dataHoraCadastro;
    @Column(name = "pneu_novo_nunca_rodado", columnDefinition = "boolean default false", nullable = false)
    private boolean pneuNovoNuncaRodado;
    @Column(name = "cod_unidade_cadastro", nullable = false)
    private Long codUnidadeCadastro;
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_cadastro", nullable = false)
    private OrigemAcaoEnum origemCadastro;
    @OneToMany(mappedBy = "pneu", fetch = FetchType.LAZY, targetEntity = AfericaoPneuValorEntity.class)
    private Set<AfericaoPneuValorEntity> valoresPneu;
    @OneToMany(mappedBy = "pneuServicoRealizado", fetch = FetchType.LAZY)
    private Set<PneuServicoRealizadoEntity> servicosRealizados;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "veiculo_pneu",
               joinColumns = @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo"),
               inverseJoinColumns = @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo"))
    private VehicleEntity veiculoPneuAplicado;
    @Formula(value = "(select vp.posicao from veiculo_pneu vp where vp.cod_pneu = codigo)")
    private Integer posicaoAplicado;
    @OneToMany(mappedBy = "pneu", fetch = FetchType.LAZY)
    private Set<MovimentacaoEntity> movimentacoesPneu;

    public boolean isRecapado() {
        return vidaAtual > 1;
    }

    @NotNull
    public Integer getVidaAnterior() {
        return this.vidaAtual - 1;
    }

    @Transient
    @Nullable
    public Double getMenorSulco() {
        return Stream.of(alturaSulcoInterno, alturaSulcoCentralInterno, alturaSulcoCentralExterno, alturaSulcoExterno)
                .filter(Objects::nonNull)
                .min(Double::compareTo)
                .orElse(null);
    }

    @Nullable
    public BigDecimal getValorUltimaBandaAplicada() {
        return servicosRealizados.stream()
                .filter(PneuServicoRealizadoEntity::isIncrementaVida)
                .max(Comparator.comparing(PneuServicoRealizadoEntity::getCodigo))
                .map(PneuServicoRealizadoEntity::getCusto)
                .orElse(null);
    }

    @Nullable
    public MovimentacaoDestinoEntity getUltimaMovimentacaoByStatus(@NotNull final OrigemDestinoEnum statusPneu) {
        return movimentacoesPneu.stream()
                .map(MovimentacaoEntity::getMovimentacaoDestino)
                .filter(destino -> destino.getTipoDestino().equals(statusPneu))
                .max(Comparator.comparing(MovimentacaoDestinoEntity::getCodMovimentacao))
                .orElse(null);
    }
}
