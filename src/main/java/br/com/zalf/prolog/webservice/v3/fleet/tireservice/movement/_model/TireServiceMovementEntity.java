package br.com.zalf.prolog.webservice.v3.fleet.tireservice.movement._model;

import br.com.zalf.prolog.webservice.v3.fleet.tiremovement._model.TireMovementEntity;
import br.com.zalf.prolog.webservice.v3.fleet.tireservice._model.TireServiceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

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
@IdClass(TireServiceMovementPk.class)
@Table(schema = "public", name = "movimentacao_pneu_servico_realizado")
public class TireServiceMovementEntity {
    @Id
    @Column(name = "cod_movimentacao", nullable = false)
    @NotNull
    private Long tireMovementId;
    @Id
    @Column(name = "cod_servico_realizado", nullable = false)
    @NotNull
    private Long tireServiceId;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_movimentacao", referencedColumnName = "codigo", nullable = false)
    @NotNull
    private TireMovementEntity tireMovementEntity;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_servico_realizado", referencedColumnName = "codigo", nullable = false)
    @NotNull
    private TireServiceEntity tireServiceEntity;
    @Column(name = "fonte_servico_realizado", columnDefinition = "FONTE_MOVIMENTACAO", nullable = false)
    @NotNull
    private String tireServiceOrigin;
}
