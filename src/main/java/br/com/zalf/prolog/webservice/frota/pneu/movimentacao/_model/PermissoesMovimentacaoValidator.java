package br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.permissao.Visao;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum.*;
import static br.com.zalf.prolog.webservice.permissao.pilares.Pilares.Frota.Pneu.Movimentacao.*;

/**
 * Created on 12/03/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PermissoesMovimentacaoValidator {
    @NotNull
    private static final Map<String, Integer> processoToPermissaoNecessaria;

    static {
        processoToPermissaoNecessaria = new HashMap<>();
        //-- ‘Movimentação - Veículo / Estoque (Veículo -> Estoque • Estoque -> Veículo • Veículo -> Veículo)’
        processoToPermissaoNecessaria.put(VEICULO.asString().concat(ESTOQUE.asString()), MOVIMENTAR_VEICULO_ESTOQUE);
        processoToPermissaoNecessaria.put(ESTOQUE.asString().concat(VEICULO.asString()), MOVIMENTAR_VEICULO_ESTOQUE);
        processoToPermissaoNecessaria.put(VEICULO.asString().concat(VEICULO.asString()), MOVIMENTAR_VEICULO_ESTOQUE);

        //-- ‘Movimentação - Análise (Estoque ou Veículo -> Análise • Análise -> Estoque)’
        processoToPermissaoNecessaria.put(VEICULO.asString().concat(ANALISE.asString()), MOVIMENTAR_ANALISE);
        processoToPermissaoNecessaria.put(ESTOQUE.asString().concat(ANALISE.asString()), MOVIMENTAR_ANALISE);
        processoToPermissaoNecessaria.put(ANALISE.asString().concat(ESTOQUE.asString()), MOVIMENTAR_ANALISE);

        //-- ‘Movimentação - Descarte (Estoque ou Veículo ou Análise -> Descarte)’
        processoToPermissaoNecessaria.put(ESTOQUE.asString().concat(DESCARTE.asString()), MOVIMENTAR_DESCARTE);
        processoToPermissaoNecessaria.put(VEICULO.asString().concat(DESCARTE.asString()), MOVIMENTAR_DESCARTE);
        processoToPermissaoNecessaria.put(ANALISE.asString().concat(DESCARTE.asString()), MOVIMENTAR_DESCARTE);
    }

    public void verificaMovimentacoesRealizadas(
            @NotNull final Visao permissoesColaborador,
            @NotNull final List<Movimentacao> movimentacoes) throws ProLogException {
        for (final Movimentacao movimentacao : movimentacoes) {
            final OrigemDestinoEnum tipoOrigem = movimentacao.getOrigem().getTipo();
            final OrigemDestinoEnum tipoDestino = movimentacao.getDestino().getTipo();

            final Integer codPermissaoProLog =
                    processoToPermissaoNecessaria.get(tipoOrigem.asString().concat(tipoDestino.asString()));
            if (codPermissaoProLog != null) {
                if (!permissoesColaborador.hasAccessToFunction(codPermissaoProLog)) {
                    throw new GenericException(String.format(
                            "Você não tem permissão para realizar uma movimentação do(a) %s para o(a) %s",
                            tipoOrigem.getStringPermissoesValidator(),
                            tipoDestino.getStringPermissoesValidator()));
                }
            } else {
                throw new IllegalStateException("Código de permisssão não mapeada para movimentação");
            }
        }
    }
}