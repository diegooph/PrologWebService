package br.com.zalf.prolog.webservice.entrega.produtividade;

import br.com.zalf.prolog.webservice.entrega.indicador.item.IndicadorItem;

import java.util.Date;
import java.util.List;

/**
 * Created by jean on 09/12/15.
 * Refatorada em 19/09/2016
 * Produtividade distribuição, contém os dados de um ÚNICO mapa.
 * Usado na tela Produtividade
 */
public class ItemProdutividade {

    /*
    Tipo da carga do mapa
     */
    public enum CargaAtual{

        ROTA("Roteriz"),
        RECARGA("Recarga"),
        NOTURNA("Noturna"),
        EXTRA("Extra"),
        MKT("MKT"),
        RECOLHA("Recolha");

        private String s;

        CargaAtual(String s){
            this.s = s;
        }

        public String asString(){return s;}

        public static CargaAtual fromString(String text) throws IllegalArgumentException{
            if (text != null) {
                for (CargaAtual b : CargaAtual.values()) {
                    if (text.equalsIgnoreCase(b.s)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("Nenhum enum com esse valor encontrado");
        }
    }

    /*
    TIpo do mapa, podendo ser Rota ou AS
     */
    public enum TipoMapa{

        ROTA("Rota"),
        AS("AS");

        private String s;

        TipoMapa(String s){
            this.s = s;
        }

        public String asString(){return s;}

        public static TipoMapa fromString(String text) throws IllegalArgumentException{
            if (text != null) {
                for (TipoMapa b : TipoMapa.values()) {
                    if (text.equalsIgnoreCase(b.s)) {
                        return b;
                    }
                }
            }
            throw new IllegalArgumentException("Nenhum enum com esse valor encontrado");
        }
    }


    private Date data;
    private double valor;
    private int mapa;
    private double valorPorCaixa;
    private int cxsEntregues;
    private int fator;
    private CargaAtual cargaAtual;
    private TipoMapa tipoMapa;
    private List<IndicadorItem> indicadores;

    public ItemProdutividade() {
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public List<IndicadorItem> getIndicadores() {
        return indicadores;
    }

    public void setIndicadores(List<IndicadorItem> indicadores) {
        this.indicadores = indicadores;
    }

    public int getMapa() {
        return mapa;
    }

    public void setMapa(int mapa) {
        this.mapa = mapa;
    }

    public CargaAtual getCargaAtual() {
        return cargaAtual;
    }

    public void setCargaAtual(CargaAtual cargaAtual) {
        this.cargaAtual = cargaAtual;
    }

    public TipoMapa getTipoMapa() {
        return tipoMapa;
    }

    public void setTipoMapa(TipoMapa tipoMapa) {
        this.tipoMapa = tipoMapa;
    }

    public double getValorPorCaixa() {
        return valorPorCaixa;
    }

    public void setValorPorCaixa(double valorPorCaixa) {
        this.valorPorCaixa = valorPorCaixa;
    }

    public int getFator() {
        return fator;
    }

    public void setFator(int fator) {
        this.fator = fator;
    }

    public int getCxsEntregues() {
        return cxsEntregues;
    }

    public void setCxsEntregues(int cxsEntregues) {
        this.cxsEntregues = cxsEntregues;
    }

    @Override
    public String toString() {
        return "ItemProdutividade{" +
                "data=" + data +
                ", valor=" + valor +
                ", mapa=" + mapa +
                ", valorPorCaixa=" + valorPorCaixa +
                ", cxsEntregues=" + cxsEntregues +
                ", fator=" + fator +
                ", cargaAtual=" + cargaAtual +
                ", tipoMapa=" + tipoMapa +
                ", indicadores=" + indicadores +
                '}';
    }
}