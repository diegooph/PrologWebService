package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import org.jetbrains.annotations.NotNull;

/**
 * Este objeto contém informações de pneus aplicados vindos do endpoint do cliente via integração.
 * <p>
 * Created on 11/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 * {@see protheusnepomuceno}
 */
public final class PneuEstoqueProtheusNepomuceno {
    /**
     * Código único de identificação do pneu no banco de dados.
     */
    @NotNull
    private final String codPneu;

    /**
     * Atributo alfanumérico que representa o código pelo qual o usuário identifica o pneu. Normalmente costuma-se
     * utilizar o código de fogo do pneu para identificá-lo.
     */
    @NotNull
    private final String codigoCliente;

    /**
     * Atributo alfanumérico que representa o código da empresa.
     */
    @NotNull
    private final String codEmpresaPneu;

    /**
     * Atributo alfanumérico que representa o código da unidade.
     */
    @NotNull
    private final String codUnidadePneu;

    /**
     * Valor numérico da vida atual.
     */
    @NotNull
    private final Integer vidaAtualPneu;

    /**
     * Valor numérico da vida total.
     */
    @NotNull
    private final Integer vidaTotalPneu;

    /**
     * Atributo alfanumérico que representa a pressão recomendada.
     */
    @NotNull
    private final Double pressaoRecomendadaPneu;

    /**
     * Atributo alfanumérico que representa a pressão atual.
     */
    @NotNull
    private final Double pressaoAtualPneu;

    /**
     * Medida do sulco interno do pneu, coletado pelo processo de medição. Este valor representa a medida do sulco do
     * pneu em milimetros.
     */
    @NotNull
    private final Double sulcoInternoPneu;

    /**
     * Medida do sulco central interno do pneu, coletado pelo processo de medição. Este valor representa a medida do
     * sulco do pneu em milimetros.
     */
    @NotNull
    private final Double sulcoCentralInternoPneu;

    /**
     * Medida do sulco central externo do pneu, coletado pelo processo de medição. Este valor representa a medida do
     * sulco do pneu em milimetros.
     * <p>
     * Caso o pneu possua apenas 3 sulcos, este valor será igual ao {@code sulcoCentralInterno}.
     */
    @NotNull
    private final Double sulcoCentralExternoPneu;

    /**
     * Medida do sulco externo do pneu, coletado pelo processo de medição. Este valor representa a medida do sulco do
     * pneu em milimetros.
     */
    @NotNull
    private final Double sulcoExternoPneu;

    /**
     * Atributo alfanumérico que representa o dot.
     */
    @NotNull
    private final String dotPneu;

    /**
     * Atributo alfanumérico que representa o nome do modelo.
     */
    @NotNull
    private final String nomeModeloPneu;

    /**
     * Atributo alfanumérico que representa o código do modelo.
     */
    @NotNull
    private final String codModeloPneu;

    /**
     * Valor numérico da quantidade de sulcos.
     */
    @NotNull
    private final Integer qtdSulcosModeloPneu;

    /**
     * Atributo alfanumérico que representa o nome do modelo de banda.
     */
    @NotNull
    private final String nomeModeloBanda;

    /**
     * Atributo alfanumérico que representa o código do modelo de banda.
     */
    @NotNull
    private final String codModeloBanda;

    /**
     * Valor numérico da quantidade de sulcos do modelo de banda.
     */
    @NotNull
    private final Integer qtdSulcosModeloBanda;

