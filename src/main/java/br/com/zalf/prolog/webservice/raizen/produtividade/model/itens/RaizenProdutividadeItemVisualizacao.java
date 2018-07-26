package br.com.zalf.prolog.webservice.raizen.produtividade.model.itens;

import java.time.LocalDate;

/**
 * Created on 26/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class RaizenProdutividadeItemVisualizacao extends RaizenProdutividadeItem {
    private Long cpfColaborador;
    private boolean colaboradorCadastrado;
    private LocalDate dataViagem;

    public RaizenProdutividadeItemVisualizacao() {
        super(RaizenProdutividadeItemTipo.ITEM_VISUALIZACAO);
    }

    public LocalDate getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(LocalDate dataViagem) {
        this.dataViagem = dataViagem;
    }


    public Long getCpfColaborador() {
        return cpfColaborador;
    }

    public void setCpfColaborador(final Long cpfColaborador) {
        this.cpfColaborador = cpfColaborador;
    }

    public boolean isColaboradorCadastrado() {
        return colaboradorCadastrado;
    }

    public void setColaboradorCadastrado(final boolean colaboradorCadastrado) {
        this.colaboradorCadastrado = colaboradorCadastrado;
    }
}
