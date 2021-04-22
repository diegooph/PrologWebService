package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created on 2021-04-22
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "colaborador")
public final class ColaboradorEntity {
    @Id
    @Column(name = "cpf")
    private Long cpf;
    @Column(name = "codigo", unique = true)
    private Long codigo;
    @Column(name = "nome")
    private String nome;
}
