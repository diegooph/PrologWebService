package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model;

/**
 * Created on 24/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoPlanilha {

    private Long codUnidade;
    private String placa;
    private Long km;
    private boolean statusAtivo;
    private String tipo;
    private String marca;
    private String modelo;
    private Long codUnidadeCadastro;
    private int linha = -1;

    public VeiculoPlanilha() {

    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Long getKm() {
        return km;
    }

    public void setKm(Long km) {
        this.km = km;
    }

    public boolean isStatusAtivo() {
        return statusAtivo;
    }

    public void setStatusAtivo(boolean statusAtivo) {
        this.statusAtivo = statusAtivo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Long getCodUnidadeCadastro() {
        return codUnidadeCadastro;
    }

    public void setCodUnidadeCadastro(Long codUnidadeCadastro) {
        this.codUnidadeCadastro = codUnidadeCadastro;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

}



