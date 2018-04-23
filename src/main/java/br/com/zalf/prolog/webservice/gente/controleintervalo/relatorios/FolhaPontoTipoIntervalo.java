package br.com.zalf.prolog.webservice.gente.controleintervalo.relatorios;

import br.com.zalf.prolog.webservice.gente.controleintervalo.model.TipoIntervalo;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Created on 23/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FolhaPontoTipoIntervalo extends TipoIntervalo {

    /**
     * O tempo somado que o colaborador passou nesse tipo de intervalo, de acordo com o período buscado no relatório.
     */
    @SerializedName("tempoTotalTipoIntervaloSegundos")
    private Duration tempoTotalTipoIntervalo;

    public FolhaPontoTipoIntervalo() {

    }

    @NotNull
    public static FolhaPontoTipoIntervalo createFromTipoIntervalo(@NotNull final TipoIntervalo tipoIntervalo) {
        final FolhaPontoTipoIntervalo folhaTipo = new FolhaPontoTipoIntervalo();
        folhaTipo.setCodigo(tipoIntervalo.getCodigo());
        folhaTipo.setCodigoPorUnidade(tipoIntervalo.getCodigoPorUnidade());
        folhaTipo.setAtivo(tipoIntervalo.isAtivo());
        folhaTipo.setCargos(tipoIntervalo.getCargos());
        folhaTipo.setHorarioSugerido(tipoIntervalo.getHorarioSugerido());
        folhaTipo.setIcone(tipoIntervalo.getIcone());
        folhaTipo.setNome(tipoIntervalo.getNome());
        folhaTipo.setTempoLimiteEstouro(tipoIntervalo.getTempoLimiteEstouro());
        folhaTipo.setTempoRecomendado(tipoIntervalo.getTempoRecomendado());
        folhaTipo.setUnidade(tipoIntervalo.getUnidade());
        return folhaTipo;
    }

    public Duration getTempoTotalTipoIntervalo() {
        return tempoTotalTipoIntervalo;
    }

    public void setTempoTotalTipoIntervalo(final Duration tempoTotalTipoIntervalo) {
        this.tempoTotalTipoIntervalo = tempoTotalTipoIntervalo;
    }
}