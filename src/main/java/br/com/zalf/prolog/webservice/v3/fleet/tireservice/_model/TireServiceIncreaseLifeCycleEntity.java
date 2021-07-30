package br.com.zalf.prolog.webservice.v3.fleet.tireservice._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created on 2021-03-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pneu_servico_realizado_incrementa_vida", schema = "public")
public class TireServiceIncreaseLifeCycleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "cod_servico_realizado", nullable = false)
    @NotNull
    private Long tireServiceId;
    @Column(name = "cod_modelo_banda", nullable = false)
    @NotNull
    private Long treadModelId;
    @Column(name = "vida_nova_pneu", nullable = false)
    @NotNull
    private Integer newTireLifeCycle;
    @Column(name = "fonte_servico_realizado", nullable = false, length = 20)
    @NotNull
    private String tireServiceOrigin;
}