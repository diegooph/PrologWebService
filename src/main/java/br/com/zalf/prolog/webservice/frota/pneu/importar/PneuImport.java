package br.com.zalf.prolog.webservice.frota.pneu.importar;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import com.univocity.parsers.annotations.*;
import com.univocity.parsers.conversions.EnumSelector;

import java.math.BigDecimal;

/**
 * Created on 15/09/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PneuImport {
    @Parsed(field = "codigo_cliente")
    @Validate
    private String codigoCliente;

    @Parsed(field = "cod_modelo")
    @Validate
    private Long codModeloPneu;

    @Parsed(field = "cod_dimensao")
    @Validate
    private Long codDimensao;

    @Parsed(field = "pressao_recomendada")
    private double pressaoRecomendada;

    @Parsed(field = "status")
    @EnumOptions(selectors = EnumSelector.STRING)
    private StatusPneu statusPneu;

    @Parsed(field = "cod_unidade")
    private Long codUnidadeAlocado;

    @Parsed(field = "vida_atual")
    private int vidaAtual;

    @Parsed(field = "vidas_total")
    private int vidasTotal;

    @Parsed(field = "valor_pneu")
    @Validate
    @Replace(expression = "\\R$", replacement = "")
    @Format(formats = {"#0.00"}, options = "decimalSeparator=.")
    private BigDecimal valor;

    @Parsed(field = "valor_banda")
    @Replace(expression = "\\R$", replacement = "")
    @Format(formats = {"#0.00"}, options = "decimalSeparator=.")
    private BigDecimal valorBanda;

    @Parsed(field = "cod_modelo_banda")
    private Long codModeloBanda;

    @Parsed(field = "cod_empresa")
    @Validate
    private Long codEmpresa;

    @Parsed(field = "pneu_novo_nunca_rodado")
    @BooleanString(trueStrings = {"yes", "y", "1", "TRUE", "true"}, falseStrings = {"no", "n", "0", "FALSE", "false"})
    private boolean pneuNovoNuncaRodado;

    @Validate
    private String dot;

    @Parsed(field = "dot")
    public void setDot(final String dot) {
        if(dot != null){
            if (StringUtils.isNullOrEmpty(dot) || !Pneu.isDotValid(dot)) {
                throw new IllegalArgumentException("O dot fornecido não é válido: " + dot);
            }
        }
        this.dot = dot;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public Long getCodModeloPneu() {
        return codModeloPneu;
    }

    public Long getCodDimensao() {
        return codDimensao;
    }

    public double getPressaoRecomendada() {
        return pressaoRecomendada;
    }

    public StatusPneu getStatusPneu() {
        return statusPneu;
    }

    public Long getCodUnidadeAlocado() {
        return codUnidadeAlocado;
    }

    public int getVidaAtual() {
        return vidaAtual;
    }

    public int getVidasTotal() {
        return vidasTotal;
    }

    public Long getCodModeloBanda() {
        return codModeloBanda;
    }

    public Long getCodEmpresa() {
        return codEmpresa;
    }

    public boolean isPneuNovoNuncaRodado() {
        return pneuNovoNuncaRodado;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public BigDecimal getValorBanda() {
        return valorBanda;
    }

    public String getDot() {
        return dot;
    }
}