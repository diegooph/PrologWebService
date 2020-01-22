package br.com.zalf.prolog.webservice.colaborador.model;

import br.com.zalf.prolog.webservice.colaborador.constraints.Pis;

import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Teste de bean validation
 */
public class ColaboradorTeste {
    @Nullable
    @Pis(message = "Número PIS inválido.")
    private String pis;

    /*
    * A validação do CPF só está considerando 11 caracteres devido às operações no Paraguai
    * */
    @NotNull(message = "Você precisa fornecer o CPF.")
    @Size(min = 11, max = 11, message = "Número CPF inválido.")
    private String cpf;

    public ColaboradorTeste(final String pis,
                            final String cpf) {
        this.pis = pis;
        this.cpf = cpf;
    }

    public String getPis() {
        return pis;
    }

    public String getCpf() { return cpf; }
}
