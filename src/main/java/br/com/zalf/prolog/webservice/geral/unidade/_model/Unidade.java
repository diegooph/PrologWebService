package br.com.zalf.prolog.webservice.geral.unidade._model;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Equipe;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchDto;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchUpdateDto;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jean on 27/01/16.
 * Unidade de uma empresa, contém uma ou mais equipes (salas).
 *
 * @deprecated at 2020-03-17. Esse objeto é utilizado para inúmeros casos de uso, listagem, visualização, seleção e etc.
 * Nosso novo padrão é termos um objeto para cada ação realizada no sistema.
 * Isso já começou a ser migrado com os novos objetos para {@link BranchUpdateDto edição} e
 * para {@link BranchDto visualização}.
 */
@Deprecated
public final class Unidade {

    private Long codigo;
    private String nome;
    private List<Equipe> equipes;

    /**
     * Ainda não será removido pois implementações antidas utilizam essa lista com o nome das equipes.
     */
    @Deprecated
    @SerializedName("listEquipe")
    private List<String> listNomesEquipes;

    public Unidade() {
    }

    public Unidade(final Long codigo, final String nome, final List<String> listEquipe) {
        this.codigo = codigo;
        this.nome = nome;
        this.listNomesEquipes = listEquipe;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Unidade)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        final Unidade unidade = (Unidade) obj;
        return !(codigo == null || unidade.codigo == null) && codigo.equals(unidade.codigo);
    }

    @Override
    public String toString() {
        return "Unidade{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", listNomesEquipes=" + listNomesEquipes +
                '}';
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

    public List<String> getListNomesEquipes() {
        return listNomesEquipes;
    }

    public void setListNomesEquipes(final List<String> listNomesEquipes) {
        this.listNomesEquipes = listNomesEquipes;
    }

    public List<Equipe> getEquipes() {
        return equipes;
    }

    public void setEquipes(final List<Equipe> equipes) {
        this.equipes = equipes;
    }
}
