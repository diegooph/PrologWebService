package br.com.zalf.prolog.frota.checklist.os;

/**
 * Created by luiz on 3/18/16.
 */
public class Tempo {
    private int dia;
    private int hora;
    private int minuto;

    public Tempo(int dia, int hora, int minuto) {
        this.dia = dia;
        this.hora = hora;
        this.minuto = minuto;
    }

    public Tempo(long millis) {

        int minutes = (int) ((millis / (1000*60)) % 60);
        int hours   = (int) ((millis / (1000*60*60)) % 24);
        int days    = (int) ((millis / (1000*60*60*24) % 24));

    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }

    public int getTempoInMinutos(){
        return (((dia*24)*60) + (hora*60) + minuto);
    }

    public long getTempoInMillis(){
        return (((dia*24)*60) + (hora*60) + minuto)*60000;
    }

    @Override
    public String toString() {
        return "Tempo{" +
                "dia=" + dia +
                ", hora=" + hora +
                ", minuto=" + minuto +
                '}';
    }
}
