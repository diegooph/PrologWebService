package br.com.zalf.prolog.webservice.v3.general.empresa;

import br.com.zalf.prolog.webservice.v3.general.unidade._model.UnidadeEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created on 2021-05-17
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Data
@Entity
@Table(name = "empresa", schema = "public")
public class EmpresaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "logo_thumbnail_url")
    private String thumbnailLogoUrl;

    @Column(name = "data_hora_cadastro", nullable = false, columnDefinition = "default now()")
    private LocalDateTime dataHoraCadastro;

    @Column(name = "cod_auxiliar")
    private String codAuxiliar;

    @Column(name = "status_ativo", nullable = false, columnDefinition = "default true")
    private boolean ativo;

    @Column(name = "logo_consta_site_comercial", nullable = false, columnDefinition = "default false")
    private boolean logoConstaSiteComercial;

    @OneToMany(mappedBy = "empresaEntity", fetch = FetchType.LAZY, targetEntity = UnidadeEntity.class)
    private Set<UnidadeEntity> unidades;
}
