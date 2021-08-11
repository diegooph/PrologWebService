package br.com.zalf.prolog.webservice.v3.fleet.tire._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    private Long id;
    @Column(name = "altura", nullable = false)
    private Double width;
    @Column(name = "largura", nullable = false)
    private Double aspectRation;
    @Column(name = "aro", nullable = false)
    private Double diameter;
    @Column(name = "cod_empresa", nullable = false)
    private Long companyId;
    @Column(name = "cod_auxiliar", nullable = false)
    private String additionalId;
    @Column(name = "status_ativo", nullable = false)
    private boolean isActive;
    @Column(name = "data_hora_cadastro", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "cod_colaborador_cadastro", nullable = false)
    private Long createdByUserId;
    @Column(name = "data_hora_ultima_atualizacao", nullable = false)
    private LocalDateTime lastedUpdateAt;
    @Column(name = "cod_colaborador_ultima_atualizacao", nullable = false)
    private Long lastedUpdateUserId;
    @Column(name = "origem_cadastro", nullable = false)
    private String registerOrigin;

    @NotNull
    public String getTireSizeUserFriendly() {
        return aspectRation + "/" + width + " R" + diameter;
    }
}
