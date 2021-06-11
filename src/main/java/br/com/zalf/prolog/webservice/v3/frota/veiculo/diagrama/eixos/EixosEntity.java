package br.com.zalf.prolog.webservice.v3.frota.veiculo.diagrama.eixos;

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
@Table(schema = "public", name = "veiculo_diagrama_eixos")
public class EixosEntity {
    @Column(name = "cod_diagrama", nullable = false)
    private short codDiagrama;
    @Column(name = "tipo_eixo", nullable = false)
    private char tipoEixo;
    @Column(name = "posicao", nullable = false)
    private short posicao;
    @Column(name = "qt_pneus", nullable = false)
    private short qtPneus;
    @Column(name = "eixo_direcional", nullable = false)
    private boolean eixoDirecional;
}
