package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import com.sun.istack.internal.Nullable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jean on 04/04/16.
 */
public class Pneu {

    public static final String ESTOQUE = "ESTOQUE";
    public static final String EM_USO = "EM_USO";
    public static final String DESCARTE = "DESCARTE";
    public static final String ANALISE = "ANALISE";

    public enum Problema{
        NUMERO_INCORRETO, PRESSAO_INDISPONIVEL
    }

    @Nullable
    private List<Problema> problemas;
    // Caso o pneu esteja com problema de NUMERO_INCORRETO (pneu instalado
    // é diferente do que esta no sistemas), enviar o codigo do pneu correto (que está
    // instalado atualmente)
    private int codPneuProblema;
    private int codigo;
    private Marca marca;
    private ModeloPneu modelo;
    private BigDecimal valor;
    // pneu só tem banda após ser recapado pela primeira vez, ou seja, vida > 1
    private Banda banda;
    private Dimensao dimensao;
    private double pressaoCorreta;
    private double pressaoAtual;
    private Sulcos sulcosPneuNovo;
    private Sulcos sulcosAtuais;
    private int vidaAtual;
    private int vidasTotal;
    private String status;

    /**
     * Usaremos um int com 3 digitos para mapear a posição de um pneu.
     *
     * Ex.: 121
     * O primeiro digito se refere ao eixo, contando a partir da dianteira, iniciando em 1, no exemplo esse seria o
     * primeiro eixo, o que controla a direção do veículo, no caso.
     * O segundo dígito indica o lado, esquerdo(1) ou direito(2), assumindo que estamos olhando o veículo na direção
     * carroceria -> cabine (sentado no bando do motorista).
     * O terceiro dígito indica se é um pneu interno(2) ou externo(1).
     *
     * Em resumo, o único dígito que pode passar de 2 é o primeiro, o segundo e terceiro serão sempre 1 ou 2.
     *
     * Obs.: Estepes serão representados sempre começando com número 9, o segundo número continua informando o lado e
     * o terceiro (interno ou externo) é ignorado.
     */
    private int posicao;

    public Pneu() {
    }

    public Banda getBanda() {
        return banda;
    }

    public void setBanda(Banda banda) {
        this.banda = banda;
    }

    public int getCodPneuProblema() {
        return codPneuProblema;
    }

    public void setCodPneuProblema(int codPneuProblema) {
        this.codPneuProblema = codPneuProblema;
    }

    public List<Problema> getProblemas() {
        return problemas;
    }

    public void setProblemas(List<Problema> problemas) {
        this.problemas = problemas;
    }

    public double getPressaoAtual() {
        return pressaoAtual;
    }

    public void setPressaoAtual(double pressaoAtual) {
        this.pressaoAtual = pressaoAtual;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public ModeloPneu getModelo() {
        return modelo;
    }

    public void setModelo(ModeloPneu modelo) {
        this.modelo = modelo;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public Dimensao getDimensao() {
        return dimensao;
    }

    public void setDimensao(Dimensao dimensao) {
        this.dimensao = dimensao;
    }

    public double getPressaoCorreta() {
        return pressaoCorreta;
    }

    public void setPressaoCorreta(double pressaoCorreta) {
        this.pressaoCorreta = pressaoCorreta;
    }

    public Sulcos getSulcosPneuNovo() {
        return sulcosPneuNovo;
    }

    public void setSulcosPneuNovo(Sulcos sulcosPneuNovo) {
        this.sulcosPneuNovo = sulcosPneuNovo;
    }

    public Sulcos getSulcosAtuais() {
        return sulcosAtuais;
    }

    public void setSulcosAtuais(Sulcos sulcosAtuais) {
        this.sulcosAtuais = sulcosAtuais;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public int getVidaAtual() {
        return vidaAtual;
    }

    public void setVidaAtual(int vidaAtual) {
        this.vidaAtual = vidaAtual;
    }

    public int getVidasTotal() {
        return vidasTotal;
    }

    public void setVidasTotal(int vidasTotal) {
        this.vidasTotal = vidasTotal;
    }

    public boolean isEstepe() {
        return posicao >= 900;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Pneu{" +
                "problemas=" + problemas +
                ", codPneuProblema=" + codPneuProblema +
                ", codigo=" + codigo +
                ", marca=" + marca +
                ", modelo=" + modelo +
                ", banda=" + banda +
                ", dimensao=" + dimensao +
                ", pressaoCorreta=" + pressaoCorreta +
                ", pressaoAtual=" + pressaoAtual +
                ", sulcosPneuNovo=" + sulcosPneuNovo +
                ", sulcosAtuais=" + sulcosAtuais +
                ", vidaAtual=" + vidaAtual +
                ", vidasTotal=" + vidasTotal +
                ", status='" + status + '\'' +
                ", posicao=" + posicao +
                '}';
    }

    public static class Dimensao {
        public long codigo;
        public int altura;
        public int largura;
        public double aro;

        public Dimensao() {
        }

        @Override
        public String toString() {
            return "Dimensao{" +
                    "codigo=" + codigo +
                    ", altura=" + altura +
                    ", largura=" + largura +
                    ", aro=" + aro +
                    '}';
        }
    }
}