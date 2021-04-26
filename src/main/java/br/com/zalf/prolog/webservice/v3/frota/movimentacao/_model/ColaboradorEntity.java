package br.com.zalf.prolog.webservice.v3.frota.movimentacao._model;

import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_unidade")
    private UnidadeEntity unidade;
}
