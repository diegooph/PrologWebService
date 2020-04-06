package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao;

import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoExistenteUnidade;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoVisualizacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.MotivoMovimentoUnidade;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.TransicaoUnidadeMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.UnidadeTransicoesMotivoMovimento;
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
public final class MotivoMovimentoTransicaoConverter {

    private MotivoMovimentoTransicaoConverter() {
        throw new IllegalStateException(UnidadeConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    static TransicaoVisualizacao createMotivoRetiradaOrigemDestinoVisualizacao(
            @NotNull final ResultSet rSet) throws Throwable {
        return new TransicaoVisualizacao(
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
    static MotivoMovimentoUnidade createMotivoRetiradaListagemResumida(@NotNull final ResultSet rSet)
            throws Throwable {
        return new MotivoMovimentoUnidade(
                rSet.getLong("CODIGO_MOTIVO"),
                rSet.getString("DESCRICAO_MOTIVO"));
    }

    @NotNull
    static MotivoMovimentoUnidade createMotivoRetiradaListagem(@NotNull final ResultSet rSet) throws Throwable {
        return new MotivoMovimentoUnidade(
                rSet.getLong("codigo_motivo"),
                rSet.getString("descricao_motivo"));
    }

    @NotNull
    static TransicaoUnidadeMotivos createMotivoRetiradaOrigemDestinoListagemMotivos(
            @NotNull final ResultSet rSet) throws Throwable {
        final List<MotivoMovimentoUnidade> motivosRetirada = new ArrayList<>();
        motivosRetirada.add(MotivoMovimentoTransicaoConverter.createMotivoRetiradaListagem(rSet));

        return new TransicaoUnidadeMotivos(
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("origem_movimento"))),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("destino_movimento"))),
                motivosRetirada,
                rSet.getBoolean("obrigatorio"));
    }

    @NotNull
    static UnidadeTransicoesMotivoMovimento createMotivoRetiradaOrigemDestinoListagem(@NotNull final ResultSet rSet)
            throws Throwable {
        final List<TransicaoUnidadeMotivos> origensDestinos = new ArrayList<>();
        origensDestinos.add(MotivoMovimentoTransicaoConverter.createMotivoRetiradaOrigemDestinoListagemMotivos(rSet));

        return new UnidadeTransicoesMotivoMovimento(
                rSet.getLong("codigo_unidade"),
                rSet.getString("nome_unidade"),
                origensDestinos);
    }

    @NotNull
    static TransicaoExistenteUnidade createOrigemDestinoListagem(@NotNull final ResultSet rSet) throws Throwable {
        return new TransicaoExistenteUnidade(
                rSet.getLong("CODIGO_UNIDADE"),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("ORIGEM"))),
                OrigemDestinoEnum.getFromStatusPneu(StatusPneu.fromString(rSet.getString("destino"))),
                rSet.getBoolean("OBRIGATORIO"));
    }

}
