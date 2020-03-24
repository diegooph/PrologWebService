package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoVisualizacaoApp;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoVisualizacaoListagem;
import br.com.zalf.prolog.webservice.geral.unidade.UnidadeConverter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;

/**
 * Created on 2020-03-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class MotivoConverter {

    private MotivoConverter() {
        throw new IllegalStateException(UnidadeConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static MotivoVisualizacaoListagem createMotivoVisualizacaoListagem(
            @NotNull final ResultSet rSet) throws Throwable {
        return new MotivoVisualizacaoListagem(rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("NOME_EMPRESA"),
                rSet.getString("DESCRICAO_MOTIVO"),
                rSet.getObject("DATA_HORA_ULTIMA_ALTERACAO_MOTIVO", LocalDateTime.class),
                rSet.getString("NOME_COLABORADOR_ULTIMA_ALTERACAO"));
    }

    @NotNull
    static MotivoVisualizacaoApp createMotivoListagemApp(@NotNull final ResultSet rSet) throws Throwable {
        return new MotivoVisualizacaoApp(rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("DESCRICAO_MOTIVO"));
    }

}
