package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.VeiculoAcoplamentoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 2020-12-02
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@AllArgsConstructor
public class VeiculoAcoplamentoValidator {
    @NotNull
    private final Connection connection;
    @NotNull
    private final VeiculoAcoplamentoDao veiculoAcoplamentoDao;
    @NotNull
    private final VeiculoDao veiculoDao;

    // Verifica se há códigos repetidos ao enviar os acoplamentos - desde a acao não seja "Mudou_posicao_x"
    public void veiculoAcoplamentoValidator(@NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {

        final List<AcoplamentoRecebido> acoplamentoRecebidos = processoRealizacao.getAcoplamentosRecebidos();

        final List<AcoplamentoAtual> acoplamentosAtuais =
                veiculoAcoplamentoDao.buscaAcoplamentosAtuais(getListCodVeiculosAtuais(acoplamentoRecebidos));
        validaAcoplamentoDiferente(processoRealizacao, acoplamentoRecebidos, acoplamentosAtuais);
    }

    private void validaAcoplamentoDiferente(@NotNull final VeiculoAcoplamentoProcessoRealizacao veiculoAcoplamentoProcessoRealizacao,
                                            @NotNull final List<AcoplamentoRecebido> acoplamentoRecebido,
                                            @NotNull final List<AcoplamentoAtual> acoplamentoAtual) {
        //verificar tamanho e verificar se as posições são as mesmas
        if (veiculoAcoplamentoProcessoRealizacao.getAcoesRealizadas().size() != acoplamentoRecebido.size()) {
            // TODO prossegue o insert de acoplamento
        } else {
            final List<String> concatVeiculoPosicaoRecebido = acoplamentoRecebido.stream()
                    .map(recebido -> String.format("%s%s%s",
                                                   recebido.getCodProcessoAcoplamento(),
                                                   recebido.getCodVeiculo(),
                                                   recebido.getCodPosicao()))
                    .collect(Collectors.toList());

            final List<String> concatVeiculoPosicaoAtual = acoplamentoAtual.stream()
                    .map(atual -> String.format("%s%s%s",
                                                atual.getCodProcessoAcoplamento(),
                                                atual.getCodVeiculo(),
                                                atual.getCodPosicao()))
                    .collect(Collectors.toList());
            if (concatVeiculoPosicaoRecebido.containsAll(concatVeiculoPosicaoAtual)) {
                // TODO retorna ok
            } else {
                // tem que ver se os processos são iguais
                final List<Long> codProcessoAcoplamentoAtual = acoplamentoAtual.stream()
                        .map(AcoplamentoAtual::getCodProcessoAcoplamento).distinct()
                        .collect(Collectors.toList());
                final List<Long> codProcessoAcoplamentoRecebido = acoplamentoRecebido.stream()
                        .map(AcoplamentoRecebido::getCodProcessoAcoplamento).distinct()
                        .collect(Collectors.toList());
                if (codProcessoAcoplamentoRecebido.containsAll(codProcessoAcoplamentoAtual)) {
                    //TODO pode fazer o insert
                } else {
                    //TODO erro porque existem veículos em outros processos.
                }
            }
        }
    }

    private long[] getListCodVeiculosAtuais(final List<AcoplamentoRecebido> acoplamentoRecebidos) {
        return acoplamentoRecebidos.stream()
                .mapToLong(AcoplamentoRecebido::getCodVeiculo)
                .toArray();
    }
}
