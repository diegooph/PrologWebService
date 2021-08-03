package br.com.zalf.prolog.webservice.v3.fleet.tireservice.movement._model;

import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created on 2021-06-24
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@IdClass(TireServiceRetreaderPk.class)
@Table(schema = "public", name = "movimentacao_pneu_servico_realizado_recapadora")
public class TireServiceRetreaderEntity {
    @Id
    @Column(name = "cod_movimentacao", nullable = false)
    private Long tireMovementId;
    @Id
    @Column(name = "cod_servico_realizado_movimentacao", nullable = false)
    private Long tireServiceMovementId;
    @Id
    @Column(name = "cod_recapadora", nullable = false)
    private Long retreaderId;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao", referencedColumnName = "codigo", nullable = false)
    private TireMovementEntity tireMovementEntity;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_servico_realizado_movimentacao",
                referencedColumnName = "cod_servico_realizado",
                nullable = false)
    private TireServiceMovementEntity tireServiceMovementEntity;
}
