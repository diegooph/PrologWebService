package br.com.zalf.prolog.webservice.v3.fleet.checklistworkorder._model;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Created on 2021-03-29
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public final class ChecklistWorkOrderPk implements Serializable {
    @NotNull
    private Long id;
    @NotNull
    private Long branchId;
}
