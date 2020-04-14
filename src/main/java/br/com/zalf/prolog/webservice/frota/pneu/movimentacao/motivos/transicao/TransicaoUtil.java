package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.MotivoMovimentoUnidade;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.listagem.TransicaoUnidadeMotivos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2020-04-14
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class TransicaoUtil {

    public static List<TransicaoUnidadeMotivos> criaListDeTransicoesPossiveis() {
        final List<TransicaoUnidadeMotivos> transicoesUnidade = new ArrayList<>();
        final List<MotivoMovimentoUnidade> motivosMovimentoVazio = new ArrayList<>();

        transicoesUnidade.add(new TransicaoUnidadeMotivos(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.VEICULO, motivosMovimentoVazio, false));
        transicoesUnidade.add(new TransicaoUnidadeMotivos(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.ANALISE, motivosMovimentoVazio, false));
        transicoesUnidade.add(new TransicaoUnidadeMotivos(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.ESTOQUE, motivosMovimentoVazio, false));
        transicoesUnidade.add(new TransicaoUnidadeMotivos(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.ANALISE, motivosMovimentoVazio, false));
        transicoesUnidade.add(new TransicaoUnidadeMotivos(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.VEICULO, motivosMovimentoVazio, false));

        return transicoesUnidade;
    }

}
