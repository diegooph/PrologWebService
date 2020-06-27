package br.com.zalf.prolog.webservice.frota.checklist.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 12/06/2020
 *
 * Essa classe representa a mídia adicionada na realização de um checklist, podendo ser utilizada para mídias anexadas
 * em perguntas respondidas como OK e alternativas respondidas como NOK.
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Data
public class MidiaResposta {
    @NotNull
    private final String uuid;
    @NotNull
    private final String url;
    @NotNull
    private final String tipoMidia;
}
