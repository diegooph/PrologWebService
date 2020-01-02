package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuEstoque;
import br.com.zalf.prolog.webservice.frota.pneu._model.PneuTipo;
import br.com.zalf.prolog.webservice.frota.pneu._model.Sulcos;

/**
 * Created by jean on 04/04/16.
 */
public final class ServicoMovimentacao extends Servico {
    /**
     * Quando um serviço de movimentação é fechado, um {@link ProcessoMovimentacao} é automaticamente
     * criado e inserido no banco para representar o fechamento desse serviço. Se o serviço já estiver
     * fechado, ele possuirá um código de processo diferente de {@code null}.
     */
    private Long codProcessoMovimentacao;

    /**
     * Utilizamos um {@link PneuEstoque PneuEstoque} pois o App quando envia esse pneu no fechamento do serviço envia
     * com o tipo {@link PneuTipo#PNEU_ESTOQUE estoque} já que o pneu é pré-selecionado de uma listagem de pneus em
     * estoque.
     */
    private PneuEstoque pneuNovo;
    private Sulcos sulcosColetadosFechamento;

    public ServicoMovimentacao(PneuEstoque pneuNovo) {
        this.pneuNovo = pneuNovo;
        setTipoServico(TipoServico.MOVIMENTACAO);
    }

    public ServicoMovimentacao() {
        setTipoServico(TipoServico.MOVIMENTACAO);
    }

    public PneuEstoque getPneuNovo() {
        return pneuNovo;
    }

    public void setPneuNovo(PneuEstoque pneuNovo) {
        this.pneuNovo = pneuNovo;
    }

    public Long getCodProcessoMovimentacao() {
        return codProcessoMovimentacao;
    }

    public void setCodProcessoMovimentacao(Long codProcessoMovimentacao) {
        this.codProcessoMovimentacao = codProcessoMovimentacao;
    }

    public Sulcos getSulcosColetadosFechamento() {
        return sulcosColetadosFechamento;
    }

    public void setSulcosColetadosFechamento(Sulcos sulcosColetadosFechamento) {
        this.sulcosColetadosFechamento = sulcosColetadosFechamento;
    }
}