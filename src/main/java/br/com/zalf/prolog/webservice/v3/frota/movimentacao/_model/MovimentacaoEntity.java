package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.pneu.pneuservico.PneuServicoRealizadoEntity;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Optional;
import java.util.Set;

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
@Table(schema = "public", name = "movimentacao")
public final class MovimentacaoEntity implements PersistentAttributeInterceptable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo", referencedColumnName = "cod_movimentacao", nullable = false)
    private MovimentacaoOrigemEntity movimentacaoOrigem;
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo", referencedColumnName = "cod_movimentacao", nullable = false)
    private MovimentacaoDestinoEntity movimentacaoDestino;
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao_processo", nullable = false)
    private MovimentacaoProcessoEntity movimentacaoProcesso;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo", nullable = false)
    private PneuEntity pneu;
    @Column(name = "sulco_interno")
    private Double sulcoInterno;
    @Column(name = "sulco_central_interno")
    private Double sulcoCentralInterno;
    @Column(name = "sulco_central_externo")
    private Double sulcoCentralExterno;
    @Column(name = "sulco_externo")
    private Double sulcoExterno;
    @Column(name = "pressao_atual")
    private Double pressaoAtual;
    @Column(name = "vida")
    private int vida;
    @Column(name = "observacao")
    private String observacao;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "movimentacao_pneu_servico_realizado",
               joinColumns = @JoinColumn(name = "cod_movimentacao"),
               inverseJoinColumns = @JoinColumn(name = "cod_servico_realizado"))
    private Set<PneuServicoRealizadoEntity> servicosRealizados;
    @Transient
    private PersistentAttributeInterceptor persistentAttributeInterceptor;

    @Override
    public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
        return persistentAttributeInterceptor;
    }

    @Override
    public void $$_hibernate_setInterceptor(final PersistentAttributeInterceptor persistentAttributeInterceptor) {
        this.persistentAttributeInterceptor = persistentAttributeInterceptor;
    }

    public boolean isMovimentacaoNoVeiculo() {
        return movimentacaoOrigem.getVeiculo() != null || movimentacaoDestino.getVeiculo() != null;
    }

    @NotNull
    public Optional<VeiculoMovimentacao> getVeiculo() {
        if (movimentacaoOrigem.getVeiculo() != null) {
            return createVeiculoMovimentacao(movimentacaoOrigem.getVeiculo(),
                                             movimentacaoOrigem.getKmColetadoVeiculo(),
                                             movimentacaoOrigem.getCodDiagrama());
        } else if (movimentacaoDestino.getVeiculo() != null) {
            return createVeiculoMovimentacao(movimentacaoDestino.getVeiculo(),
                                             movimentacaoDestino.getKmColetadoVeiculo(),
                                             movimentacaoDestino.getCodDiagrama());
        }

        return Optional.empty();
    }

    @NotNull
    private Optional<VeiculoMovimentacao> createVeiculoMovimentacao(@NotNull final VeiculoEntity veiculo,
                                                                    @NotNull final Long kmColetadoVeiculo,
                                                                    @NotNull final Long codDiagrama) {
        return Optional.of(new VeiculoMovimentacao(veiculo.getCodigo(),
                                                   veiculo.getPlaca(),
                                                   veiculo.getIdentificadorFrota(),
                                                   kmColetadoVeiculo,
                                                   codDiagrama));
    }
}
