package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 31/03/20.
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@Data
public class PosicaoPneuMepado {
    private final int posicaoProLog;
    @NotNull
    private final String posicaoParceiro;
}
