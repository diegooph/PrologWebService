package br.com.zalf.prolog.webservice.v3.fleet.tire._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    private Long id;
    @Column(name = "nome", nullable = false)
    @NotNull
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_marca", referencedColumnName = "codigo")
    @NotNull
    private TreadBrandEntity treadBrandEntity;
    @Column(name = "cod_empresa", nullable = false)
    @NotNull
    private Long companyId;
    @Column(name = "qt_sulcos", nullable = false, columnDefinition = "default 4")
    @NotNull
    private Short groovesQuantity;
    @Column(name = "altura_sulcos", nullable = false)
    @NotNull
    private Double groovesWidth;
}
