package br.com.zalf.prolog.webservice.autenticacao._model;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public final class AutenticacaoColaborador {
    @NotNull
    private final Long codColaborador;
    @NotNull
    private final Long cpfColaborador;
    private final boolean colaboradorAtivo;
    private final boolean empresaAtiva;
    private final boolean unidadeAtiva;

    public void validate() {
        validaColaboradorAtivo();
        validaEmpresaAtiva();
        validaUnidadeAtiva();
    }

    private void validaColaboradorAtivo() {
        if (!isColaboradorAtivo()) {
            throw new GenericException("Erro ao autenticar, colaborador inativo.");
        }
    }

    private void validaEmpresaAtiva() {
        if (!isEmpresaAtiva()) {
            throw new GenericException("Erro ao autenticar, empresa inativa.");
        }
    }

    private void validaUnidadeAtiva() {
        if (!isUnidadeAtiva()) {
            throw new GenericException("Erro ao autenticar, unidade inativa.");
        }
    }
}
