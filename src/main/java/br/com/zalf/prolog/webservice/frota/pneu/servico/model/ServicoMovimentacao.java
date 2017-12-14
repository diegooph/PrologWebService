package br.com.zalf.prolog.webservice.frota.pneu.servico.model;

import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.model.ProcessoMovimentacao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;

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
    private Pneu pneuNovo;
    private Sulcos sulcosColetadosFechamento;
    private int posicaoNovoPneu;

    public ServicoMovimentacao(Pneu pneuNovo) {
        this.pneuNovo = pneuNovo;
        setTipoServico(TipoServico.MOVIMENTACAO);
    }

    public ServicoMovimentacao() {
        setTipoServico(TipoServico.MOVIMENTACAO);
    }

    public Pneu getPneuNovo() {
        return pneuNovo;
    }

    public void setPneuNovo(Pneu pneuNovo) {
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

    public int getPosicaoNovoPneu() {
        return posicaoNovoPneu;
    }

    public void setPosicaoNovoPneu(int posicaoNovoPneu) {
        this.posicaoNovoPneu = posicaoNovoPneu;
    }
}