package br.com.zalf.prolog.webservice.customfields._model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created on 2020-03-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Getter
@AllArgsConstructor
public enum CampoPersonalizadoFuncaoProlog {
    MOVIMENTACAO(14, "movimentacao_campo_personalizado_resposta");

    /**
     * Código da funcionalidade que representa movimentação.
     */
    private final int codFuncaoProlog;

    /**
     * Nome da tabela no banco de dados onde as respostas dos campos personalizados coletados em processos de
     * movimentação serão salvas.
     */
    private final String tableNameRespostas;
}
