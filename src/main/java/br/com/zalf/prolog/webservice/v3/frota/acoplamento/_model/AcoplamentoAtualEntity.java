package br.com.zalf.prolog.webservice.v3.frota.acoplamento._model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created on 2021-06-14
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Builder(toBuilder = true, setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(schema = "public", name = "veiculo_acoplamento_atual")
public class AcoplamentoAtualEntity {
    @Column(name = "cod_processo", nullable = false)
    private Long codProcesso;
    @Column(name = "cod_unidade", nullable = false)
    private Long cod_unidade;
    @Column(name = "cod_posicao", nullable = false)
    private Long cod_posicao;
    @Column(name = "cod_diagrama", nullable = false)
    private Long cod_diagrama;
    @Column(name = "motorizado", nullable = false)
    private boolean motorizado;
    @Column(name = "cod_veiculo", nullable = false)
    private Long cod_veiculo;
    @Column(name = "acoplado", nullable = false)
    private boolean acoplado;
}
