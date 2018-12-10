package br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.listagem;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;

/**
 * Classe utilizada para listar a Quantidade de Itens segundo às prioridades:
 * *{@link PrioridadeAlternativa#CRITICA}.
 * *{@link PrioridadeAlternativa#ALTA}.
 * *{@link PrioridadeAlternativa#BAIXA}.
 *
 * A contagem das quantidades de Itens é feito com base na {@link Veiculo#placa placa} do veículo.
 *
 * Created on 09/11/18
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class QtdItensPlacaListagem {
    /**
     * Placa do {@link Veiculo} a qual os Itens pertencem.
     */
    private String placaVeiculo;

    /**
     * Quantidade de Itens de prioridade {@link PrioridadeAlternativa#CRITICA crítica} da {@code placaVeiculo}.
     */
    private int qtdCritica;

    /**
     * Quantidade de Itens de prioridade {@link PrioridadeAlternativa#ALTA alta} da {@code placaVeiculo}.
     */
    private int qtdAlta;

    /**
     * Quantidade de Itens de prioridade {@link PrioridadeAlternativa#BAIXA baixa} da {@code placaVeiculo}.
     */
    private int qtdBaixa;

    public QtdItensPlacaListagem() {

    }

    @NotNull
    public static QtdItensPlacaListagem createDummy() {
        final QtdItensPlacaListagem listagem = new QtdItensPlacaListagem();
        listagem.setPlacaVeiculo("AAA1234");
        listagem.setQtdCritica(7);
        listagem.setQtdAlta(1);
        listagem.setQtdBaixa(3);
        return listagem;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(final String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public int getQtdCritica() {
        return qtdCritica;
    }

    public void setQtdCritica(final int qtdCritica) {
        this.qtdCritica = qtdCritica;
    }

    public int getQtdAlta() {
        return qtdAlta;
    }

    public void setQtdAlta(final int qtdAlta) {
        this.qtdAlta = qtdAlta;
    }

    public int getQtdBaixa() {
        return qtdBaixa;
    }

    public void setQtdBaixa(final int qtdBaixa) {
        this.qtdBaixa = qtdBaixa;
    }
}