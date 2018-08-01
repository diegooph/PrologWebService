package br.com.zalf.prolog.webservice.contato;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 21/06/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class MensagemContato {
    @NotNull
    private final String nome;
    @NotNull
    private final String email;
    @Nullable
    private final String telefone;
    @Nullable
    private final String empresa;
    @NotNull
    private final String mensagem;

    public MensagemContato(@NotNull final String nome,
                           @NotNull final String email,
                           @Nullable final String telefone,
                           @Nullable final String empresa,
                           @NotNull final String mensagem) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.empresa = empresa;
        this.mensagem = mensagem;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getTelefone() {
        return telefone;
    }

    @Nullable
    public String getEmpresa() {
        return empresa;
    }

    @NotNull
    public String getMensagem() {
        return mensagem;
    }
}