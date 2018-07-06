package br.com.zalf.prolog.webservice.raizen.produtividade;

import java.time.LocalDate;

/**
 * Created on 03/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeItem {

    private Long codigo;
    private Long cpfMotorista;
    private String placa;
    private LocalDate dataViagem;
    private double valor;
    private String usina;
    private String fazenda;
    private double raio;
    private double tonelada;
    private Long codigoColaboradorCadastro;
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

    public Long getCpfMotorista() {
        return cpfMotorista;
    }

    public void setCpfMotorista(Long cpfMotorista) {
        this.cpfMotorista = cpfMotorista;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public LocalDate getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(LocalDate dataViagem) {
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

    public Long getCodigoColaboradorCadastro() {
        return codigoColaboradorCadastro;
    }

    public void setCodigoColaboradorCadastro(Long codigoColaboradorCadastro) {
        this.codigoColaboradorCadastro = codigoColaboradorCadastro;
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
                "codigoColaboradorCadastro=" + codigoColaboradorCadastro +
                ", cpfMotorista=" + cpfMotorista +
                ", placa=" + placa +
                ", dataViagem=" + dataViagem +
                ", valor=" + valor +
                ", usina=" + usina +
                ", fazenda=" + fazenda +
                ", raio=" + raio +
                ", tonelada=" + tonelada +
                "}";
    }
}