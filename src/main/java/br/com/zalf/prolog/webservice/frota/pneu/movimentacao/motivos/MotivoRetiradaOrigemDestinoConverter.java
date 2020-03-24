package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoRetiradaOrigemDestinoVisualizacaoListagem;
import br.com.zalf.prolog.webservice.geral.unidade.UnidadeConverter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;

/**
 * Created on 2020-03-23
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public class MotivoRetiradaOrigemDestinoConverter {

    private MotivoRetiradaOrigemDestinoConverter() {
        throw new IllegalStateException(UnidadeConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static MotivoRetiradaOrigemDestinoVisualizacaoListagem createMotivoOrigemDestinoVisualizacaoListagem(
            @NotNull final ResultSet rSet) throws Throwable {
        return new MotivoRetiradaOrigemDestinoVisualizacaoListagem(rSet.getLong("CODIGO_MOTIVO_ORIGEM_DESTINO"),
                rSet.getString("NOME_EMPRESA"),
                rSet.getString("DESCRICAO_MOTIVO"),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("ORIGEM"))),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("DESTINO"))),
                rSet.getBoolean("OBRIGATORIO"),
                rSet.getObject("DATA_HORA_ULTIMA_ALTERACAO", LocalDateTime.class),
                rSet.getString("NOME_COLABORADOR_ULTIMA_ALTERACAO"));
    }

}
