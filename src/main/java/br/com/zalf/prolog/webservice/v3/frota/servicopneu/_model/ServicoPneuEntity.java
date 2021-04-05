package br.com.zalf.prolog.webservice.v3.frota.servicopneu._model;

import br.com.zalf.prolog.webservice.v3.frota.afericao._model.AfericaoEntity;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.VeiculoKmColetado;
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
@Table(schema = "public", name = "afericao_manutencao")
public final class ServicoPneuEntity implements EntityKmColetado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @Column(name = "km_momento_conserto")
    private long kmColetadoVeiculoFechamentoServico;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_afericao", nullable = false)
    private AfericaoEntity afericao;

    @NotNull
    @Override
    public VeiculoKmColetado getVeiculoKmColetado() {
        return VeiculoKmColetado.of(getAfericao().getCodVeiculo(), kmColetadoVeiculoFechamentoServico);
    }
}
