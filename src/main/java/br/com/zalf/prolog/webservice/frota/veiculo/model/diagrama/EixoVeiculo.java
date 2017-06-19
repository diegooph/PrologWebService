package br.com.zalf.prolog.webservice.frota.veiculo.model.diagrama;

/**
 * Created by luiz on 22/05/17.
 */
public class EixoVeiculo {
    private final TipoEixoVeiculo tipoEixo;
    private final int quantidadePneus;
    private final int posicao;

    public EixoVeiculo(TipoEixoVeiculo tipoEixo, int quantidadePneus, int posicao) {
        this.tipoEixo = tipoEixo;
        this.quantidadePneus = quantidadePneus;
        this.posicao = posicao;
    }

    public TipoEixoVeiculo getTipoEixo() {
        return tipoEixo;
    }

    public int getQuantidadePneus() {
        return quantidadePneus;
    }

    public int getPosicao() {
        return posicao;
    }
}