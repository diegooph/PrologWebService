package br.com.zalf.prolog.webservice.gente.calendario;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Cargo;
import br.com.zalf.prolog.webservice.gente.colaborador.model.Equipe;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;

import java.time.LocalDateTime;

/**
 * Created by luiz on 2/8/16.
 * Informações do evento, utilizado na tela calendário
 */
public class Evento {
    private long codigo;
    private LocalDateTime data;
    private String descricao;
    private String local;
    private Equipe equipe;
    private Cargo funcao;
    private Unidade unidade;

    public Evento() {
    }

    public Evento(final long codigo, final LocalDateTime data, final String descricao, final String local) {
        this.codigo = codigo;
        this.data = data;
        this.descricao = descricao;
        this.local = local;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(final Equipe equipe) {
        this.equipe = equipe;
    }

    public Cargo getFuncao() {
        return funcao;
    }

    public void setFuncao(final Cargo cargo) {
        this.funcao = cargo;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(final Unidade unidade) {
        this.unidade = unidade;
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(final long codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(final LocalDateTime data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(final String descricao) {
        this.descricao = descricao;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(final String local) {
        this.local = local;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "codigo=" + codigo +
                ", data=" + data +
                ", descricao='" + descricao + '\'' +
                ", local='" + local + '\'' +
                ", equipe=" + equipe +
                ", funcao=" + funcao +
                ", unidade=" + unidade +
                '}';
    }
}
