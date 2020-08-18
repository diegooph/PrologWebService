package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.visualizacao.item;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 2020-07-16
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public class ImagemVinculadaChecklistVisualizacao {
    @NotNull
    private final Long codChecklist;
    @NotNull
    private final String urlImagem;
}
