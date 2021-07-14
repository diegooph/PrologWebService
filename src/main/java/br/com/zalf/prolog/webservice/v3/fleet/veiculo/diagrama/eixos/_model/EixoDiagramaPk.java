package br.com.zalf.prolog.webservice.v3.fleet.veiculo.diagrama.eixos._model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EixoDiagramaPk implements Serializable {
    private short codDiagrama;
    private short posicao;
}
