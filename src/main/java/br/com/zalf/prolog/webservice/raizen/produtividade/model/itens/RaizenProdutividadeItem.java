package br.com.zalf.prolog.webservice.raizen.produtividade.model.itens;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * Created on 03/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public abstract class RaizenProdutividadeItem {
    private Long codigo;
    private String placa;
    private boolean placaCadastrada;
    private BigDecimal valor;
    private String usina;
    private String fazenda;
    private double raio;
    private double toneladas;
    private Long codColaboradorCadastro;
    private Long codColaboradorAlteracao;
    private Long codEmpresa;

    @Exclude
    private final RaizenProdutividadeItemTipo tipo;

    public RaizenProdutividadeItem(@NotNull final RaizenProdutividadeItemTipo tipo) {
        this.tipo = tipo;
    }

    public boolean isPlacaCadastrada() {
        return placaCadastrada;
    }

    public void setPlacaCadastrada(final boolean placaCadastrada) {
        this.placaCadastrada = placaCadastrada;
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

    public double getToneladas() {
        return toneladas;
    }

    public void setToneladas(double toneladas) {
        this.toneladas = toneladas;
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
                ", toneladas=" + toneladas +
                "}";
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<RaizenProdutividadeItem> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(RaizenProdutividadeItem.class, "tipo")
                .registerSubtype(RaizenProdutividadeItemData.class, RaizenProdutividadeItemTipo.ITEM_DATA.asString())
                .registerSubtype(RaizenProdutividadeItemColaborador.class, RaizenProdutividadeItemTipo.ITEM_COLABORADOR.asString())
                .registerSubtype(RaizenProdutividadeItemVisualizacao.class, RaizenProdutividadeItemTipo.ITEM_VISUALIZACAO.asString());
    }
}