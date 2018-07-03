package br.com.zalf.prolog.webservice.raizen.produtividade;

import java.util.Date;

/**
 * Created on 03/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class Raizen {

    private Long codigo;
    private String cpf;
    private String placa;
    private Date dataViagem;
    private double valor;
    private String usina;
    private String fazenda;
    private double raio;
    private double tonelada;

    public Raizen(){
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Date getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(Date dataViagem) {
        this.dataViagem = dataViagem;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getUsina() {
        return usina;
    }

    public void setUsina(String usina) {
        this.usina = usina;
    }

    public String getFazenda() {
        return fazenda;
    }

    public void setFazenda(String fazenda) {
        this.fazenda = fazenda;
    }

    public double getRaio() {
        return raio;
    }

    public void setRaio(double raio) {
        this.raio = raio;
    }

    public double getTonelada() {
        return tonelada;
    }

    public void setTonelada(double tonelada) {
        this.tonelada = tonelada;
    }

    @Override
    public String toString() {
        return "Raizen{" +
                "codigo="+codigo+
                ", cpf="+cpf+
                ", placa="+placa+
                ", dataViagem="+dataViagem+
                ", valor="+valor+
                ", usina="+usina+
                ", fazenda="+fazenda+
                ", raio="+raio+
                ", tonelada="+tonelada+
                "}";
    }
}