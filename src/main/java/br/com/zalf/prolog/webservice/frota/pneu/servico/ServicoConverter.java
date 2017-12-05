package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/5/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
final class ServicoConverter {

    private ServicoConverter() {
        throw new IllegalStateException(ServicoConverter.class.getSimpleName() + " cannot be instantiated!");
    }


    static List<Servico> createServicos(final ResultSet rSet, final PneuDao pneuDao) throws SQLException {
        final List<Servico> servicos = new ArrayList<>();
        while (rSet.next()) {
            servicos.add(createServico(rSet, pneuDao));
        }
        return servicos;
    }

    static Servico createServico(final ResultSet resultSet, final PneuDao pneuDao) throws SQLException {
        final TipoServico tipo = TipoServico.fromString(resultSet.getString("TIPO_SERVICO"));
        Servico servico;
        switch (tipo) {
            case CALIBRAGEM:
                servico = createCalibragem(pneuDao, resultSet);
                break;
            case MOVIMENTACAO:
                servico = createMovimentacao(resultSet);
                break;
            case INSPECAO:
                servico = createInspecao(pneuDao, resultSet);
                break;
            default:
                throw new IllegalStateException("Tipo desconhecido: " + tipo);
        }
        setCommonAttributes(servico, resultSet, pneuDao);
        return servico;
    }

    private static ServicoCalibragem createCalibragem(PneuDao pneuDao, ResultSet rSet) throws SQLException {
        // TODO: remover esse método se não for preciso setar nada.
        return new ServicoCalibragem();
    }

    private static ServicoInspecao createInspecao(PneuDao pneuDao, ResultSet rSet) throws SQLException {
        final ServicoInspecao inspecao = new ServicoInspecao();
        final Alternativa alternativa = new Alternativa();
        alternativa.setCodigo(rSet.getLong("COD_ALTERNATIVA_SELECIONADA"));
        alternativa.setAlternativa(rSet.getString("DESCRICAO_ALTERNATIVA_SELECIONADA"));
        inspecao.setAlternativaSelecionada(alternativa);
        return inspecao;
    }

    private static ServicoMovimentacao createMovimentacao(ResultSet rSet) throws SQLException {
        final ServicoMovimentacao movimentacao = new ServicoMovimentacao();
        // TODO: qual a melhor forma de recuperar o pneu novo aqui? Uma chamada a PneuDao não vai dar certo pq o pneu
        // será buscado com os novos valores. Teriamos que salvar em banco no momento de finalizar uma movimentação
        // todos
//        movimentacao.setPneuNovo(pneuDao.getPneuByCod());
        return movimentacao;
    }

    private static void setCommonAttributes(final Servico servico, final ResultSet resultSet, final PneuDao pneuDao)
            throws SQLException {
        servico.setCodigo(resultSet.getLong("CODIGO"));
        servico.setCpfResponsavelFechamento(resultSet.getLong("CPF_RESPONSAVEL_FECHAMENTO"));
        servico.setDataHoraAbertura(resultSet.getDate("DATA_HORA_ABERTURA"));
        servico.setDataHoraFechamento(resultSet.getDate("DATA_HORA_FECHAMENTO"));
        servico.setPlacaVeiculo(resultSet.getString("PLACA_VEICULO"));
        servico.setPneuComProblema(pneuDao.createPneu(resultSet));
        servico.setKmVeiculoMomentoFechamento(resultSet.getInt("KM_VEICULO_MOMENTO_FECHAMENTO"));
        servico.setCodAfericao(resultSet.getLong("COD_AFERICAO"));
        servico.setTipoServico(TipoServico.fromString(resultSet.getString("TIPO_SERVICO")));
        servico.setQtdApontamentos(resultSet.getInt("QT_APONTAMENTOS"));
        servico.setTempoRealizacaoServicoInMillis(resultSet.getLong("TEMPO_REALIZACAO"));
    }
}