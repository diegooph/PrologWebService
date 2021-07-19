package br.com.zalf.prolog.webservice.v3.fleet.attach._model;

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
public class CurrentAttachPk implements Serializable {
    @NotNull
    private Long attachProcessId;
    @NotNull
    private Short positionId;
}
