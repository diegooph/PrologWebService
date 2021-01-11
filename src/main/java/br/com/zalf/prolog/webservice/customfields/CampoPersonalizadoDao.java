package br.com.zalf.prolog.webservice.customfields;

import br.com.zalf.prolog.webservice.customfields._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;

/**
 * Created on 2020-03-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public interface CampoPersonalizadoDao {

    @NotNull
    List<CampoPersonalizadoParaRealizacao> getCamposParaRealizacaoMovimentacao(
            @NotNull final Long codUnidade) throws Throwable;
    
    void salvaRespostasCamposPersonalizados(
            @NotNull final Connection conn,
            @NotNull final CampoPersonalizadoFuncaoProlog funcaoProlog,
            @NotNull final List<CampoPersonalizadoResposta> respostas,
            @Nullable final List<ColunaTabelaResposta> colunasEspecificas) throws Throwable;
}
