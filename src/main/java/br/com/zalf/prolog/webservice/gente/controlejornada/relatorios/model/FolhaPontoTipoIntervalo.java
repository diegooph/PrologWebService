package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Icone;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 23/04/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class FolhaPontoTipoIntervalo extends TipoMarcacao {

    /**
     * O tempo somado que o colaborador passou nesse tipo de intervalo, de acordo com o período buscado no relatório.
     */
    @SerializedName("tempoTotalTipoIntervaloSegundos")
    private Duration tempoTotalTipoIntervalo;

    /**
     * O tempo somado que o colaborador passou em horas noturnas (conforme definido pela CLT) nesse tipo de intervalo,
     * de acordo com o período buscado no relatório.
     */
    @SerializedName("tempoTotalHorasNoturnasSegundos")
    private Duration tempoTotalHorasNoturnas;

    public FolhaPontoTipoIntervalo() {

    }

    @NotNull
    public static FolhaPontoTipoIntervalo createFromTipoIntervalo(@NotNull final TipoMarcacao tipoIntervalo,
                                                                  @NotNull final Long tempoTotalTipoIntervaloSegundos,
                                                                  @NotNull final Long tempoTotalHorasNoturnasSegundos) {
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
        folhaTipo.setTempoTotalTipoIntervalo(Duration.ofSeconds(tempoTotalTipoIntervaloSegundos));
        folhaTipo.setTempoTotalHorasNoturnas(Duration.ofSeconds(tempoTotalHorasNoturnasSegundos));
        folhaTipo.setTipoJornada(tipoIntervalo.isTipoJornada());
        return folhaTipo;
    }

    @NotNull
    public static FolhaPontoTipoIntervalo getDummy() {
        final FolhaPontoTipoIntervalo folhaTipo = new FolhaPontoTipoIntervalo();
        folhaTipo.setCodigo(1L);
        folhaTipo.setCodigoPorUnidade(1L);
        folhaTipo.setAtivo(true);
        final List<Cargo> cargos = new ArrayList<>();
        cargos.add(new Cargo(180L, "Motorista"));
        folhaTipo.setCargos(cargos);
        folhaTipo.setHorarioSugerido(Time.valueOf("12:00:00"));
        folhaTipo.setIcone(Icone.JORNADA);
        folhaTipo.setNome("Jornada");
        folhaTipo.setTempoLimiteEstouro(Duration.ofHours(2));
        folhaTipo.setTempoRecomendado(Duration.ofHours(1));
        final Unidade unidade = new Unidade();
        unidade.setNome("Zalf Sistemas");
        unidade.setCodigo(5L);
        folhaTipo.setUnidade(unidade);
        folhaTipo.setTempoTotalTipoIntervalo(Duration.ofHours(6));
        folhaTipo.setTempoTotalHorasNoturnas(Duration.ofSeconds(2));
        return folhaTipo;
    }

    public Duration getTempoTotalTipoIntervalo() {
        return tempoTotalTipoIntervalo;
    }

    public void setTempoTotalTipoIntervalo(final Duration tempoTotalTipoIntervalo) {
        this.tempoTotalTipoIntervalo = tempoTotalTipoIntervalo;
    }

    public Duration getTempoTotalHorasNoturnas() {
        return tempoTotalHorasNoturnas;
    }

    public void setTempoTotalHorasNoturnas(final Duration tempoTotalHorasNoturnas) {
        this.tempoTotalHorasNoturnas = tempoTotalHorasNoturnas;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void sumTempoTotalTipoIntervalo(final long tempoTotalTipoIntervaloSegundos) {
        this.tempoTotalTipoIntervalo = tempoTotalTipoIntervalo.plusSeconds(tempoTotalTipoIntervaloSegundos);
    }

    public void sumTempoTotalHorasNoturnas(final long tempoTotalHorasNoturnasSegundos) {
        this.tempoTotalHorasNoturnas = tempoTotalHorasNoturnas.plusSeconds(tempoTotalHorasNoturnasSegundos);
    }
}