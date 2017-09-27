package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import com.google.gson.annotations.SerializedName;

import java.sql.Time;
import java.time.Duration;
import java.util.List;

/**
 * Created by Zart on 18/08/2017.
 */
public class TipoIntervalo {

    private Long codigo;
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

    public TipoIntervalo() {

    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Icone getIcone() {
        return icone;
    }

    public void setIcone(Icone icone) {
        this.icone = icone;
    }

    public Duration getTempoRecomendado() {
        return tempoRecomendado;
    }

    public void setTempoRecomendado(Duration tempoRecomendado) {
        this.tempoRecomendado = tempoRecomendado;
    }

    public Time getHorarioSugerido() {
        return horarioSugerido;
    }

    public void setHorarioSugerido(Time horarioSugerido) {
        this.horarioSugerido = horarioSugerido;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public List<Cargo> getCargos() {
        return cargos;
    }

    public void setCargos(List<Cargo> cargos) {
        this.cargos = cargos;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Duration getTempoLimiteEstouro() {
        return tempoLimiteEstouro;
    }

    public void setTempoLimiteEstouro(Duration tempoLimiteEstouro) {
        this.tempoLimiteEstouro = tempoLimiteEstouro;
    }
}