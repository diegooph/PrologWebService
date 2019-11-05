package br.com.zalf.prolog.webservice.frota.pneu.relatorios._model;

/**
 * Created by jean on 13/07/16.
 */
public class ResumoServicos {

    private int dia;
    private Servicos abertos;
    private Servicos fechados;
    private Servicos acumuladoAbertos;
    private Servicos acumuladoFechados;

    public ResumoServicos() {
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public Servicos getAbertos() {
        return abertos;
    }

    public void setAbertos(Servicos abertos) {
        this.abertos = abertos;
    }

    public Servicos getFechados() {
        return fechados;
    }

    public void setFechados(Servicos fechados) {
        this.fechados = fechados;
    }

    public Servicos getAcumuladoAbertos() {
        return acumuladoAbertos;
    }

    public void setAcumuladoAbertos(Servicos acumuladoAbertos) {
        this.acumuladoAbertos = acumuladoAbertos;
    }

    public Servicos getAcumuladoFechados() {
        return acumuladoFechados;
    }

    public void setAcumuladoFechados(Servicos acumuladoFechados) {
        this.acumuladoFechados = acumuladoFechados;
    }

    @Override
    public String toString() {
        return "ResumoServicos{" +
                "dia=" + dia +
                ", abertos=" + abertos +
                ", fechados=" + fechados +
                ", acumuladoAbertos=" + acumuladoAbertos +
                ", acumuladoFechados=" + acumuladoFechados +
                '}';
    }

    public static class Servicos {

        public int calibragem;
        public int inspecao;
        public int movimentacao;

        public Servicos() {
        }

        @Override
        public String toString() {
            return "Servicos{" +
                    "calibragem=" + calibragem +
                    ", inspecao=" + inspecao +
                    ", movimentacao=" + movimentacao +
                    '}';
        }
    }
}
