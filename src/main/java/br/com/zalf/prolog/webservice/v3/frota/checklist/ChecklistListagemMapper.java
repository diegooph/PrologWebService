package br.com.zalf.prolog.webservice.v3.frota.checklist;

import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistListagemDto;
import br.com.zalf.prolog.webservice.v3.frota.checklist._model.ChecklistProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2021-04-07
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@Component
public class ChecklistListagemMapper {

    @NotNull
    public List<ChecklistListagemDto> toDto(final List<ChecklistProjection> checklistsProjection) {
        return checklistsProjection
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @NotNull
    public ChecklistListagemDto toDto(@NotNull final ChecklistProjection checklistProjection) {
        return null;
    }
}
