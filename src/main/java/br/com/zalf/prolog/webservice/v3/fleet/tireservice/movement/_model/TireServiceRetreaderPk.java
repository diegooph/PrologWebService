package br.com.zalf.prolog.webservice.v3.fleet.tireservice.movement._model;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Created on 2021-06-28
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TireServiceRetreaderPk implements Serializable {
    @NotNull
    private Long tireMovementId;
    @NotNull
    private Long tireServiceMovementId;
    @NotNull
    private Long retreaderId;
}
