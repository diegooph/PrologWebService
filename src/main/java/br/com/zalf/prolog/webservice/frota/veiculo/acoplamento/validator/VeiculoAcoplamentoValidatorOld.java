package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.VeiculoAcoplamentoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.VeiculoAcoplamentoAcaoEnum;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

/**
 * Created on 2020-12-02
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
@AllArgsConstructor
public class VeiculoAcoplamentoValidatorOld {
    @NotNull
    private final VeiculoAcoplamentoDao veiculoAcoplamentoDao;
    @NotNull
    private final VeiculoDao veiculoDao;

    // Verifica se há códigos repetidos ao enviar os acoplamentos - desde a acao não seja "Mudou_posicao_x"
    public void veiculoAcoplamentoValidator(@NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {

        final List<AcoplamentoRecebido> acoplamentoRecebidos = processoRealizacao.getAcoplamentosRecebidos();

        final List<AcoplamentoAtual> acoplamentosAtuais =
                veiculoAcoplamentoDao.buscaAcoplamentosAtuais(getListCodVeiculosAtuais(acoplamentoRecebidos));
        validaVeiculoUnico(processoRealizacao);
        validaSequenciaPosicoes(acoplamentoRecebidos);
        validaAcoplamentoDiferente(acoplamentoRecebidos, acoplamentosAtuais);
    }

    private void validaAcoplamentoDiferente(@NotNull final List<AcoplamentoRecebido> acoplamentoRecebido,
                                            @Nullable final List<AcoplamentoAtual> acoplamentoAtual) {
        // se o atual for nulo - pode dale o insert.
        if (acoplamentoAtual == null) {
            //TODO prossegue o insert
        } else {
            // se os veículos estiverem nas mesmas posições - apenas retorna sucesso
            final List<String> concatVeiculoPosicaoRecebido = acoplamentoRecebido.stream()
                    .map(recebido -> format("%s%s",
                                            recebido.getCodVeiculo(),
                                            recebido.getCodPosicao()))
                    .collect(Collectors.toList());

            final List<String> concatVeiculoPosicaoAtual = acoplamentoAtual.stream()
                    .map(atual -> format("%s%s",
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
                // se for igual - vai pro processo de insert
                if (codProcessoAcoplamentoRecebido.containsAll(codProcessoAcoplamentoAtual)) {
                    //TODO pode fazer o insert
                } else {
                    //TODO erro porque existem veículos em outros processos.
                }
            }
        }
    }

    private void validaSequenciaPosicoes(@NotNull final List<AcoplamentoRecebido> acoplamentoRecebido) {
        //TODO Caso exista uma sequência 1-3-4 ou 3-4 nós transformamos para 1-2-3 e 2-3 respectivamente, ou enviamos
        // erro?
        final List<Short> posicoesAcoplamento = acoplamentoRecebido.stream()
                .map(AcoplamentoRecebido::getCodPosicao)
                .collect(Collectors.toList());
        final boolean isSequential = IntStream.range(0, posicoesAcoplamento.size())
                .allMatch(value -> value + 1 == posicoesAcoplamento.get(value));
        System.out.println(isSequential);
    }

    private long[] getListCodVeiculosAtuais(final List<AcoplamentoRecebido> acoplamentoRecebidos) {
        return acoplamentoRecebidos.stream()
                .mapToLong(AcoplamentoRecebido::getCodVeiculo)
                .toArray();
    }

    private void validaVeiculoUnico(@NotNull final VeiculoAcoplamentoProcessoRealizacao processoRealizacao) {
        //Se for origem só pode repetir em destino 1x
        // QQr outro estado é único
        final Long veiculosAcoes = processoRealizacao.getAcoesRealizadas().stream()
                .filter(acao -> (acao.getAcaoRealizada() == VeiculoAcoplamentoAcaoEnum.MUDOU_POSICAO_DESTINO)
                        || (acao.getAcaoRealizada() == VeiculoAcoplamentoAcaoEnum.MUDOU_POSICAO_ORIGEM))
                .map(veiculoAcao -> format("%s%s",
                                           veiculoAcao.getCodVeiculo(),
                                           veiculoAcao.getAcaoRealizada())).count();
        System.out.println(veiculosAcoes);
    }
}
