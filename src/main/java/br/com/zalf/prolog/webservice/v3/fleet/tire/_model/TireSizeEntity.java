package br.com.zalf.prolog.webservice.v3.fleet.tire._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * Created on 2021-05-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "dimensao_pneu", schema = "public")
public final class TireSizeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "altura", nullable = false)
    @NotNull
    private Integer width;
    @Column(name = "largura", nullable = false)
    @NotNull
    private Integer aspectRation;
    @Column(name = "aro", nullable = false)
    @NotNull
    private Double diameter;

    @NotNull
    public String getTireSizeUserFriendly() {
        return aspectRation + "/" + width + " R" + diameter;
    }
}
