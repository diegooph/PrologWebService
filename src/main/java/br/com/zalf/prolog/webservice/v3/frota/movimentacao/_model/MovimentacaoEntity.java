package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import br.com.zalf.prolog.webservice.v3.frota.pneu._model.PneuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
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
@Table(schema = "public", name = "movimentacao")
public final class MovimentacaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @OneToOne(mappedBy = "movimentacao", fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private MovimentacaoOrigemEntity movimentacaoOrigem;
    @OneToOne(mappedBy = "movimentacao", fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private MovimentacaoDestinoEntity movimentacaoDestino;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao_processo", nullable = false)
    private MovimentacaoProcessoEntity movimentacaoProcesso;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_pneu", referencedColumnName = "codigo", nullable = false)
    private PneuEntity pneu;
    @Column(name = "sulco_interno")
    private double sulcoInterno;
    @Column(name = "sulco_central_interno")
    private double sulcoCentralInterno;
    @Column(name = "sulco_central_externo")
    private double sulcoCentralExterno;
    @Column(name = "sulco_externo")
    private double sulcoExterno;
    @Column(name = "sulco_externo")
    private double pressaoAtual;
    @Column(name = "vida")
    private int vida;
    @Column(name = "observacao")
    private String observacao;

    public boolean isMovimentacaoNoVeiculo(@NotNull final Long codVeiculo) {
        return codVeiculo.equals(movimentacaoOrigem.getCodVeiculo())
                || codVeiculo.equals(movimentacaoDestino.getCodVeiculo());
    }

    public boolean isMovimentacaoNoVeiculo() {
        return movimentacaoOrigem.getCodVeiculo() != null || movimentacaoDestino.getCodVeiculo() != null;
    }

    @NotNull
    public Optional<VeiculoMovimentacao> getVeiculo() {
        if (movimentacaoOrigem.getCodVeiculo() != null) {
            return Optional.of(new VeiculoMovimentacao(movimentacaoOrigem.getCodVeiculo(),
                                                       movimentacaoOrigem.getKmColetadoVeiculo()));
        } else if (movimentacaoDestino.getCodVeiculo() != null) {
            return Optional.of(new VeiculoMovimentacao(movimentacaoDestino.getCodVeiculo(),
                                                       movimentacaoDestino.getKmColetadoVeiculo()));
        }

        return Optional.empty();
    }
}
