package br.com.zalf.prolog.webservice.frota.pneu._model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Regional;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import com.google.common.math.DoubleMath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

import static br.com.zalf.prolog.webservice.frota.pneu._model.PrologPosicaoPneuOrdemMapper.fromPosicao;

/**
 * Created on 31/05/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class Pneu {

    public static final int DOT_LENGTH = 4;
    public static final Comparator<Pneu> POSICAO_PNEU_COMPARATOR = Comparator.comparingInt(p -> fromPosicao(p.getPosicao()));
    private static final String TAG = Pneu.class.getSimpleName();
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
    private String codigoCliente;

    /**
     * O código único (autoincrement) do pneu no sistema.
     */
    private Long codigo;

    @Deprecated
    private Marca marca;
    @Deprecated
    private ModeloPneu modelo;
    private BigDecimal valor;

    /**
     * Pneu só tem banda após ser recapado pela primeira vez, ou seja, vida > 1.
     */
    @Deprecated
    private Banda banda;
    private Dimensao dimensao;
    private double pressaoCorreta;
    private double pressaoAtual;

    /**
     * Indica se esse pneu tem informações de sulcos atuais no nosso banco de dados. Pneus que nunca
     * foram aferidos não irão possuir sulcos.
     */
    private boolean temSulcosAtuais;

    /**
     * Contém os sulcos atuais do pneu. Pode ser <code>null</code> caso ele nunca tenha sido
     * aferido antes.
     */
    @Nullable
    private Sulcos sulcosAtuais;
    private int vidaAtual;
    private int vidasTotal;

    /**
     * O status do pneu define onde ele se encontra no momento.
     */
    private StatusPneu status;

    /**
     * {@link Regional} onde o pneu se encontra.
     */
    private Long codRegionalAlocado;

    /**
     * Nome da regional onde o pneu está alocado, usado somente em listagens.
     */
    @Nullable
    private String nomeRegionalAlocado;

    /**
     * {@link Unidade} onde o pneu se encontra.
     */
    private Long codUnidadeAlocado;

    /**
     * Nome da unidade onde o pneu está alocado, usado somente em listagens.
     */
    @Nullable
    private String nomeUnidadeAlocado;
    /**
     * O código DOT gravado na lateral do pneu indica sua conformidade com os padrões de segurança e fornece dados
     * sobre a fabricação do pneu.
     */
    private String dot;

    /**
     * Usaremos um int com 3 digitos para mapear a posição de um pneu.
     * <p>
     * Ex.: 121
     * O primeiro digito se refere ao eixo, contando a partir da dianteira, iniciando em 1, no exemplo esse seria o
     * primeiro eixo, o que controla a direção do veículo, no caso.
     * O segundo dígito indica o lado, esquerdo(1) ou direito(2), assumindo que estamos olhando o veículo na direção
     * carroceria -> cabine (sentado no bando do motorista).
     * O terceiro dígito indica se é um pneu interno(2) ou externo(1).
     * <p>
     * Em resumo, o único dígito que pode passar de 2 é o primeiro, o segundo e terceiro serão sempre 1 ou 2.
     * <p>
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

    @Exclude
    @NotNull
    private PneuTipo tipo;

    public enum Problema {
        NUMERO_INCORRETO, PRESSAO_INDISPONIVEL
    }

    public Pneu(@NotNull final PneuTipo pneuTipo) {
        this.tipo = pneuTipo;
    }

    public static RuntimeTypeAdapterFactory<Pneu> provideTypeAdapterFactory() {
        final RuntimeTypeAdapterFactory<Pneu> factory = RuntimeTypeAdapterFactory
                .of(Pneu.class, "tipo");
        final PneuTipo[] values = PneuTipo.values();
        for (int i = 0; i < values.length; i++) {
            factory.registerSubtype(values[i].getClazz(), values[i].asString());
        }
        return factory;
    }

    public static boolean isDotValid(@NotNull final String dot) {
        if (dot.length() != DOT_LENGTH || !StringUtils.isIntegerValuePositive(dot)) {
            return false;
        }

        try {
            final int semanaAno = Integer.parseInt(dot.substring(0, 2));

            // Consideramos apenas os DOTs de pneus fabricados após o ano 2000. Esses possuem 2
            // caracteres para o ano.
            final int ano = Integer.parseInt(dot.substring(2, 4)) + 2000;

            Log.d(TAG, "Ano: " + ano);

            return semanaAno <= 53;
        } catch (final Exception ex) {
            Log.e(TAG, "Erro ao validar o DOT: " + dot, ex);
        }
        return false;
    }

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

    @NotNull
    public PneuTipo getTipo() {
        return tipo;
    }

    public void setTipo(@NotNull final PneuTipo tipo) {
        this.tipo = tipo;
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

    public void setFotosCadastro(@Nullable final List<PneuFotoCadastro> fotosCadastro) {
        this.fotosCadastro = fotosCadastro;
    }

    public Boolean isPneuNovoNuncaRodado() {
        return pneuNovoNuncaRodado;
    }

    public void setPneuNovoNuncaRodado(final Boolean pneuNovoNuncaRodado) {
        if (pneuNovoNuncaRodado && vidaAtual > 1) {
            throw new IllegalStateException("Um pneu não pode ao mesmo tempo ser 'novo' e ter uma vida maior do que '1'!");
        }
        this.pneuNovoNuncaRodado = pneuNovoNuncaRodado;
    }

    @Deprecated
    public Banda getBanda() {
        return banda;
    }

    public void setBanda(@Deprecated final Banda banda) {
        this.banda = banda;
    }

    public String getCodPneuProblema() {
        return codPneuProblema;
    }

    public void setCodPneuProblema(final String codPneuProblema) {
        this.codPneuProblema = codPneuProblema;
    }

    @Nullable
    public List<Problema> getProblemas() {
        return problemas;
    }

    public void setProblemas(@Nullable final List<Problema> problemas) {
        this.problemas = problemas;
    }

    public double getPressaoAtual() {
        return pressaoAtual;
    }

    public void setPressaoAtual(final double pressaoAtual) {
        this.pressaoAtual = pressaoAtual;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    @Deprecated
    public ModeloPneu getModelo() {
        return modelo;
    }

    public void setModelo(@Deprecated final ModeloPneu modelo) {
        this.modelo = modelo;
    }

    @Deprecated
    public Marca getMarca() {
        return marca;
    }

    @Deprecated
    public void setMarca(@Deprecated final Marca marca) {
        this.marca = marca;
    }

    public Dimensao getDimensao() {
        return dimensao;
    }

    public void setDimensao(final Dimensao dimensao) {
        this.dimensao = dimensao;
    }

    public double getPressaoCorreta() {
        return pressaoCorreta;
    }

    public void setPressaoCorreta(final double pressaoCorreta) {
        this.pressaoCorreta = pressaoCorreta;
    }

    public boolean isTemSulcosAtuais() {
        return temSulcosAtuais;
    }

    @Nullable
    public Sulcos getSulcosAtuais() {
        return sulcosAtuais;
    }

    public void setSulcosAtuais(@Nullable final Sulcos sulcosAtuais) {
        this.sulcosAtuais = sulcosAtuais;
        this.temSulcosAtuais = sulcosAtuais != null;
    }

    public StatusPneu getStatus() {
        return status;
    }

    public void setStatus(final StatusPneu status) {
        this.status = status;
    }

    public Long getCodRegionalAlocado() {
        return codRegionalAlocado;
    }

    public void setCodRegionalAlocado(final Long codRegionalAlocado) {
        this.codRegionalAlocado = codRegionalAlocado;
    }

    @Nullable
    public String getNomeRegionalAlocado() { return nomeRegionalAlocado; }

    public void setNomeRegionalAlocado(final String nomeRegionalAlocado) { this.nomeRegionalAlocado = nomeRegionalAlocado; }

    public Long getCodUnidadeAlocado() {
        return codUnidadeAlocado;
    }

    public void setCodUnidadeAlocado(final Long codUnidadeAlocado) {
        this.codUnidadeAlocado = codUnidadeAlocado;
    }

    @Nullable
    public String getNomeUnidadeAlocado() { return nomeUnidadeAlocado; }

    public void setNomeUnidadeAlocado(final String nomeUnidadeAlocado) { this.nomeUnidadeAlocado = nomeUnidadeAlocado; }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(final int posicao) {
        this.posicao = posicao;
    }

    public int getVidaAtual() {
        return vidaAtual;
    }

    public void setVidaAtual(final int vidaAtual) {
        if (pneuNovoNuncaRodado != null && pneuNovoNuncaRodado && vidaAtual > 1) {
            throw new IllegalStateException("Um pneu não pode ao mesmo tempo ser 'novo' e ter uma vida maior do que 1!");
        }
        this.vidaAtual = vidaAtual;
    }

    public int getVidasTotal() {
        return vidasTotal;
    }

    public void setVidasTotal(final int vidasTotal) {
        this.vidasTotal = vidasTotal;
    }

    public boolean isEstepe() {
        return posicao >= 900;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(final BigDecimal valor) {
        this.valor = valor.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public int getPressaoAtualAsInt() {
        return DoubleMath.roundToInt(pressaoAtual, RoundingMode.HALF_DOWN);
    }

    public String getDot() {
        return dot;
    }

    public void setDot(final String dot) {
        this.dot = dot;
    }

    public double getValorMenorSulcoAtual() {
        return sulcosAtuais.getMenorSulco();
    }

    public double getValorMaiorSulcoAtual() {
        return sulcosAtuais.getMaiorSulco();
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

    public void incrementaVida() {
        pneuNovoNuncaRodado = false;
        vidaAtual++;
        if (vidaAtual > vidasTotal) {
            vidasTotal = vidaAtual;
        }
    }

    /**
     * Se o pneu estiver na primeira vida, então a sua quantidade de sulcos é o atributo
     * quantidadeSulcos do modelo de pneu, senão, é o quantidadeSulcos do modelo de banda.
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

        public long getCodigo() {
            return codigo;
        }

        public void setCodigo(final long codigo) {
            this.codigo = codigo;
        }

        public int getAltura() {
            return altura;
        }

        public void setAltura(final int altura) {
            this.altura = altura;
        }

        public int getLargura() {
            return largura;
        }

        public void setLargura(final int largura) {
            this.largura = largura;
        }

        public double getAro() {
            return aro;
        }

        public void setAro(final double aro) {
            this.aro = aro;
        }
    }
}