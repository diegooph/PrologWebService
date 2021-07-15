package br.com.zalf.prolog.webservice.v3.frota.acoplamento._model;

import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created on 2021-06-14
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@IdClass(AcoplamentoAtualPk.class)
@Table(schema = "public", name = "veiculo_acoplamento_atual")
public class AcoplamentoAtualEntity {
    @Id
    @Column(name = "cod_processo")
    private Long codProcesso;
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_processo", referencedColumnName = "codigo")
    private AcoplamentoProcessoEntity acoplamentoProcessoEntity;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @Id
    @Column(name = "cod_posicao", nullable = false)
    private Short codPosicao;
    @Column(name = "cod_diagrama", nullable = false)
    private Long codDiagrama;
    @Column(name = "motorizado", nullable = false)
    private boolean motorizado;
    @OneToOne
    @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo")
    private VeiculoEntity veiculoEntity;
    @Column(name = "acoplado", nullable = false)
    private boolean acoplado;

    @NotNull
    public Long getCodVeiculoAcoplamentoAtual() {
        return veiculoEntity.getCodigo();
    }
}
