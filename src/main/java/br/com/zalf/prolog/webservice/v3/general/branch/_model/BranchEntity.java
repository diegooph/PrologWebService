package br.com.zalf.prolog.webservice.v3.general.branch._model;

import br.com.zalf.prolog.webservice.v3.general.company.CompanyEntity;
import br.com.zalf.prolog.webservice.v3.general.group.GroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created on 2020-11-24
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "unidade")
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    @NotNull
    private Long id;
    @Column(name = "nome", length = 40, nullable = false)
    @NotNull
    private String name;
    @Formula(value = "(select count(c.*) from colaborador c where c.cod_unidade = codigo)")
    @NotNull
    private Integer totalUsers;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_regional", referencedColumnName = "codigo")
    @NotNull
    private GroupEntity groupEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_empresa", referencedColumnName = "codigo")
    @NotNull
    private CompanyEntity companyEntity;
    @Column(name = "timezone", nullable = false)
    @NotNull
    private String timezone;
    @Column(name = "data_hora_cadastro", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @NotNull
    private LocalDateTime createdAt;
    @Column(name = "status_ativo", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive;
    @Column(name = "cod_auxiliar")
    @Nullable
    private String additionalId;
    @Column(name = "latitude_unidade")
    @Nullable
    private String branchLatitude;
    @Column(name = "longitude_unidade")
    @Nullable
    private String branchLongitude;
}
