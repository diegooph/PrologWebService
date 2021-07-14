package br.com.zalf.prolog.webservice.v3.fleet.pneu._model;

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
@Table(name = "modelo_banda", schema = "public")
@Builder(toBuilder = true, setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModeloBandaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "nome", nullable = false)
    private String nome;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_marca", referencedColumnName = "codigo")
    private MarcaBandaEntity marcaBanda;
    @Column(name = "cod_empresa", nullable = false)
    private Long codEmpresa;
    @Column(name = "qt_sulcos", nullable = false, columnDefinition = "default 4")
    private Short quantidadeSulcos;
    @Column(name = "altura_sulcos", nullable = false)
    private Double alturaSulcos;
}
