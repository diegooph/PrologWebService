package br.com.zalf.prolog.webservice.v3.frota.checklistordemservico._model;

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
public final class ChecklistOrdemServicoPk implements Serializable {
    @NotNull
    private Long codOrdemServico;
    @NotNull
    private Long codUnidade;
}
