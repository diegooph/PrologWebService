package br.com.zalf.prolog.webservice.integracao.api.pneu.model;

import lombok.Data;

import java.util.List;

/**
 * Created on 31/03/20.
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@Data
public class DiagramaPosicaoMapeado {
    private final int codDiagrama;
    private final List<PosicaoPneuMepado> posicoesMapeadas;
}
