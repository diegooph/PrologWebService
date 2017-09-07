package br.com.zalf.prolog.webservice.gente.controleintervalo.model;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;

public class IntervaloOfflineSupport {
    public static final String HEADER_NAME_VERSAO_DADOS_INTERVALO = "ProLog-Versao-Dados-Intervalo";

    @Nullable
    private List<Colaborador> colaboradores;

    @Nullable
    private List<TipoIntervalo> tiposIntervalo;

    @Nullable
    private Long versaoDadosIntervalo;

    @NotNull
    private EstadoIntervaloSupport estadoIntervaloSupport;

    public IntervaloOfflineSupport(@NotNull EstadoIntervaloSupport estadoIntervaloSupport) {
        this.estadoIntervaloSupport = estadoIntervaloSupport;
    }

    public List<Colaborador> getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(List<Colaborador> colaboradores) {
        this.colaboradores = colaboradores;
    }

    public List<TipoIntervalo> getTiposIntervalo() {
        return tiposIntervalo;
    }

    public void setTiposIntervalo(List<TipoIntervalo> tiposIntervalo) {
        this.tiposIntervalo = tiposIntervalo;
    }

    public Long getVersaoDadosIntervalo() {
        return versaoDadosIntervalo;
    }

    public void setVersaoDadosIntervalo(Long versaoDadosIntervalo) {
        this.versaoDadosIntervalo = versaoDadosIntervalo;
    }

    public EstadoIntervaloSupport getEstadoIntervaloSupport() {
        return estadoIntervaloSupport;
    }

    public void setEstadoIntervaloSupport(EstadoIntervaloSupport estadoIntervaloSupport) {
        this.estadoIntervaloSupport = estadoIntervaloSupport;
    }
}