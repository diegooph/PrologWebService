package br.com.zalf.prolog.webservice.v3.fleet.processeskm._model;

import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoTipoProcesso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.OffsetDateTime;

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
@Table(schema = "public", name = "veiculo_processo_alteracao_km")
public final class UpdateProcessKmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @Column(name = "data_hora_alteracao", nullable = false)
    private OffsetDateTime kmUpdatedAt;
    @Column(name = "cod_colaborador_alteracao")
    private Long userIdUpdate;
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_alteracao", nullable = false)
    private OrigemAcaoEnum updateSource;
    @Column(name = "cod_processo_alterado", nullable = false)
    private Long processIdUpdated;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_processo_alterado", nullable = false)
    private VeiculoTipoProcesso processTypeUpdated;
    @Column(name = "km_antigo", nullable = false)
    private long oldKm;
    @Column(name = "km_novo", nullable = false)
    private long newKm;
}
