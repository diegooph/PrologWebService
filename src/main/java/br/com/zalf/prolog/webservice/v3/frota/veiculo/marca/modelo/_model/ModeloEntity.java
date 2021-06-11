package br.com.zalf.prolog.webservice.v3.frota.veiculo.marca.modelo._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
@Table(schema = "public", name = "modelo_veiculo")
public class ModeloEntity {
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "nome", nullable = false)
    private String nome;
    @Column(name = "cod_marca", nullable = false)
    private Long codMarca;
    @Column(name = "cod_empresa", nullable = false)
    private Long codEmpresa;
}
