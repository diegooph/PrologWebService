package br.com.zalf.prolog.webservice.customfields._model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para construir de forma encadeada diversas colunas específicas de cada tabela de respostas dos
 * campos personalizados.
 * Um exemplo de uso pode ser o seguinte:
 * <pre>
 *      new ColunaTabelaRespostaBuilder()
 *          .addColunaEspecifica(new ColunaTabelaResposta("cod_processo_movimentacao", codigoProcesso))
 *          .addColunaEspecifica(new ColunaTabelaResposta("cod_unidade", codigoUnidade))
 *          .getColunas()
 * </pre>
 * <p>
 * Created on 2020-03-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class ColunaTabelaRespostaBuilder {
    @NotNull
    private final List<ColunaTabelaResposta> colunasEspecificasTabelaResosta;

    public ColunaTabelaRespostaBuilder() {
        this.colunasEspecificasTabelaResosta = new ArrayList<>();
    }

    @NotNull
    public ColunaTabelaRespostaBuilder addColunaEspecifica(@NotNull final ColunaTabelaResposta coluna) {
        colunasEspecificasTabelaResosta.add(coluna);
        return this;
    }

    @NotNull
    public List<ColunaTabelaResposta> getColunas() {
        return colunasEspecificasTabelaResosta;
    }
}
