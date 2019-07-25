package br.com.zalf.prolog.webservice.implantacao.conferencia.frota.veiculo.model;

/**
 * Created on 24/07/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class VeiculoPlanilha {

    private Long cod_unidade;
    private String placa;
    private Long km;
    private boolean status_ativo;
    private Long cod_tipo;
    private Long cod_modelo;
    private Long cod_unidade_cadastro;

    public Long getCod_unidade() {
        return cod_unidade;
    }

    public void setCod_unidade(Long cod_unidade) {
        this.cod_unidade = cod_unidade;
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

    public boolean isStatus_ativo() {
        return status_ativo;
    }

    public void setStatus_ativo(boolean status_ativo) {
        this.status_ativo = status_ativo;
    }

    public Long getCod_tipo() {
        return cod_tipo;
    }

    public void setCod_tipo(Long cod_tipo) {
        this.cod_tipo = cod_tipo;
    }

    public Long getCod_modelo() {
        return cod_modelo;
    }

    public void setCod_modelo(Long cod_modelo) {
        this.cod_modelo = cod_modelo;
    }

    public Long getCod_unidade_cadastro() {
        return cod_unidade_cadastro;
    }

    public void setCod_unidade_cadastro(Long cod_unidade_cadastro) {
        this.cod_unidade_cadastro = cod_unidade_cadastro;
    }
}
