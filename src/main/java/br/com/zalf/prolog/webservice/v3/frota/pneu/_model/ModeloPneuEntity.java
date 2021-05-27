package br.com.zalf.prolog.webservice.v3.frota.pneu._model;

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
@Entity
@Table(name = "modelo_pneu", schema = "public")
@Builder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModeloPneuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "nome", nullable = false)
    private String nome;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_marca", referencedColumnName = "codigo")
    private MarcaPneuEntity marca;
    @Column(name = "cod_empresa", nullable = false)
    private Long codEmpresa;
    @Column(name = "qt_sulcos", nullable = false, columnDefinition = "default 4")
    private Short quantidadeSulcos;
    @Column(name = "altura_sulcos", nullable = false)
    private Double alturaSulcos;
}
