package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.OrigemDestinoListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoListagem;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoListagemMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoListagemResumida;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.origemdestino._model.MotivoMovimentoOrigemDestinoVisualizacao;
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
public final class MotivoMovimentoOrigemDestinoConverter {

    private MotivoMovimentoOrigemDestinoConverter() {
        throw new IllegalStateException(UnidadeConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static MotivoMovimentoOrigemDestinoVisualizacao createMotivoRetiradaOrigemDestinoVisualizacao(
            @NotNull final ResultSet rSet) throws Throwable {
        return new MotivoMovimentoOrigemDestinoVisualizacao(
                rSet.getLong("CODIGO_MOTIVO_ORIGEM_DESTINO"),
                rSet.getString("NOME_EMPRESA"),
                rSet.getString("DESCRICAO_MOTIVO"),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("ORIGEM"))),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("DESTINO"))),
                rSet.getBoolean("OBRIGATORIO"),
                rSet.getObject("DATA_HORA_ULTIMA_ALTERACAO", LocalDateTime.class),
                rSet.getString("NOME_COLABORADOR_ULTIMA_ALTERACAO"));
    }

    @NotNull
    static MotivoMovimentoOrigemDestinoListagemResumida createMotivoRetiradaListagemResumida(@NotNull final ResultSet rSet)
            throws Throwable {
        return new MotivoMovimentoOrigemDestinoListagemResumida(
                rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("DESCRICAO_MOTIVO"));
    }

    @NotNull
    static MotivoMovimentoOrigemDestinoListagemResumida createMotivoRetiradaListagem(@NotNull final ResultSet rSet) throws Throwable {
        return new MotivoMovimentoOrigemDestinoListagemResumida(
                rSet.getLong("codigo_motivo"),
                rSet.getString("descricao_motivo"));
    }

    @NotNull
    static MotivoMovimentoOrigemDestinoListagemMotivos createMotivoRetiradaOrigemDestinoListagemMotivos(
            @NotNull final ResultSet rSet) throws Throwable {
        final List<MotivoMovimentoOrigemDestinoListagemResumida> motivosRetirada = new ArrayList<>();
        motivosRetirada.add(MotivoMovimentoOrigemDestinoConverter.createMotivoRetiradaListagem(rSet));

        return new MotivoMovimentoOrigemDestinoListagemMotivos(
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("origem_movimento"))),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("destino_movimento"))),
                motivosRetirada,
                rSet.getBoolean("obrigatorio"));
    }

    @NotNull
    static MotivoMovimentoOrigemDestinoListagem createMotivoRetiradaOrigemDestinoListagem(@NotNull final ResultSet rSet)
            throws Throwable {
        final List<MotivoMovimentoOrigemDestinoListagemMotivos> origensDestinos = new ArrayList<>();
        origensDestinos.add(MotivoMovimentoOrigemDestinoConverter.createMotivoRetiradaOrigemDestinoListagemMotivos(rSet));

        return new MotivoMovimentoOrigemDestinoListagem(
                rSet.getLong("codigo_unidade"),
                rSet.getString("nome_unidade"),
                origensDestinos);
    }

    @NotNull
    static OrigemDestinoListagem createOrigemDestinoListagem(@NotNull final ResultSet rSet) throws Throwable {
        return new OrigemDestinoListagem(
                rSet.getLong("CODIGO_UNIDADE"),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("ORIGEM"))),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("destino"))),
                rSet.getBoolean("OBRIGATORIO"));
    }

}
