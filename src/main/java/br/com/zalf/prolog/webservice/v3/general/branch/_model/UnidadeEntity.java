package br.com.zalf.prolog.webservice.v3.general.branch._model;

import br.com.zalf.prolog.webservice.v3.general.company.CompanyEntity;
import br.com.zalf.prolog.webservice.v3.general.group.GroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created on 2020-11-24
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "unidade")
public class UnidadeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "nome", length = 40, nullable = false)
    private String nome;
    @Formula(value = "(select count(c.*) from colaborador c where c.cod_unidade = codigo)")
    private Integer totalColaboradores;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_regional", referencedColumnName = "codigo")
    private GroupEntity grupo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_empresa", referencedColumnName = "codigo")
    private CompanyEntity companyEntity;
    @Column(name = "timezone", nullable = false)
    private String timezone;
    @Column(name = "data_hora_cadastro", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dataHoraCadastro;
    @Column(name = "status_ativo", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean ativo;
    @Column(name = "cod_auxiliar")
    private String codAuxiliar;
    @Column(name = "latitude_unidade")
    private String latitudeUnidade;
    @Column(name = "longitude_unidade")
    private String longitudeUnidade;
}
