package br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.ConfiguracaoTipoVeiculoAferivel;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Existe uma inconsistência que pode acontecer nessas configurações de Aferição, que é o caso
 * de aferição obrigatória de Estepe, porém o tipo de veículo não possue nenhum tipo de aferição permitida.
 * Definimos que não é necessário o tratamento deste caso, uma vez que como nenhum tipo de aferição está habilitada,
 * o usuário não conseguirá iniciar uma aferição e assim, não terá como aferir os estepes bem como o resto do veículo.
 *
 * Created on 09/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
final class ConfiguracaoAfericaoValidator {

    private ConfiguracaoAfericaoValidator() {
        throw new IllegalStateException(ConfiguracaoAfericaoValidator.class.getSimpleName() + " cannot be instanciated!");
    }

    static void validateUpdateTiposVeiculosAferiveis(@NotNull final List<ConfiguracaoTipoVeiculoAferivel> configuracoes)
            throws GenericException {
        for (final ConfiguracaoTipoVeiculoAferivel configuracao : configuracoes) {
            try {
                validateTipoVeiculo(configuracao);
            } catch (Exception e) {
                throw new GenericException(e.getMessage(), "TipoVeiculo está vindo null", e);
            }
        }
    }

    private static void validateTipoVeiculo(@NotNull final ConfiguracaoTipoVeiculoAferivel configuracao) {
        Preconditions.checkNotNull(configuracao.getTipoVeiculo(), "Tipo de veículo não está correto");
        Preconditions.checkNotNull(configuracao.getTipoVeiculo().getCodigo(), "Código do tipo de veículo não está correto");
    }
}