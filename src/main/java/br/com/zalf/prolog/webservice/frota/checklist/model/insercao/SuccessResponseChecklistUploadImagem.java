package br.com.zalf.prolog.webservice.frota.checklist.model.insercao;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-05-15
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
public final class SuccessResponseChecklistUploadImagem {
    @NotNull
    private final String urlImagem;
}
