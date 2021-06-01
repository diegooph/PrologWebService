package br.com.zalf.prolog.webservice.v3.frota.afericao._model;

import br.com.zalf.prolog.webservice.v3.LocalDateTimeUtcAttributeConverter;
import br.com.zalf.prolog.webservice.v3.frota.afericao.valores._model.AfericaoPneuValorEntity;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.EntityKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model.VeiculoKmColetado;
import br.com.zalf.prolog.webservice.v3.frota.servicopneu._model.ServicoPneuEntity;
import br.com.zalf.prolog.webservice.v3.frota.veiculo._model.VeiculoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
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
@Table(schema = "public", name = "afericao")
public final class AfericaoEntity implements EntityKmColetado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_veiculo", referencedColumnName = "codigo", nullable = false)
    private VeiculoEntity veiculo;
    @Column(name = "km_veiculo", nullable = false)
    private Long kmColetadoVeiculo;
    @OneToMany(mappedBy = "afericao", fetch = FetchType.LAZY)
    private Set<ServicoPneuEntity> servicosGeradosPneu;
    @Convert(converter = LocalDateTimeUtcAttributeConverter.class)
    @Column(name = "data_hora")
    private LocalDateTime dataHora;
    @OneToMany(mappedBy = "afericao", fetch = FetchType.LAZY, targetEntity = AfericaoPneuValorEntity.class)
    private Set<AfericaoPneuValorEntity> valoresAfericao;

    @NotNull
    @Override
    public VeiculoKmColetado getVeiculoKmColetado() {
        return VeiculoKmColetado.of(veiculo.getCodigo(), kmColetadoVeiculo);
    }
}
