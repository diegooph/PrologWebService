package br.com.zalf.prolog.webservice.v3.general.company;

import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created on 2021-05-17
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "empresa", schema = "public")
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @Column(name = "nome", nullable = false)
    private String name;
    @Column(name = "logo_thumbnail_url")
    private String thumbnailLogoUrl;
    @Column(name = "data_hora_cadastro", nullable = false, columnDefinition = "default now()")
    private LocalDateTime createdAt;
    @Column(name = "cod_auxiliar")
    private String codAuxiliar;
    @Column(name = "status_ativo", nullable = false, columnDefinition = "default true")
    private boolean isActive;
    @OneToMany(mappedBy = "companyEntity", fetch = FetchType.LAZY, targetEntity = BranchEntity.class)
    private Set<BranchEntity> branches;
}
