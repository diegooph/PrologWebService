package br.com.zalf.prolog.webservice;

import br.com.zalf.prolog.webservice.commons.gson.GsonUtils;
import br.com.zalf.prolog.webservice.database.DatabaseManager;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.MotivoMovimentoService;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoMovimentoEdicao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.MotivoMovimentoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao.MotivoMovimentoTransicaoService;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.insercao.MotivoMovimentoTransicaoInsercao;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.insercao.TransicaoInsercao;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    private static final int MAX_LENGTH_NOME_COLABORADOR = 20;

    public static void main(final String[] args) {
        DatabaseManager.init();
        final MotivoMovimentoService motivoMovimentoService = new MotivoMovimentoService();
        motivoMovimentoService.insert(new MotivoMovimentoInsercao(
                        3L,
                        "Motivo 3" + System.currentTimeMillis(),
                        ""),
                2272L);
        motivoMovimentoService.update(new MotivoMovimentoEdicao(
                        1L,
                        "Motivo 32" + System.currentTimeMillis(),
                        "",
                        true),
                2272L);
        motivoMovimentoService.getMotivoByCodigo(1L, ZoneId.of("UTC"));
        motivoMovimentoService.getMotivosListagem(3L, true, ZoneId.of("UTC"));
        motivoMovimentoService.getHistoricoByMotivo(1L, ZoneId.of("UTC"));

        final MotivoMovimentoTransicaoService motivoMovimentoTransicaoService = new MotivoMovimentoTransicaoService();
        final List<MotivoMovimentoTransicaoInsercao> transicoes = new ArrayList<>();
        transicoes.add(new MotivoMovimentoTransicaoInsercao(
                3L,
                215L,
                Collections.singletonList(new TransicaoInsercao(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.VEICULO, true, Collections.singletonList(1L)))
        ));
        motivoMovimentoTransicaoService.insert(transicoes, 2272L);

        System.out.println(GsonUtils.getGson().toJson(motivoMovimentoTransicaoService.getMotivosTransicaoUnidade(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.VEICULO, 215L)));

        System.out.println(GsonUtils.getGson().toJson(motivoMovimentoTransicaoService.getTransicaoVisualizacao(2L, ZoneId.of("UTC"))));

        System.out.println(GsonUtils.getGson().toJson(motivoMovimentoTransicaoService.getTransicoesExistentesByUnidade(215L)));

        System.out.println(GsonUtils.getGson().toJson(motivoMovimentoTransicaoService.getUnidadesTransicoesMotivoMovimento(2272L)));
        DatabaseManager.finish();
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    private static String formataNomeColaborador(@NotNull final String nomeColaboradorAbertura) {
        return verificaNomeMaxLength(String.join(" ", new String[]{}));
    }

    @SuppressWarnings("Duplicates")
    @NotNull
    private static String verificaNomeMaxLength(@NotNull final String nomeColaboradorAbertura) {
        if (nomeColaboradorAbertura.length() > MAX_LENGTH_NOME_COLABORADOR) {
            return nomeColaboradorAbertura.substring(0, MAX_LENGTH_NOME_COLABORADOR - 1).trim().concat(".");
        } else {
            return nomeColaboradorAbertura;
        }
    }
}