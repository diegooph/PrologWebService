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
        final List<String> concatVeiculoPosicaoRecebido = acoplamentoRecebido.stream()
                .map(a -> String.format("%s%s", a.getCodVeiculo(), a.getCodPosicao()))
                .collect(Collectors.toList());

        final List<String> concatVeiculoPosicaoAtual = acoplamentoAtual.stream()
                .map(a -> String.format("%s%s", a.getCodVeiculo(), a.getCodPosicao()))
                .collect(Collectors.toList());

        if (concatVeiculoPosicaoRecebido.containsAll(concatVeiculoPosicaoAtual)) {
            System.out.println(true);
        }
        //TODO - Aqui ainda tá errado. Se não cair no de cima tem que voltar e não ir pro próximo.
        else if (veiculoAcoplamentoProcessoRealizacao.getAcoesRealizadas().size() == acoplamentoRecebido.size()) {
            System.out.println(true);
        }
    }

    private long[] getListCodVeiculosAtuais(final List<AcoplamentoRecebido> acoplamentoRecebidos) {
        return acoplamentoRecebidos.stream()
                .mapToLong(AcoplamentoRecebido::getCodVeiculo)
                .toArray();
    }
}
