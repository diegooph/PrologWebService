package br.com.zalf.prolog.webservice.v3.fleet.transferenciaveiculo._model;

import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.fleet.kmprocessos._model.VeiculoKmColetado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

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
@Table(schema = "public", name = "veiculo_transferencia_informacoes")
public final class TransferenciaVeiculoInformacaoEntity implements EntityKmColetado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_veiculo", nullable = false)
    private Long codVeiculo;
    @Column(name = "km_veiculo_momento_transferencia", nullable = false)
    private long kmColetadoVeiculoMomentoTransferencia;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_processo_transferencia", nullable = false)
    private TransferenciaVeiculoProcessoEntity transferenciaVeiculoProcesso;

    @NotNull
    @Override
    public VeiculoKmColetado getVeiculoKmColetado() {
        return VeiculoKmColetado.of(codVeiculo, kmColetadoVeiculoMomentoTransferencia);
    }
}
