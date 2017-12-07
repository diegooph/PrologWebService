package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;

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
        // Aqui precisa ser um do-while porque já é feito um resultSet.next() antes de chamar
        // esse método. Se fizessemos apenas um while, perderíamos o primeiro elemento.
        do {
            servicos.add(createServico(rSet, pneuDao, false));
        } while (rSet.next());

        return servicos;
    }

    static Servico createServico(final ResultSet resultSet,
                                 final PneuDao pneuDao,
                                 final boolean incluirAtributosEspecificos) throws SQLException {
        final TipoServico tipo = TipoServico.fromString(resultSet.getString("TIPO_SERVICO"));
        Servico servico;
        switch (tipo) {
            case CALIBRAGEM:
                // O serviço de calibragem não possui nenhum atributo específico.
                servico = new ServicoCalibragem();
                break;
            case MOVIMENTACAO:
                servico = new ServicoMovimentacao();
                if (incluirAtributosEspecificos) {
                    setAtributosMovimentacao((ServicoMovimentacao) servico, resultSet);
                }
                break;
            case INSPECAO:
                servico = new ServicoInspecao();
                if (incluirAtributosEspecificos) {
                    setAtributosInspecao((ServicoInspecao) servico, resultSet);
                }
                break;
            default:
                throw new IllegalStateException("Tipo de serviço desconhecido: " + tipo);
        }
        setAtributosComunsServico(servico, resultSet, pneuDao);
        return servico;
    }

    static ServicosAbertosHolder createServicosAbertosHolder(ResultSet resultSet) throws SQLException {
        final ServicosAbertosHolder holder = new ServicosAbertosHolder();
        final List<QuantidadeServicos> servicos = new ArrayList<>();
        int totalCalibragens = 0, totalInspecoes = 0, totalMovimentacoes = 0;
        while (resultSet.next()) {
            final QuantidadeServicosVeiculo qtdServicosVeiculo = createQtdServicosVeiculo(resultSet);
            totalCalibragens += qtdServicosVeiculo.getQtdServicosCalibragem();
            totalInspecoes += qtdServicosVeiculo.getQtdServicosInspecao();
            totalMovimentacoes += qtdServicosVeiculo.getQtdServicosMovimentacao();
            servicos.add(qtdServicosVeiculo);
        }
        holder.setQtdTotalCalibragensAbertas(totalCalibragens);
        holder.setQtdTotalInspecoesAbertas(totalInspecoes);
        holder.setQtdTotalMovimentacoesAbertas(totalMovimentacoes);
        holder.setServicosAbertos(servicos);
        return holder;
    }

    static QuantidadeServicosVeiculo createQtdServicosVeiculo(ResultSet resultSet) throws SQLException {
        final QuantidadeServicosVeiculo qtdServicosFechados = new QuantidadeServicosVeiculo();
        qtdServicosFechados.setPlacaVeiculo(resultSet.getString("PLACA_VEICULO"));
        qtdServicosFechados.setQtdServicosCalibragem(resultSet.getInt("TOTAL_CALIBRAGENS"));
        qtdServicosFechados.setQtdServicosInspecao(resultSet.getInt("TOTAL_INSPECOES"));
        qtdServicosFechados.setQtdServicosMovimentacao(resultSet.getInt("TOTAL_MOVIMENTACOES"));
        return qtdServicosFechados;
    }

    static QuantidadeServicosPneu createQtdServicosPneu(ResultSet resultSet) throws SQLException {
        final QuantidadeServicosPneu qtdServicosFechados = new QuantidadeServicosPneu();
        qtdServicosFechados.setCodigoPneu(resultSet.getString("COD_PNEU"));
        qtdServicosFechados.setQtdServicosCalibragem(resultSet.getInt("TOTAL_CALIBRAGENS"));
        qtdServicosFechados.setQtdServicosInspecao(resultSet.getInt("TOTAL_INSPECOES"));
        qtdServicosFechados.setQtdServicosMovimentacao(resultSet.getInt("TOTAL_MOVIMENTACOES"));
        return qtdServicosFechados;
    }

    static Veiculo createVeiculoAberturaServico(ResultSet resultSet) throws SQLException {
        final Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(resultSet.getString("PLACA_VEICULO"));
        veiculo.setKmAtual(resultSet.getInt("KM_VEICULO"));

        final List<Pneu> pneus = new ArrayList<>();
        // Aqui precisa ser um do-while porque já é feito um resultSet.next() antes de chamar
        // esse método. Se fizessemos apenas um while, perderíamos o primeiro elemento.
        do {
            final Pneu pneu = new Pneu();
            pneu.setCodigo(resultSet.getString("COD_PNEU"));
            pneu.setPosicao(resultSet.getInt("POSICAO"));
            pneu.setVidaAtual(resultSet.getInt("VIDA_MOMENTO_AFERICAO"));
            pneu.setPressaoAtual(resultSet.getDouble("PSI"));

            // Sulcos.
            final Sulcos sulcos = new Sulcos();
            sulcos.setExterno(resultSet.getDouble("ALTURA_SULCO_EXTERNO"));
            sulcos.setCentralExterno(resultSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
            sulcos.setCentralInterno(resultSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
            sulcos.setInterno(resultSet.getDouble("ALTURA_SULCO_INTERNO"));
            pneu.setSulcosAtuais(sulcos);
            pneus.add(pneu);
        } while (resultSet.next());
        veiculo.setListPneus(pneus);

        return veiculo;
    }

    private static void setAtributosInspecao(final ServicoInspecao inspecao, final ResultSet rSet) throws SQLException {
        final Alternativa alternativa = new Alternativa();
        alternativa.setCodigo(rSet.getLong("COD_ALTERNATIVA_SELECIONADA"));
        alternativa.setAlternativa(rSet.getString("DESCRICAO_ALTERNATIVA_SELECIONADA"));
        inspecao.setAlternativaSelecionada(alternativa);
    }

    private static void setAtributosMovimentacao(final ServicoMovimentacao movimentacao, final ResultSet rSet) throws SQLException {
        // TODO: qual a melhor forma de recuperar o pneu novo aqui? Uma chamada a PneuDao não vai dar certo pq o pneu
        // será buscado com os novos valores. Teriamos que salvar em banco no momento de finalizar uma movimentação
        // todos
//        movimentacao.setPneuNovo(pneuDao.getPneuByCod());
    }

    private static void setAtributosComunsServico(final Servico servico, final ResultSet resultSet, final PneuDao pneuDao)
            throws SQLException {
        servico.setCodigo(resultSet.getLong("CODIGO_SERVICO"));
        servico.setCpfResponsavelFechamento(resultSet.getLong("CPF_RESPONSAVEL_FECHAMENTO"));
        servico.setDataHoraAbertura(resultSet.getDate("DATA_HORA_ABERTURA"));
        servico.setDataHoraFechamento(resultSet.getDate("DATA_HORA_FECHAMENTO"));
        servico.setPlacaVeiculo(resultSet.getString("PLACA_VEICULO"));
        servico.setPneuComProblema(pneuDao.createPneu(resultSet));
        servico.setKmVeiculoMomentoFechamento(resultSet.getInt("KM_VEICULO_MOMENTO_FECHAMENTO"));
        servico.setCodAfericao(resultSet.getLong("COD_AFERICAO"));
        servico.setTipoServico(TipoServico.fromString(resultSet.getString("TIPO_SERVICO")));
        servico.setQtdApontamentos(resultSet.getInt("QT_APONTAMENTOS"));
        servico.setTempoRealizacaoServicoInMillis(resultSet.getLong("TEMPO_REALIZACAO_MILLIS"));
    }
}