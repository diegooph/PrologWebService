package br.com.zalf.prolog.webservice.raizen.produtividade;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created on 03/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeItem {

    private Long codigo;
    private String placa;
    private BigDecimal valor;
    private String usina;
    private String fazenda;
    private double raio;
    private double tonelada;
    private Long codColaboradorCadastro;
    private Long codColaboradorAlteracao;
    private Long codEmpresa;

    public RaizenProdutividadeItem() {
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
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

    public Long getCodColaboradorCadastro() {
        return codColaboradorCadastro;
    }

    public void setCodColaboradorCadastro(Long codColaboradorCadastro) {
        this.codColaboradorCadastro = codColaboradorCadastro;
    }

    public Long getCodColaboradorAlteracao() {
        return codColaboradorAlteracao;
    }

    public void setCodColaboradorAlteracao(Long codColaboradorAlteracao) {
        this.codColaboradorAlteracao = codColaboradorAlteracao;
    }

    public Long getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(Long codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    @Override
    public String toString() {
        return "RaizenProdutividadeItem{" +
                "codigoColaboradorCadastro=" + codColaboradorCadastro +
                ", placa=" + placa +
                ", valor=" + valor +
                ", usina=" + usina +
                ", fazenda=" + fazenda +
                ", raio=" + raio +
                ", tonelada=" + tonelada +
                "}";
    }
}