    public PneuEstoqueProtheusNepomuceno(@NotNull final String codPneu,
                                         @NotNull final String codigoCliente,
                                         @NotNull final String codEmpresaPneu,
                                         @NotNull final String codUnidadePneu,
                                         @NotNull final Integer vidaAtualPneu,
                                         @NotNull final Integer vidaTotalPneu,
                                         @NotNull final Double pressaoRecomendadaPneu,
                                         @NotNull final Double pressaoAtualPneu,
                                         @NotNull final Double sulcoInternoPneu,
                                         @NotNull final Double sulcoCentralInternoPneu,
                                         @NotNull final Double sulcoCentralExternoPneu,
                                         @NotNull final Double sulcoExternoPneu,
                                         @NotNull final String dotPneu,
                                         @NotNull final String nomeModeloPneu,
                                         @NotNull final String codModeloPneu,
                                         @NotNull final Integer qtdSulcosModeloPneu,
                                         @NotNull final String nomeModeloBanda,
                                         @NotNull final String codModeloBanda,
                                         @NotNull final Integer qtdSulcosModeloBanda){
        this.codPneu = codPneu;
        this.codigoCliente = codigoCliente;
        this.codEmpresaPneu = codEmpresaPneu;
        this.codUnidadePneu = codUnidadePneu;
        this.vidaAtualPneu = vidaAtualPneu;
        this.vidaTotalPneu = vidaTotalPneu;
        this.pressaoRecomendadaPneu = pressaoRecomendadaPneu;
        this.pressaoAtualPneu = pressaoAtualPneu;
        this.sulcoInternoPneu = sulcoInternoPneu;
        this.sulcoCentralInternoPneu = sulcoCentralInternoPneu;
        this.sulcoCentralExternoPneu = sulcoCentralExternoPneu;
        this.sulcoExternoPneu = sulcoExternoPneu;
        this.dotPneu = dotPneu;
        this.nomeModeloPneu = nomeModeloPneu;
        this.codModeloPneu = codModeloPneu;
        this.qtdSulcosModeloPneu = qtdSulcosModeloPneu;
        this.nomeModeloBanda = nomeModeloBanda;
        this.codModeloBanda = codModeloBanda;
        this.qtdSulcosModeloBanda = qtdSulcosModeloBanda;
    }

    @NotNull
    public static PneuEstoqueProtheusNepomuceno getPneuEstoqueDummy() {
        return new PneuEstoqueProtheusNepomuceno(
                "PN1123",
                "1000084",
                "01",
                "01",
                1,
                4,
                115.0,
                110.0,
                11.1,
                11.2,
                11.0,
                11.1,
                "0405",
                "LSU",
                "28",
                3,
                "DV-RM 226",
                "89",
                3
        );
    }

    @NotNull
    public String getCodPneu() { return codPneu; }

    @NotNull
    public String getCodigoCliente() { return codigoCliente; }

    @NotNull
    public String getCodEmpresaPneu() { return codEmpresaPneu; }

    @NotNull
    public String getCodUnidadePneu() { return codUnidadePneu; }

    @NotNull
    public Integer getVidaAtualPneu() { return vidaAtualPneu; }

    @NotNull
    public Integer getVidaTotalPneu() { return vidaTotalPneu; }

    @NotNull
    public Double getPressaoRecomendadaPneu() { return pressaoRecomendadaPneu; }

    @NotNull
    public Double getPressaoAtualPneu() { return pressaoAtualPneu; }

    @NotNull
    public Double getSulcoInternoPneu() { return sulcoInternoPneu; }

    @NotNull
    public Double getSulcoCentralInternoPneu() { return sulcoCentralInternoPneu; }

    @NotNull
    public Double getSulcoCentralExternoPneu() { return sulcoCentralExternoPneu; }

    @NotNull
    public Double getSulcoExternoPneu() { return sulcoExternoPneu; }

    @NotNull
    public String getDotPneu() { return dotPneu; }

    @NotNull
    public String getNomeModeloPneu() { return nomeModeloPneu; }

    @NotNull
    public String getCodModeloPneu() { return codModeloPneu; }

    @NotNull
    public Integer getQtdSulcosModeloPneu() { return qtdSulcosModeloPneu; }

    @NotNull
    public String getNomeModeloBanda() { return nomeModeloBanda; }

    @NotNull
    public String getCodModeloBanda() { return codModeloBanda; }

    @NotNull
    public Integer getQtdSulcosModeloBanda() { return qtdSulcosModeloBanda; }
}
