package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import br.com.zalf.prolog.webservice.geral.unidade.UnidadeConverter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    static MotivoRetiradaOrigemDestinoVisualizacao createMotivoRetiradaOrigemDestinoVisualizacao(
            @NotNull final ResultSet rSet) throws Throwable {
        return new MotivoRetiradaOrigemDestinoVisualizacao(rSet.getLong("CODIGO_MOTIVO_ORIGEM_DESTINO"),
                rSet.getString("NOME_EMPRESA"),
                rSet.getString("DESCRICAO_MOTIVO"),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("ORIGEM"))),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("DESTINO"))),
                rSet.getBoolean("OBRIGATORIO"),
                rSet.getObject("DATA_HORA_ULTIMA_ALTERACAO", LocalDateTime.class),
                rSet.getString("NOME_COLABORADOR_ULTIMA_ALTERACAO"));
    }

    @NotNull
    static MotivoRetiradaListagem createMotivoRetiradaListagem(@NotNull final ResultSet rSet) throws Throwable {
        return new MotivoRetiradaListagem(rSet.getLong("codigo_motivo"),
                rSet.getString("descricao_motivo"));
    }

    @NotNull
    static MotivoRetiradaOrigemDestinoListagemMotivos createMotivoRetiradaOrigemDestinoListagemMotivos(@NotNull final ResultSet rSet) throws Throwable {
        final List<MotivoRetiradaListagem> motivosRetirada = new ArrayList();
        motivosRetirada.add(MotivoRetiradaOrigemDestinoConverter.createMotivoRetiradaListagem(rSet));

        return new MotivoRetiradaOrigemDestinoListagemMotivos(
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("origem_movimento"))),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("destino_movimento"))),
                motivosRetirada,
                rSet.getBoolean("obrigatorio"));
    }

    @NotNull
    static MotivoRetiradaOrigemDestinoListagem createMotivoRetiradaOrigemDestinoListagem(@NotNull final ResultSet rSet) throws Throwable {
        final List<MotivoRetiradaOrigemDestinoListagemMotivos> origensDestinos = new ArrayList();
        origensDestinos.add(MotivoRetiradaOrigemDestinoConverter.createMotivoRetiradaOrigemDestinoListagemMotivos(rSet));

        return new MotivoRetiradaOrigemDestinoListagem(
                rSet.getLong("codigo_unidade"),
                rSet.getString("nome_unidade"),
                origensDestinos);
    }

    @NotNull
    static OrigemDestinoListagem createOrigemDestinoListagem(@NotNull final ResultSet rSet) throws Throwable {
        return new OrigemDestinoListagem(rSet.getLong("CODIGO_UNIDADE"),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("ORIGEM"))),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("destino"))));
    }

}
