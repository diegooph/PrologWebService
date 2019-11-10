package br.com.zalf.prolog.webservice.frota.pneu.afericao._model;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 27/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public abstract class Afericao {
    private Long codigo;

    /**
     * O código da {@link Unidade} onde a aferição foi realizada.
     */
    private Long codUnidade;
    private LocalDateTime dataHora;
    private Colaborador colaborador;
    /**
     * Armazena o tempo que o colaborador levou para realizar a aferição, em milisegundos.
     */
    private long tempoRealizacaoAfericaoInMillis;

    private TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao;

    @Exclude
    @NotNull
    private final TipoProcessoColetaAfericao tipoProcessoColetaAfericao;

    public Afericao(@NotNull final TipoProcessoColetaAfericao tipoProcessoColetaAfericao) {
        this.tipoProcessoColetaAfericao = tipoProcessoColetaAfericao;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodUnidade() {
        return codUnidade;
    }

    public void setCodUnidade(final Long codUnidade) {
        this.codUnidade = codUnidade;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public long getTempoRealizacaoAfericaoInMillis() {
        return tempoRealizacaoAfericaoInMillis;
    }

    public void setTempoRealizacaoAfericaoInMillis(long tempoRealizacaoAfericaoInMillis) {
        this.tempoRealizacaoAfericaoInMillis = tempoRealizacaoAfericaoInMillis;
    }

    public TipoMedicaoColetadaAfericao getTipoMedicaoColetadaAfericao() {
        return tipoMedicaoColetadaAfericao;
    }

    public void setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) {
        this.tipoMedicaoColetadaAfericao = tipoMedicaoColetadaAfericao;
    }

    @NotNull
    public TipoProcessoColetaAfericao getTipoProcessoColetaAfericao() {
        return tipoProcessoColetaAfericao;
    }

    @NotNull
    public abstract List<Pneu> getPneusAferidos();

    @NotNull
    public static RuntimeTypeAdapterFactory<Afericao> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(Afericao.class, "tipoProcessoColetaAfericao")
                .registerSubtype(AfericaoPlaca.class, TipoProcessoColetaAfericao.PLACA.asString())
                .registerSubtype(AfericaoAvulsa.class, TipoProcessoColetaAfericao.PNEU_AVULSO.asString());
    }
}