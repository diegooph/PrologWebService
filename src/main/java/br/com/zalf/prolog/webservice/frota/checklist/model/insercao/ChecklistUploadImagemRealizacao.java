package br.com.zalf.prolog.webservice.frota.checklist.model.insercao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-05-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class ChecklistUploadImagemRealizacao {
    @NotNull
    private final Long codigoChecklist;
    @Nullable
    private final Long codigoPergunta;
    @Nullable
    private final Long codigoAlternativa;
}
