package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoExistenteUnidade;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.TransicaoVisualizacao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.MotivoMovimentoUnidade;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.TransicaoUnidadeMotivos;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.UnidadeTransicoesMotivoMovimento;
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
        throw new IllegalStateException(MotivoMovimentoTransicaoConverter.class.getSimpleName() + " cannot be " +
                                                "instantiated!");
    }

    @NotNull
    static TransicaoVisualizacao createTransicaoVisualizacao(
            @NotNull final ResultSet rSet) throws Throwable {
        return new TransicaoVisualizacao(
                rSet.getLong("CODIGO_MOTIVO_TRANSICAO"),
                rSet.getString("NOME_EMPRESA"),
                rSet.getString("DESCRICAO_MOTIVO"),
                OrigemDestinoEnum.fromString(rSet.getString("ORIGEM")),
                OrigemDestinoEnum.fromString(rSet.getString("DESTINO")),
                rSet.getBoolean("OBRIGATORIO"),
                rSet.getObject("DATA_HORA_ULTIMA_ALTERACAO", LocalDateTime.class),
                rSet.getString("NOME_COLABORADOR_ULTIMA_ALTERACAO"));
    }

    @NotNull
    static MotivoMovimentoUnidade createMotivoMovimentoUnidade(@NotNull final ResultSet rSet) throws Throwable {
        return new MotivoMovimentoUnidade(
                rSet.getLong("codigo_motivo"),
                rSet.getString("descricao_motivo"));
    }

    @NotNull
    static TransicaoUnidadeMotivos createTransicaoUnidadeMotivos(
            @NotNull final ResultSet rSet) throws Throwable {
        final List<MotivoMovimentoUnidade> motivosMovimento = new ArrayList<>();

        if (rSet.getLong("CODIGO_MOTIVO") != 0) {
            motivosMovimento.add(MotivoMovimentoTransicaoConverter.createMotivoMovimentoUnidade(rSet));
        }

        return new TransicaoUnidadeMotivos(
                OrigemDestinoEnum.fromString(rSet.getString("origem_movimento")),
                OrigemDestinoEnum.fromString(rSet.getString("destino_movimento")),
                motivosMovimento,
                rSet.getBoolean("obrigatorio"));
    }

    @NotNull
    static UnidadeTransicoesMotivoMovimento createUnidadeTransicoesMotivoMovimento(@NotNull final ResultSet rSet)
            throws Throwable {
        final List<TransicaoUnidadeMotivos> origensDestinos = new ArrayList<>();

        if (rSet.getString("ORIGEM_MOVIMENTO") != null) {
            origensDestinos.add(MotivoMovimentoTransicaoConverter.createTransicaoUnidadeMotivos(rSet));
        }

        return new UnidadeTransicoesMotivoMovimento(
                rSet.getLong("codigo_unidade"),
                rSet.getString("nome_unidade"),
                origensDestinos);
    }

    @NotNull
    static TransicaoExistenteUnidade createTransicaoExistenteUnidade(@NotNull final ResultSet rSet) throws Throwable {
        return new TransicaoExistenteUnidade(
                rSet.getLong("CODIGO_UNIDADE"),
                OrigemDestinoEnum.fromString(rSet.getString("ORIGEM")),
                OrigemDestinoEnum.fromString(rSet.getString("destino")),
                false);
    }

}
