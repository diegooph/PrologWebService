package br.com.zalf.prolog.webservice.v3.frota.afericao._model;

import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos.VeiculoKmColetado;
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
@Table(schema = "public", name = "afericao")
public final class AfericaoEntity implements EntityKmColetado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @Column(name = "cod_veiculo", nullable = false)
    private Long codVeiculo;
    @Column(name = "km_veiculo", nullable = false)
    private long kmColetadoVeiculo;

    @NotNull
    @Override
    public VeiculoKmColetado getVeiculoKmColetado() {
        return VeiculoKmColetado.of(codVeiculo, kmColetadoVeiculo);
    }
}
