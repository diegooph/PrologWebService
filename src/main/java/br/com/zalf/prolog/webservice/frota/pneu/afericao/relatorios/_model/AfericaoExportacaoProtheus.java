package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 2020-10-08
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Data
public final class AfericaoExportacaoProtheus {
    @NotNull
    private final Long codAfericao;
    @NotNull
    private final List<AfericaoExportacaoProtheusInfos> infosAfericao;
}
