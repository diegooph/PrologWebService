package br.com.zalf.prolog.webservice.v3.fleet.tire._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@Table(name = "modelo_banda", schema = "public")
public final class TreadModelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long id;
    @Column(name = "nome", nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_marca", referencedColumnName = "codigo")
    private TreadBrandEntity treadBrandEntity;
    @Column(name = "cod_empresa", nullable = false)
    private Long companyId;
    @Column(name = "qt_sulcos", nullable = false, columnDefinition = "default 4")
    private Short groovesQuantity;
    @Column(name = "altura_sulcos", nullable = false)
    private Double groovesWidth;
}
