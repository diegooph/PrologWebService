package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaVisualizacaoApp;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaVisualizacaoListagem;
import br.com.zalf.prolog.webservice.geral.unidade.UnidadeConverter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;

/**
 * Created on 2020-03-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class MotivoRetiradaConverter {

    private MotivoRetiradaConverter() {
        throw new IllegalStateException(UnidadeConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static MotivoRetiradaVisualizacaoListagem createMotivoVisualizacaoListagem(
            @NotNull final ResultSet rSet) throws Throwable {
        return new MotivoRetiradaVisualizacaoListagem(rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("NOME_EMPRESA"),
                rSet.getString("DESCRICAO_MOTIVO"),
                rSet.getObject("DATA_HORA_ULTIMA_ALTERACAO_MOTIVO", LocalDateTime.class),
                rSet.getString("NOME_COLABORADOR_ULTIMA_ALTERACAO"));
    }

    @NotNull
    static MotivoRetiradaVisualizacaoApp createMotivoListagemApp(@NotNull final ResultSet rSet) throws Throwable {
        return new MotivoRetiradaVisualizacaoApp(rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("DESCRICAO_MOTIVO"));
    }

}
