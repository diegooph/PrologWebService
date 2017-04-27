package br.com.zalf.prolog.gente.contracheque;

/**
 * Created by Zalf on 28/11/16.
 */
public class ItemImportContracheque extends ItemContracheque {

    private Long cpf;
    private String nome;

    public ItemImportContracheque() {
    }

    public Long getCpf() {
        return cpf;
    }

    public void setCpf(Long cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "ItemImportContracheque{" +
                "cpf=" + cpf +
                ", nome='" + nome + '\'' +
                super.toString() +
                '}';
    }
}
