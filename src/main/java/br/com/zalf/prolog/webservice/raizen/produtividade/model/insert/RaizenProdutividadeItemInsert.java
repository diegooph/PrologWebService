package br.com.zalf.prolog.webservice.raizen.produtividade.model.insert;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created on 09/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeItemInsert {
    private Long codigo;
    private String placa;
    private BigDecimal valor;
    private String usina;
    private String fazenda;
    private double raio;
    private double toneladas;
    private Long codEmpresa;
    private Long cpfMotorista;
    private LocalDate dataViagem;

    public RaizenProdutividadeItemInsert() {

    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(final String placa) {
        this.placa = placa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(final BigDecimal valor) {
        this.valor = valor;
    }

    public String getUsina() {
        return usina;
    }

    public void setUsina(final String usina) {
        this.usina = usina;
    }

    public String getFazenda() {
        return fazenda;
    }

    public void setFazenda(final String fazenda) {
        this.fazenda = fazenda;
    }

    public double getRaio() {
        return raio;
    }

    public void setRaio(final double raio) {
        this.raio = raio;
    }

    public double getToneladas() {
        return toneladas;
    }

    public void setToneladas(final double toneladas) {
        this.toneladas = toneladas;
    }

    public Long getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(final Long codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public Long getCpfMotorista() {
        return cpfMotorista;
    }

    public void setCpfMotorista(final Long cpfMotorista) {
        this.cpfMotorista = cpfMotorista;
    }

    public LocalDate getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(final LocalDate dataViagem) {
        this.dataViagem = dataViagem;
    }
}
