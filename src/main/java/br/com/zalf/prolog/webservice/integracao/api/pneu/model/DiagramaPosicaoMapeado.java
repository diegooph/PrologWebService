package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 31/03/20.
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@Data
public class DiagramaPosicaoMapeado {
    private final int codTipoVeiculo;
    @NotNull
    private final List<PosicaoPneuMepado> posicoesMapeadas;
}
