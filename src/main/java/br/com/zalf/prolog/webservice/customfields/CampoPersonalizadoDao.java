package br.com.zalf.prolog.webservice.customfields;

import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoFuncaoProlog;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoResposta;
import br.com.zalf.prolog.webservice.customfields._model.ColunaTabelaResposta;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
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

    @NotNull
    List<CampoPersonalizadoParaRealizacao> getCamposParaRealizacaoAfericao(
            @NotNull final Long codUnidade,
            @NotNull final TipoProcessoColetaAfericao tipoProcessoColetaAfericao) throws Throwable;

    void salvaRespostasCamposPersonalizados(
            @NotNull final Connection conn,
            @NotNull final CampoPersonalizadoFuncaoProlog funcaoProlog,
            @NotNull final List<CampoPersonalizadoResposta> respostas,
            @Nullable final List<ColunaTabelaResposta> colunasEspecificas) throws Throwable;
}
