package br.com.zalf.prolog.webservice.gente.controlejornada.model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.gente.controlejornada.tipomarcacao.TipoMarcacao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IntervaloOfflineSupport {
    public static final String HEADER_NAME_VERSAO_DADOS_INTERVALO = "ProLog-Versao-Dados-Intervalo";
    public static final String HEADER_NAME_TOKEN_MARCACAO = "ProLog-Token-Marcacao";

    @Nullable
    private List<Colaborador> colaboradores;

    @Nullable
    private List<TipoMarcacao> tiposIntervalo;

    @Nullable
    private Long versaoDadosIntervalo;

    @Nullable
    private String tokenSincronizacaoMarcacao;

    @NotNull
    private EstadoVersaoIntervalo estadoVersaoIntervalo;

    public IntervaloOfflineSupport(@NotNull final EstadoVersaoIntervalo estadoVersaoIntervalo) {
        this.estadoVersaoIntervalo = estadoVersaoIntervalo;
    }

    public List<Colaborador> getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(List<Colaborador> colaboradores) {
        this.colaboradores = colaboradores;
    }

    public List<TipoMarcacao> getTiposIntervalo() {
        return tiposIntervalo;
    }

    public void setTiposIntervalo(List<TipoMarcacao> tiposIntervalo) {
        this.tiposIntervalo = tiposIntervalo;
    }

    public Long getVersaoDadosIntervalo() {
        return versaoDadosIntervalo;
    }

    public void setVersaoDadosIntervalo(Long versaoDadosIntervalo) {
        this.versaoDadosIntervalo = versaoDadosIntervalo;
    }

    public EstadoVersaoIntervalo getEstadoVersaoIntervalo() {
        return estadoVersaoIntervalo;
    }

    public void setEstadoVersaoIntervalo(EstadoVersaoIntervalo estadoVersaoIntervalo) {
        this.estadoVersaoIntervalo = estadoVersaoIntervalo;
    }

    public String getTokenSincronizacaoMarcacao() {
        return tokenSincronizacaoMarcacao;
    }

    public void setTokenSincronizacaoMarcacao(final String tokenSincronizacaoMarcacao) {
        this.tokenSincronizacaoMarcacao = tokenSincronizacaoMarcacao;
    }
}