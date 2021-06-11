package br.com.zalf.prolog.webservice.v3.frota.veiculo.tipo;

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
@Table(schema = "public", name = "veiculo_tipo")
public class TipoEntity {
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "nome", nullable = false)
    private String nome;
    @Column(name = "cod_diagrama", nullable = false)
    private short cod_diagrama;
    @Column(name = "cod_empresa", nullable = false)
    private Long cod_empresa;
    @Column(name = "status_ativo", nullable = false)
    private boolean status_ativo;
    @Column(name = "cod_auxiliar", nullable = false)
    private String cod_auxiliar;
}
