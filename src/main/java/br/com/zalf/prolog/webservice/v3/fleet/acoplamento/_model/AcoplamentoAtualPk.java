package br.com.zalf.prolog.webservice.v3.fleet.acoplamento._model;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Created on 2021-06-16
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AcoplamentoAtualPk implements Serializable {
    @NotNull
    private Long codProcesso;
    @NotNull
    private Short codPosicao;
}
