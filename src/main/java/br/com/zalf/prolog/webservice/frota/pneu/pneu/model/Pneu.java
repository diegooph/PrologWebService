package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import br.com.zalf.prolog.webservice.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.colaborador.model.Regional;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import com.google.common.math.DoubleMath;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

import static br.com.zalf.prolog.webservice.commons.util.ProLogPosicaoPneuOrdemMapper.fromPosicao;

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
    // é diferente do que esta no sistema), enviar o codigo do pneu correto (que está
    // instalado atualmente).
    private String codPneuProblema;

    /**
     * O código único do pneu a nível de {@link Empresa} que o cliente escolhe ao cadastrar um pneu.
     * Esse código é equivalente ao número de fogo do pneu.
     */
    public String codigoCliente;

    /**
     * O código único (autoincrement) do pneu no sistema.
     */
    public Long codigo;

    private Marca marca;
    private ModeloPneu modelo;
    private BigDecimal valor;

    /**
     * Pneu só tem banda após ser recapado pela primeira vez, ou seja, vida > 1.
     */
    private Banda banda;
    private Dimensao dimensao;
    private double pressaoCorreta;
    private double pressaoAtual;
    private Sulcos sulcosAtuais;
    private int vidaAtual;
    private int vidasTotal;

    /**
     * O status do pneu define onde ele se encontra no momento. Precisamos utilizar o {@link Exclude} para a
     * serialização/desserialização das subclasses funcionar corretamente utilizando o {@link Gson}.
     */
    @Exclude
    private StatusPneu status;

    /**
     * {@link Regional} onde o pneu se encontra.
     */
    private Long codRegionalAlocado;

    /**
     * {@link Unidade} onde o pneu se encontra.
     */
    private Long codUnidadeAlocado;

    /**
     * O código DOT gravado na lateral do pneu indica sua conformidade com os padrões de segurança e fornece dados
     * sobre a fabricação do pneu.
     */
    private String dot;

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

    /**
     * Indica se esse pneu nunca rodou e nem nunca foi aplicado a nenhum veículo.
     */
    private Boolean pneuNovoNuncaRodado;

    /**
     * Fotos que foram capturadas no momento do cadastro deste Pneu.
     */
    @Nullable
    private List<PneuFotoCadastro> fotosCadastro;

    public Pneu() {

    }

    public Pneu(@NotNull final StatusPneu statusPneu) {
        this.status = statusPneu;
    }

    public static RuntimeTypeAdapterFactory<Pneu> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(Pneu.class, "status")
                .registerSubtype(PneuAnalise.class, StatusPneu.ANALISE.asString());
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(final String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public List<PneuFotoCadastro> getFotosCadastro() {
        return fotosCadastro;
    }

    public void setFotosCadastro(final List<PneuFotoCadastro> fotosCadastro) {
        this.fotosCadastro = fotosCadastro;
    }

    public Boolean isPneuNovoNuncaRodado() {
        return pneuNovoNuncaRodado;
    }

    public void setPneuNovoNuncaRodado(Boolean pneuNovoNuncaRodado) {
        if (pneuNovoNuncaRodado && vidaAtual > 1) {
            throw new IllegalStateException("Um pneu não pode ao mesmo tempo ser 'novo' e ter uma vida maior do que '1'!");
        }
        this.pneuNovoNuncaRodado = pneuNovoNuncaRodado;
    }

    public Banda getBanda() {
        return banda;
    }

    public void setBanda(Banda banda) {
        this.banda = banda;
    }

    public String getCodPneuProblema() {
        return codPneuProblema;
    }

    public void setCodPneuProblema(String codPneuProblema) {
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

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
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

    public Sulcos getSulcosAtuais() {
        return sulcosAtuais;
    }

    public void setSulcosAtuais(Sulcos sulcosAtuais) {
        this.sulcosAtuais = sulcosAtuais;
    }

    public StatusPneu getStatus() {
        return status;
    }

    public void setStatus(StatusPneu status) {
        this.status = status;
    }

    public Long getCodRegionalAlocado() {
        return codRegionalAlocado;
    }

    public void setCodRegionalAlocado(Long codRegionalAlocado) {
        this.codRegionalAlocado = codRegionalAlocado;
    }

    public Long getCodUnidadeAlocado() {
        return codUnidadeAlocado;
    }

    public void setCodUnidadeAlocado(Long codUnidadeAlocado) {
        this.codUnidadeAlocado = codUnidadeAlocado;
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
        if (pneuNovoNuncaRodado != null && pneuNovoNuncaRodado && vidaAtual > 1) {
            throw new IllegalStateException("Um pneu não pode ao mesmo tempo ser 'novo' e ter uma vida maior do que 1!");
        }
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
        this.valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public int getPressaoAtualAsInt() {
        return DoubleMath.roundToInt(pressaoAtual, RoundingMode.HALF_DOWN);
    }

    public String getDot() {
        return dot;
    }

    public void setDot(String dot) {
        this.dot = dot;
    }

    public double getValorMenorSulcoAtual() {
        return sulcosAtuais.getMenorSulco();
    }

    /**
     * Se o pneu estiver na primeira vida, então a sua quantidade de sulcos é o atributo
     * {@link ModeloPneu#quantidadeSulcos}, senão, é o {@link ModeloBanda#quantidadeSulcos}.
     *
     * @return a quantidade de sulcos desse pneu.
     */
    private int getQuantidadeSulcos() {
        if (vidaAtual == 1) {
            return modelo.getQuantidadeSulcos();
        } else {
            return banda.getModelo().getQuantidadeSulcos();
        }
    }

    public boolean jaFoiRecapado() {
        return vidaAtual > 1;
    }

    public Double getAlturaSulcoBandaPneu() {
        return banda.getModelo().getAlturaSulcos();
    }

    public BigDecimal getValorBanda() {
        return banda.getValor();
    }

    public Long getCodModeloBanda() {
        return banda.getModelo().getCodigo();
    }

    public boolean temQtdImparSulcos() {
        return getQuantidadeSulcos() % 2 != 0;
    }

    public static final Comparator<Pneu> POSICAO_PNEU_COMPARATOR = Comparator.comparingInt(p -> fromPosicao(p.getPosicao()));

    @Override
    public String toString() {
        return "Pneu{" +
                "problemas=" + problemas +
                ", codPneuProblema='" + codPneuProblema + '\'' +
                ", codigoCliente='" + codigoCliente + '\'' +
                ", codigo=" + codigo +
                ", marca=" + marca +
                ", modelo=" + modelo +
                ", valor=" + valor +
                ", banda=" + banda +
                ", dimensao=" + dimensao +
                ", pressaoCorreta=" + pressaoCorreta +
                ", pressaoAtual=" + pressaoAtual +
                ", sulcosAtuais=" + sulcosAtuais +
                ", vidaAtual=" + vidaAtual +
                ", vidasTotal=" + vidasTotal +
                ", status=" + status +
                ", codRegionalAlocado=" + codRegionalAlocado +
                ", codUnidadeAlocado=" + codUnidadeAlocado +
                ", dot='" + dot + '\'' +
                ", posicao=" + posicao +
                ", pneuNovoNuncaRodado=" + pneuNovoNuncaRodado +
                ", fotosCadastro=" + fotosCadastro +
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