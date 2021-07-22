package br.com.zalf.prolog.webservice.v3.fleet.tiremaintenance._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created on 2021-05-25
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Data
@Entity
@Table(name = "afericao_alternativa_manutencao_inspecao", schema = "public")
public final class TireMaintenanceProblemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "alternativa", nullable = false)
    @NotNull
    private String name;
    @Column(name = "status_ativo", nullable = false)
    @NotNull
    private Boolean isActive;
}
