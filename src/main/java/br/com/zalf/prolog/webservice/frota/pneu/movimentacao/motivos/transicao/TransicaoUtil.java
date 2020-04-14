package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos.transicao._model.Transicao;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2020-04-14
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class TransicaoUtil {

    public static Map<Transicao, Boolean> criaListDeTransicoesPossiveis() {
        final Map<Transicao, Boolean> transicoesUnidade = new HashMap<>();

        transicoesUnidade.put(new Transicao(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.VEICULO), false);
        transicoesUnidade.put(new Transicao(OrigemDestinoEnum.ESTOQUE, OrigemDestinoEnum.ANALISE), false);
        transicoesUnidade.put(new Transicao(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.ESTOQUE), false);
        transicoesUnidade.put(new Transicao(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.ANALISE), false);
        transicoesUnidade.put(new Transicao(OrigemDestinoEnum.VEICULO, OrigemDestinoEnum.VEICULO), false);

        return transicoesUnidade;
    }

}
