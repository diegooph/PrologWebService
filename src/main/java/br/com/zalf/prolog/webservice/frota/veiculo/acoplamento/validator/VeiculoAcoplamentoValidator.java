package br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.validator;

import br.com.zalf.prolog.webservice.frota.veiculo.VeiculoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento.VeiculoAcoplamentoDao;
import br.com.zalf.prolog.webservice.frota.veiculo.acoplamento._model.realizacao.VeiculoAcoplamentoProcessoRealizacao;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

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
                veiculoAcoplamentoDao.buscaAcoplamentosAtuais(getListCodVeiculos(acoplamentoRecebidos));
    }

    private long[] getListCodVeiculos(final List<AcoplamentoRecebido> acoplamentoRecebidos) {
        return acoplamentoRecebidos.stream()
                .mapToLong(AcoplamentoRecebido::getCodVeiculo)
                .toArray();
    }
}
