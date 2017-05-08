package br.com.zalf.prolog.gente.ranking;

/**
 * Created by jean on 31/01/16.
 * Usado para gerar o ranking geral, contém os dados do colaborador e seus indicadores para o cálculo das medalhas.
 * Atributo pontuação é a soma do valor que cada medalha equivale.
 */
public class ItemPosicao {

    private int posicao;
    private Long cpf;
    private String nome;
    private String funcao;
    private String equipe;
//    private DevolucaoCxHolder devCx;
//    private DevolucaoHlHolder devHl;
//    private DevolucaoNfHolder devNf;
//    private TempoLargadaHolder tempoLargada;
//    private TempoRotaHolder tempoRota;
//    private TempoInternoHolder tempoInterno;
//    private JornadaLiquidaHolder jornada;
//    private TrackingHolder tracking;
    private int ouro;
    private int prata;
    private int bronze;
    private int pontuacao;

    public ItemPosicao() {
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public Long getCpf() {
        return cpf;
    }

    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getEquipe() {
        return equipe;
    }

    public void setEquipe(String equipe) {
        this.equipe = equipe;
    }

//    public DevolucaoCxHolder getDevCx() {
//        return devCx;
//    }
//
//    public void setDevCx(DevolucaoCxHolder devCx) {
//        this.devCx = devCx;
//    }
//
//    public DevolucaoHlHolder getDevHl() {
//        return devHl;
//    }
//
//    public void setDevHl(DevolucaoHlHolder devHl) {
//        this.devHl = devHl;
//    }
//
//    public DevolucaoNfHolder getDevNf() {
//        return devNf;
//    }
//
//    public void setDevNf(DevolucaoNfHolder devNf) {
//        this.devNf = devNf;
//    }
//
//    public TempoLargadaHolder getTempoLargada() {
//        return tempoLargada;
//    }
//
//    public void setTempoLargada(TempoLargadaHolder tempoLargada) {
//        this.tempoLargada = tempoLargada;
//    }
//
//    public TempoRotaHolder getTempoRota() {
//        return tempoRota;
//    }
//
//    public void setTempoRota(TempoRotaHolder tempoRota) {
//        this.tempoRota = tempoRota;
//    }
//
//    public TempoInternoHolder getTempoInterno() {
//        return tempoInterno;
//    }
//
//    public void setTempoInterno(TempoInternoHolder tempoInterno) {
//        this.tempoInterno = tempoInterno;
//    }
//
//    public JornadaLiquidaHolder getJornada() {
//        return jornada;
//    }
//
//    public void setJornada(JornadaLiquidaHolder jornada) {
//        this.jornada = jornada;
//    }
//
//    public TrackingHolder getTracking() {
//        return tracking;
//    }
//
//    public void setTracking(TrackingHolder tracking) {
//        this.tracking = tracking;
//    }

    public int getOuro() {
        return ouro;
    }

    public void setOuro(int ouro) {
        this.ouro = ouro;
    }

    public int getPrata() {
        return prata;
    }

    public void setPrata(int prata) {
        this.prata = prata;
    }

    public int getBronze() {
        return bronze;
    }

    public void setBronze(int bronze) {
        this.bronze = bronze;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    @Override
    public String toString() {
        return "ItemPosicao{" +
                "posicao=" + posicao +
                ", cpf=" + cpf +
                ", nome='" + nome + '\'' +
                ", funcao='" + funcao + '\'' +
                ", equipe='" + equipe + '\'' +
//                ", devCx=" + devCx +
//                ", devHl=" + devHl +
//                ", devNf=" + devNf +
//                ", tempoLargada=" + tempoLargada +
//                ", tempoRota=" + tempoRota +
//                ", tempoInterno=" + tempoInterno +
//                ", jornada=" + jornada +
//                ", tracking=" + tracking +
                ", ouro=" + ouro +
                ", prata=" + prata +
                ", bronze=" + bronze +
                ", pontuacao=" + pontuacao +
                '}';
    }
}
