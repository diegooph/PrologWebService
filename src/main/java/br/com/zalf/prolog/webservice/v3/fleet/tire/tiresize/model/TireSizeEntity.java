package br.com.zalf.prolog.webservice.v3.fleet.tire.tiresize.model;

import br.com.zalf.prolog.webservice.commons.util.datetime.TimezoneUtils;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.OrigemAcaoEnum;
import br.com.zalf.prolog.webservice.v3.LocalDateTimeUtcAttributeConverter;
import br.com.zalf.prolog.webservice.v3.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Getter
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
    private boolean isActive;
    @Convert(converter = LocalDateTimeUtcAttributeConverter.class)
    @Column(name = "data_hora_cadastro", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_colaborador_cadastro", referencedColumnName = "codigo")
    private UserEntity createByUser;
    @Convert(converter = LocalDateTimeUtcAttributeConverter.class)
    @Column(name = "data_hora_ultima_atualizacao", nullable = false)
    private LocalDateTime lastUpdateAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_colaborador_ultima_atualizacao", referencedColumnName = "codigo")
    private UserEntity lastUpdateUser;
    @Column(name = "origem_cadastro", nullable = false)
    private OrigemAcaoEnum registerOrigin;

    @NotNull
    public Optional<UserEntity> getCreateByUser() {
        return Optional.ofNullable(createByUser);
    }

    @NotNull
    public LocalDateTime getCreatedAtWithTimezone() {
        return createByUser != null
                ? TimezoneUtils.applyTimezone(createdAt, createByUser.getUserZoneId())
                : createdAt;
    }

    @NotNull
    public Optional<UserEntity> getLastUpdateUser() {
        return Optional.ofNullable(lastUpdateUser);
    }

    @NotNull
    public Optional<LocalDateTime> getLastUpdatedAtWithTimezone() {
        return lastUpdateAt != null && lastUpdateUser != null
                ? Optional.of(TimezoneUtils.applyTimezone(lastUpdateAt, lastUpdateUser.getUserZoneId()))
                : Optional.empty();
    }
}
