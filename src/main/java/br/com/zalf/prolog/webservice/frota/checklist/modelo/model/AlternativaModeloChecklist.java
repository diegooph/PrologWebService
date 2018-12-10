package br.com.zalf.prolog.webservice.frota.checklist.modelo.model;

import br.com.zalf.prolog.webservice.commons.gson.Exclude;
import br.com.zalf.prolog.webservice.commons.gson.RuntimeTypeAdapterFactory;
import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao.AlternativaModeloChecklistEdicao;
import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.insercao.AlternativaModeloChecklistInsercao;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 06/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public abstract class AlternativaModeloChecklist {
    private Long codigo;
    private String descricao;
    private boolean tipoOutros;
    private int ordemExibicao;
    private boolean deveAbrirOrdemServico;
    private PrioridadeAlternativa prioridade;

    @NotNull
    @Exclude
    private final String tipo;

    public AlternativaModeloChecklist(@NotNull final String tipo) {
        this.tipo = tipo;
    }

    @NotNull
    public static RuntimeTypeAdapterFactory<AlternativaModeloChecklist> provideTypeAdapterFactory() {
        return RuntimeTypeAdapterFactory
                .of(AlternativaModeloChecklist.class, "tipo")
                .registerSubtype(AlternativaModeloChecklistInsercao.class, AlternativaModeloChecklistInsercao.TIPO_SERIALIZACAO)
                .registerSubtype(AlternativaModeloChecklistEdicao.class, AlternativaModeloChecklistEdicao.TIPO_SERIALIZACAO);
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(final Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(final String descricao) {
        this.descricao = descricao;
    }

    public boolean isTipoOutros() {
        return tipoOutros;
    }

    public void setTipoOutros(final boolean tipoOutros) {
        this.tipoOutros = tipoOutros;
    }

    public int getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(final int ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public boolean isDeveAbrirOrdemServico() {
        return deveAbrirOrdemServico;
    }

    public void setDeveAbrirOrdemServico(final boolean deveAbrirOrdemServico) {
        this.deveAbrirOrdemServico = deveAbrirOrdemServico;
    }

    public PrioridadeAlternativa getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(final PrioridadeAlternativa prioridade) {
        this.prioridade = prioridade;
    }
}
