package br.com.zalf.prolog.webservice.v3.fleet.attach._model;

import br.com.zalf.prolog.webservice.v3.LocalDateTimeUtcAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created on 2021-06-11
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "veiculo_acoplamento_processo")
public class AttachProcessEntity {
    @Id
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "cod_unidade", nullable = false)
    @NotNull
    private Long branchId;
    @Column(name = "cod_colaborador", nullable = false)
    @NotNull
    private Long userId;
    @Convert(converter = LocalDateTimeUtcAttributeConverter.class)
    @Column(name = "data_hora")
    @NotNull
    private LocalDateTime createdAt;
    @Column(name = "observacao")
    @Nullable
    private String notes;
    @OneToMany(mappedBy = "attachProcessEntity",
               fetch = FetchType.LAZY,
               targetEntity = CurrentAttachEntity.class)
    @NotNull
    private Set<CurrentAttachEntity> currentAttachEntities;
}
