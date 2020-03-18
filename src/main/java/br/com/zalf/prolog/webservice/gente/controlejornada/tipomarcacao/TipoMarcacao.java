package br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.controlejornada.model.Icone;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Time;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Zart on 18/08/2017.
 */
public class TipoMarcacao {

    private Long codigo;

    /**
     * O código desse tipo de intervalo para a unidade no qual ele existe. Diferentes unidades sempre têm seu código
     * iniciado em 1.
     */
    private Long codigoPorUnidade;
    private String nome;
    private Icone icone;
    @SerializedName("tempoRecomendadoSegundos")
    private Duration tempoRecomendado;
    private Time horarioSugerido;
    private Unidade unidade;
    private List<Cargo> cargos;
    private boolean ativo;
    @SerializedName("tempoLimiteEstouroSegundos")
    private Duration tempoLimiteEstouro;

    /**
     * Indica se esse tipo representa a jornada do colaborador.
     */
    private boolean tipoJornada;

    @Nullable
    private FormulaCalculoJornada formulaCalculoJornada;

    @Nullable
    private String codigoAuxiliar;

    public TipoMarcacao() {

    }

    @NotNull
    public static TipoMarcacao createDummy() {
        final TipoMarcacao tipoIntervalo = new TipoMarcacao();
        tipoIntervalo.setCodigo(10L);
        tipoIntervalo.setNome("Alimentação");
        tipoIntervalo.setAtivo(true);
        tipoIntervalo.setCodigoPorUnidade(2L);
        tipoIntervalo.setHorarioSugerido(Time.valueOf("10:00:00"));
        tipoIntervalo.setIcone(Icone.ALIMENTACAO);
        tipoIntervalo.setTempoLimiteEstouro(Duration.ofSeconds(10));
        tipoIntervalo.setTempoRecomendado(Duration.ofSeconds(100));

        final Unidade unidade = new Unidade();
        unidade.setNome("Nome Unidade");
        unidade.setCodigo(1L);
        tipoIntervalo.setUnidade(unidade);

        final List<Cargo> cargos = new ArrayList<>();
        cargos.add(Cargo.createDummy());
        cargos.add(Cargo.createDummy());
        tipoIntervalo.setCargos(cargos);
        return tipoIntervalo;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public Icone getIcone() {
        return icone;
    }

    public void setIcone(final Icone icone) {
        this.icone = icone;
    }

    public Duration getTempoRecomendado() {
        return tempoRecomendado;
    }

    public void setTempoRecomendado(final Duration tempoRecomendado) {
        this.tempoRecomendado = tempoRecomendado;
    }

    public Time getHorarioSugerido() {
        return horarioSugerido;
    }

    public void setHorarioSugerido(final Time horarioSugerido) {
        this.horarioSugerido = horarioSugerido;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(final Unidade unidade) {
        this.unidade = unidade;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(final List<Cargo> cargos) {
        this.cargos = cargos;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(final boolean ativo) {
        this.ativo = ativo;
    }

    public Duration getTempoLimiteEstouro() {
        return tempoLimiteEstouro;
    }

    public void setTempoLimiteEstouro(final Duration tempoLimiteEstouro) {
        this.tempoLimiteEstouro = tempoLimiteEstouro;
    }

    public Long getCodigoPorUnidade() {
        return codigoPorUnidade;
    }

    public void setCodigoPorUnidade(final Long codigoPorUnidade) {
        this.codigoPorUnidade = codigoPorUnidade;
    }

    public boolean isTipoJornada() {
        return tipoJornada;
    }

    public void setTipoJornada(final boolean tipoJornada) {
        this.tipoJornada = tipoJornada;
    }

    @Nullable
    public FormulaCalculoJornada getFormulaCalculoJornada() {
        return formulaCalculoJornada;
    }

    public void setFormulaCalculoJornada(@Nullable final FormulaCalculoJornada formulaCalculoJornada) {
        this.formulaCalculoJornada = formulaCalculoJornada;
    }

    @Nullable
    public String getCodigoAuxiliar() {
        return codigoAuxiliar;
    }

    public void setCodigoAuxiliar(@Nullable final String codigoAuxiliar) {
        this.codigoAuxiliar = codigoAuxiliar;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof TipoMarcacao)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        final TipoMarcacao tipoIntervalo = (TipoMarcacao) obj;
        return !(codigo == null || tipoIntervalo.codigo == null) && codigo.equals(tipoIntervalo.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}