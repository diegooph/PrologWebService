package br.com.zalf.prolog.webservice.v3.frota.kmprocessos._model;

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
@Table(schema = "public", name = "veiculo_processo_edicao_km")
public final class AlteracaoKmProcessoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "data_hora_alteracao_km", nullable = false)
    private OffsetDateTime dataHoraAlteraoKm;
    @Column(name = "cod_colaborador_alteracao_km")
    private Long codColaboradorAlteracaoKm;
    @Column(name = "origem_alteracao", nullable = false)
    private OrigemAcaoEnum origemAlteracao;
    @Column(name = "cod_processo_alterado", nullable = false)
    private Long codProcessoAlterado;
    @Column(name = "tipo_processo_alterado", nullable = false)
    private VeiculoTipoProcesso tipoProcessoAlterado;
    @Column(name = "km_antigo", nullable = false)
    private long kmAntigo;
    @Column(name = "km_novo", nullable = false)
    private long kmNovo;
}
