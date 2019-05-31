package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model;

import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 27/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PneuAfericaoRodoparHorizonte {
    /**
     * Atributo alfanumérico que representa o código pelo qual o usuário identifica o pneu. Normalmente costuma-se
     * utilizar o código de fogo do pneu para identificá-lo.
     */
    @NotNull
    private final String codigoCliente;
    /**
     * Código único de identificação do pneu no banco de dados.
     */
    @NotNull
    private final Long codigo;
    /**
     * Código da {@link Unidade unidade} a qual o pneu está alocado.
     */
    @NotNull
    private final Long codUnidadeAlocado;
    /**
     * Número inteiro que representa a vida atual do pneu.
     * <p>
     * Para o ProLog o pneu quando novo tem {@code vidaAtual vida atual} igual a UM ({@code vidaAtual == 1}).
     */
    @NotNull
    private final Integer vidaAtual;
    /**
     * Número inteiro que representa o total de vidas do pneu. Pode-se interpretar este atributo como o total de recapes
     * que o pneu pode sofrer ao longo de toda a duração dele.
     */
    @NotNull
    private final Integer vidaTotal;
    /**
     * Número inteiro que representa a posição em que o pneu está aplicado no veículo.
     */
    @NotNull
    private final Integer posicao;
    /**
     * Valor estipulado como a pressão ideal para este pneu.
     */
    @NotNull
    private final Double pressaoCorreta;
    /**
     * Última medida de pressão capturada, ou, valor mais atual para a pressão do pneu.
     */
    @NotNull
    private final Double pressaoAtual;
    /**
     * Última medida do sulco interno capturado, ou, valor mais atual para o sulco interno do pneu.
     */
    @NotNull
    private final Double sulcoInternoAtual;
    /**
     * Última medida do sulco central interno capturado, ou, valor mais atual para o sulco central interno do pneu.
     */
    @NotNull
    private final Double sulcoCentralInternoAtual;
    /**
     * Última medida do sulco central externo capturado, ou, valor mais atual para o sulco central externo do pneu.
     */
    @NotNull
    private final Double sulcoCentralExternoAtual;
    /**
     * Última medida do sulco externo capturado, ou, valor mais atual para o sulco externo do pneu.
     */
    @NotNull
    private final Double sulcoExternoAtual;
    /**
     * Objeto que contém informações sobre o {@link ModeloPneuRodoparHorizonte modelo do pneu}.
     */
    @NotNull
    private final ModeloPneuRodoparHorizonte modeloPneu;
    /**
     * Objeto que contém informações sobre o {@link ModeloBandaRodoparHorizonte modelo da banda} aplicada no pneu.
     * <p>
     * Este atributo só será utilizado para o caso de o pneu já ter sofrido algum recape. Para o caso o pneu nunca tenha
     * sido recapado então este valor será <code>NULL</code>.
     */
    @Nullable
    private final ModeloBandaRodoparHorizonte modeloBanda;

    public PneuAfericaoRodoparHorizonte(@NotNull final String codigoCliente,
                                        @NotNull final Long codigo,
                                        @NotNull final Long codUnidadeAlocado,
                                        @NotNull final Integer vidaAtual,
                                        @NotNull final Integer vidaTotal,
                                        @NotNull final Integer posicao,
                                        @NotNull final Double pressaoCorreta,
                                        @NotNull final Double pressaoAtual,
                                        @NotNull final Double sulcoInternoAtual,
                                        @NotNull final Double sulcoCentralInternoAtual,
                                        @NotNull final Double sulcoCentralExternoAtual,
                                        @NotNull final Double sulcoExternoAtual,
                                        @NotNull final ModeloPneuRodoparHorizonte modeloPneu,
                                        @Nullable final ModeloBandaRodoparHorizonte modeloBanda) {
        this.codigoCliente = codigoCliente;
        this.codigo = codigo;
        this.codUnidadeAlocado = codUnidadeAlocado;
        this.vidaAtual = vidaAtual;
        this.vidaTotal = vidaTotal;
        this.posicao = posicao;
        this.pressaoCorreta = pressaoCorreta;
        this.pressaoAtual = pressaoAtual;
        this.sulcoInternoAtual = sulcoInternoAtual;
        this.sulcoCentralInternoAtual = sulcoCentralInternoAtual;
        this.sulcoCentralExternoAtual = sulcoCentralExternoAtual;
        this.sulcoExternoAtual = sulcoExternoAtual;
        this.modeloBanda = modeloBanda;
        this.modeloPneu = modeloPneu;
    }

    @NotNull
    public String getCodigoCliente() {
        return codigoCliente;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public Long getCodUnidadeAlocado() {
        return codUnidadeAlocado;
    }

    @NotNull
    public Integer getVidaAtual() {
        return vidaAtual;
    }

    @NotNull
    public Integer getVidaTotal() {
        return vidaTotal;
    }

    @NotNull
    public Integer getPosicao() {
        return posicao;
    }

    @NotNull
    public Double getPressaoCorreta() {
        return pressaoCorreta;
    }

    @NotNull
    public Double getPressaoAtual() {
        return pressaoAtual;
    }

    @NotNull
    public Double getSulcoInternoAtual() {
        return sulcoInternoAtual;
    }

    @NotNull
    public Double getSulcoCentralInternoAtual() {
        return sulcoCentralInternoAtual;
    }

    @NotNull
    public Double getSulcoCentralExternoAtual() {
        return sulcoCentralExternoAtual;
    }

    @NotNull
    public Double getSulcoExternoAtual() {
        return sulcoExternoAtual;
    }

    @NotNull
    public ModeloPneuRodoparHorizonte getModeloPneu() {
        return modeloPneu;
    }

    @Nullable
    public ModeloBandaRodoparHorizonte getModeloBanda() {
        return modeloBanda;
    }
}
