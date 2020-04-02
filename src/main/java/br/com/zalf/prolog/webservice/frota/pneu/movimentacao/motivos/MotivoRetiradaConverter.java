package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaHistoricoListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaVisualizacao;
import br.com.zalf.prolog.webservice.geral.unidade.UnidadeConverter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;

/**
 * Created on 2020-03-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class MotivoRetiradaConverter {

    private MotivoRetiradaConverter() {
        throw new IllegalStateException(UnidadeConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static MotivoRetiradaVisualizacao createMotivoRetiradaVisualizacao(
            @NotNull final ResultSet rSet) throws Throwable {
        return new MotivoRetiradaVisualizacao(
                rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("DESCRICAO_MOTIVO"),
                rSet.getBoolean("ATIVO_MOTIVO"),
                rSet.getString("CODIGO_AUXILIAR"),
                rSet.getObject("DATA_HORA_ULTIMA_ALTERACAO_MOTIVO", LocalDateTime.class),
                rSet.getString("NOME_COLABORADOR_ULTIMA_ALTERACAO"));
    }

    @NotNull
    static MotivoRetiradaListagem createMotivoRetiradaListagem(@NotNull final ResultSet rSet) throws Throwable {
        return new MotivoRetiradaListagem(
                rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("DESCRICAO_MOTIVO"),
                rSet.getBoolean("ATIVO_MOTIVO"));
    }

    @NotNull
    static MotivoRetiradaHistoricoListagem createMotivoRetiradaHistoricoListagem(@NotNull final ResultSet rSet)
            throws Throwable {
        return new MotivoRetiradaHistoricoListagem(
                rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("DESCRICAO_MOTIVO"),
                rSet.getBoolean("ATIVO_MOTIVO"),
                rSet.getString("CODIGO_AUXILIAR"),
                rSet.getObject("DATA_HORA_ALTERACAO", LocalDateTime.class),
                rSet.getString("NOME_COLABORADOR_ALTERACAO"));
    }

}
