package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoMovimentoHistoricoListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoMovimentoListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoMovimentoVisualizacao;
import br.com.zalf.prolog.webservice.geral.unidade.UnidadeConverter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;

/**
 * Created on 2020-03-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class MotivoMovimentoConverter {

    private MotivoMovimentoConverter() {
        throw new IllegalStateException(UnidadeConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static MotivoMovimentoVisualizacao createMotivoMovimentoVisualizacao(
            @NotNull final ResultSet rSet) throws Throwable {
        return new MotivoMovimentoVisualizacao(
                rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("DESCRICAO_MOTIVO"),
                rSet.getString("CODIGO_AUXILIAR"),
                rSet.getObject("DATA_HORA_ULTIMA_ALTERACAO_MOTIVO", LocalDateTime.class),
                rSet.getString("NOME_COLABORADOR_ULTIMA_ALTERACAO"),
                rSet.getBoolean("ATIVO_MOTIVO"));
    }

    @NotNull
    static MotivoMovimentoListagem createMotivoMovimentoListagem(@NotNull final ResultSet rSet) throws Throwable {
        return new MotivoMovimentoListagem(
                rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("DESCRICAO_MOTIVO"),
                rSet.getString("CODIGO_AUXILIAR"),
                rSet.getBoolean("ATIVO_MOTIVO"));
    }

    @NotNull
    static MotivoMovimentoHistoricoListagem createMotivoMovimentoHistoricoListagem(@NotNull final ResultSet rSet)
            throws Throwable {
        return new MotivoMovimentoHistoricoListagem(
                rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("DESCRICAO_MOTIVO"),
                rSet.getBoolean("ATIVO_MOTIVO"),
                rSet.getString("CODIGO_AUXILIAR"),
                rSet.getObject("DATA_HORA_ALTERACAO", LocalDateTime.class),
                rSet.getString("NOME_COLABORADOR_ALTERACAO"));
    }

}
