package br.com.zalf.prolog.webservice.v3.frota.movimentacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created on 2021-03-25
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "movimentacao")
public final class MovimentacaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo", nullable = false)
    private Long codigo;
    @Column(name = "cod_unidade", nullable = false)
    private Long codUnidade;
    @OneToOne(mappedBy = "movimentacao")
    @PrimaryKeyJoinColumn
    private MovimentacaoOrigemEntity movimentacaoOrigem;
    @OneToOne(mappedBy = "movimentacao")
    @PrimaryKeyJoinColumn
    private MovimentacaoDestinoEntity movimentacaoDestino;
}
