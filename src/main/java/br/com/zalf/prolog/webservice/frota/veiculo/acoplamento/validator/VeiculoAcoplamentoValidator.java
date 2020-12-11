package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class VeiculoAcoplamentoValidator {
    @NotNull
    private final VeiculoAcoplamentoAtual acoplamentoAtual;
    @NotNull
    private final VeiculoAcoplamentoProcessoRealizacao processoRealizacao;

    public void validate() {
        validateVeiculosRepetidosAcoplamento();
        validateOrdenacao();
        validateVeiculosPertencemUnicoProcesso();
        validateAcoesAcoplamentos();
    }

    private void validateVeiculosRepetidosAcoplamento() {
    }

    private void validateOrdenacao() {
    }

    private void validateVeiculosPertencemUnicoProcesso() {

    }

    private void validateAcoesAcoplamentos() {

    }
}
