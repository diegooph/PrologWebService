package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Objeto que contem as {@link InfosAfericaoAvulsa informações do pneu} e dados da última aferição.
 * <p>
 * Created on 13/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class InfosAfericaoAvulsa {
    /**
     * Atributo numérico que representa o código da última aferição.
     */
    @NotNull
    private final Long codUltimaAfericao;

    /**
     * Atributo alfanumérico que representa o código do pneu no prolog.
     */
    @Nullable
    private final String codPneuProlog;

    /**
     * Atributo alfanumérico que representa o código único do pneu no cliente.
     */
    @NotNull
    private final String codPneuCliente;

    /**
     * Atributo alfanumérico que representa o código auxiliar do pneu, normalmente contém o número de fogo.
     */
    @NotNull
    private final String codPneuAuxiliar;

    /**
     * Atributo alfanumérico que representa a data da última aferição.
     */
    @NotNull
    private final String dataHoraUltimaAfericao;

    /**
     * Atributo alfanumérico que representa o nome do último colaborador.
     */
    @NotNull
    private final String nomeColaboradorAfericao;

    /**
     * Constante alfanumérica que representa o {@link TipoMedicaoColetadaAfericao tipo de medição} que foi
     * utilizado para a captura de informações, podem ter sido utilizados 3 tipos:
     * *{@link TipoMedicaoColetadaAfericao#SULCO}
     * *{@link TipoMedicaoColetadaAfericao#PRESSAO}
     * *{@link TipoMedicaoColetadaAfericao#SULCO_PRESSAO}
     */
    @NotNull
    private final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao;

    /**
     * Constante alfanumérica que representa o {@link TipoProcessoColetaAfericao tipo de processo de coleta} que foi
     * utilizado para a captura de informações, podem ter sido utilizados 2 tipos:
     * *{@link TipoProcessoColetaAfericao#PLACA}
     * *{@link TipoProcessoColetaAfericao#PNEU_AVULSO}
     */
    @NotNull
    private final TipoProcessoColetaAfericao tipoProcessoColetaAfericao;

    /**
     * Atributo alfanumérico que representa a placa do veículo onde o pneu estava aplicado quando aferido
     */
    @Nullable
    private final String placaAplicadoQuandoAferido;

    public InfosAfericaoAvulsa(@NotNull final Long codUltimaAfericao,
                               @Nullable final String codPneuProlog,
                               @NotNull final String codPneuCliente,
                               @NotNull final String codPneuAuxiliar,
                               @NotNull final String dataHoraUltimaAfericao,
                               @NotNull final String nomeColaboradorAfericao,
                               @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao,
                               @NotNull final TipoProcessoColetaAfericao tipoProcessoColetaAfericao,
                               @Nullable final String placaAplicadoQuandoAferido) {
        this.codUltimaAfericao = codUltimaAfericao;
        this.codPneuProlog = codPneuProlog;
        this.codPneuCliente = codPneuCliente;
        this.codPneuAuxiliar = codPneuAuxiliar;
        this.dataHoraUltimaAfericao = dataHoraUltimaAfericao;
        this.nomeColaboradorAfericao = nomeColaboradorAfericao;
        this.tipoMedicaoColetadaAfericao = tipoMedicaoColetadaAfericao;
        this.tipoProcessoColetaAfericao = tipoProcessoColetaAfericao;
        this.placaAplicadoQuandoAferido = placaAplicadoQuandoAferido;
    }

    @NotNull
    public static InfosAfericaoAvulsa getInfosAfericaoAvulsaDummy(){
        return new InfosAfericaoAvulsa(
                1L,
                "1",
                "1",
                "P0001",
                "2020-03-13 08:00:00",
                "John Doe",
                TipoMedicaoColetadaAfericao.SULCO_PRESSAO,
                TipoProcessoColetaAfericao.PLACA,
                "AAA1111"
        );
    }

    @NotNull
    public Long getCodUltimaAfericao() { return codUltimaAfericao; }

    @Nullable
    public String getCodPneuProlog() { return codPneuProlog; }

    @NotNull
    public String getCodPneuCliente() { return codPneuCliente; }

    @NotNull
    public String getCodPneuAuxiliar() { return codPneuAuxiliar; }

    @NotNull
    public String getDataHoraUltimaAfericao() { return dataHoraUltimaAfericao; }

    @NotNull
    public String getNomeColaboradorAfericao() { return nomeColaboradorAfericao; }

    @NotNull
    public TipoMedicaoColetadaAfericao getTipoMedicaoColetadaAfericao() { return tipoMedicaoColetadaAfericao; }

    @NotNull
    public TipoProcessoColetaAfericao getTipoProcessoColetaAfericao() { return tipoProcessoColetaAfericao; }

    @Nullable
    public String getPlacaAplicadoQuandoAferido() { return placaAplicadoQuandoAferido; }
}
