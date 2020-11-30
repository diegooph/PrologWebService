package br.com.zalf.prolog.webservice.geral.unidade._model;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.gente.empresa._model.EmpresaEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created on 2020-11-24
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Entity
@NamedNativeQueries(value = {
        @NamedNativeQuery(name = "funcUnidadeVisualizacao",
                          resultSetMapping = "UnidadeVisualizacaoDtoMapper",
                          query = "select * " +
                                  "from func_unidade_visualizacao(:fCodUnidade);",
                          resultClass = UnidadeVisualizacaoDto.class),
        @NamedNativeQuery(name = "funcUnidadeListagem",
                          resultSetMapping = "UnidadeVisualizacaoDtoMapper",
                          query = "select * " +
                                  "from func_unidade_listagem(" +
                                  "F_COD_EMPRESA => :fCodEmpresa," +
                                  "F_COD_REGIONAIS => cast(" +
                                  "(string_to_array(text(:fCodRegionais), text(',')))" +
                                  "as bigint[]));",
                          resultClass = UnidadeVisualizacaoDto.class)
})
@NamedEntityGraph(name = "graph.RegionalEmpresa",
                  attributeNodes = {
                          @NamedAttributeNode("regional"),
                          @NamedAttributeNode("empresa")
                  })
@Table(name = "unidade",
       indexes = {@Index(name = "idx_unidade_cod_empresa", columnList = "codigo")})
@Data
public class UnidadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo",
            nullable = false)
    private Long codigo;

    @Column(name = "nome",
            length = 40,
            nullable = false)
    private String nome;

    @Column(name = "total_colaboradores")
    private Integer totalColaboradores;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_regional",
                foreignKey = @ForeignKey(name = "fk_unidade_regional",
                                         value = ConstraintMode.CONSTRAINT),
                referencedColumnName = "codigo",
                nullable = false)
    private RegionalEntity regional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_empresa",
                foreignKey = @ForeignKey(name = "fk_unidade_empresa",
                                         value = ConstraintMode.CONSTRAINT),
                referencedColumnName = "codigo",
                nullable = false)
    private EmpresaEntity empresa;

    @Column(name = "timezone",
            nullable = false)
    private String timezone;

    @Column(name = "data_hora_cadastro",
            nullable = false)
    private LocalDateTime dataHoraCadastro = Now.localDateTimeUtc();

    @Column(name = "status_ativo",
            nullable = false)
    private boolean ativo = true;

    @Column(name = "cod_auxiliar")
    private String codAuxiliar;

    @Column(name = "latitude_unidade")
    private String latitudeUnidade;

    @Column(name = "longitude_unidade")
    private String longitudeUnidade;

}
