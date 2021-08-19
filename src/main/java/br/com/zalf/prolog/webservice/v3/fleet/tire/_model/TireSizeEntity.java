package br.com.zalf.prolog.webservice.v3.fleet.tire._model;

import br.com.zalf.prolog.webservice.v3.user.UserEntity;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created on 2021-05-18
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "dimensao_pneu", schema = "public")
public final class TireSizeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @Column(name = "altura", nullable = false)
    private Double height;
    @Column(name = "largura", nullable = false)
    private Double width;
    @Column(name = "aro", nullable = false)
    private Double rim;
    @Column(name = "cod_empresa", nullable = false)
    private Long companyId;
    @Column(name = "cod_auxiliar", nullable = false)
    private String additionalId;
    @Column(name = "status_ativo", nullable = false)
    private boolean active;
    @Column(name = "data_hora_cadastro", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_colaborador_cadastro", referencedColumnName = "codigo")
    private UserEntity createByUser;
    @Column(name = "data_hora_ultima_atualizacao", nullable = false)
    private LocalDateTime lastedUpdateAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_colaborador_ultima_atualizacao", referencedColumnName = "codigo")
    private UserEntity lastedUpdateUser;
    @Column(name = "origem_cadastro", nullable = false)
    private String registerOrigin;

    public Optional<UserEntity> getCreateByUser() {
        return Optional.ofNullable(createByUser);
    }

    public Optional<UserEntity> getLastedUpdateUser() {
        return Optional.ofNullable(lastedUpdateUser);
    }

    @NotNull
    public String getTireSizeUserFriendly() {
        return width + "/" + height + " R" + rim;
    }
}
