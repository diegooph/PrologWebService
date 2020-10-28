package br.com.zalf.prolog.webservice.frota.checklist.model.alteracao_logica;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created on 2020-10-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Data(staticConstructor = "of")
public class ChecklistsAlteracaoAcaoData {
    @NotNull
    @NotEmpty(message = "A lista de checklists não pode ser vazia.")
    private final List<@NotNull(message = "a lista de checklists não deve conter elementos nulos.") Long> codigos;
    @NotNull
    private final ChecklistAlteracaoAcao acaoExecutada;
    @Nullable
    private final String observacao;
}
