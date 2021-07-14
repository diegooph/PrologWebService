package br.com.zalf.prolog.webservice.v3.fleet.socorrorota._model;

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
@Table(schema = "public", name = "socorro_rota_abertura")
public final class AberturaSocorroRotaEntity implements EntityKmColetado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_veiculo_problema", nullable = false)
    private Long codVeiculo;
    @Column(name = "cod_socorro_rota", nullable = false)
    private Long codSocorroRota;
    @Column(name = "cod_empresa", nullable = false)
    private Long codEmpresa;
    @Column(name = "km_veiculo_abertura", nullable = false)
    private long kmColetadoVeiculoAberturaSocorro;

    @NotNull
    @Override
    public VeiculoKmColetado getVeiculoKmColetado() {
        return VeiculoKmColetado.of(codVeiculo, kmColetadoVeiculoAberturaSocorro);
    }
}
