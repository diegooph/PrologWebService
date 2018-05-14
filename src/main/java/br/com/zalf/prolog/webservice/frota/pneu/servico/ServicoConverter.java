package br.com.zalf.prolog.webservice.frota.pneu.servico;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.commons.questoes.Alternativa;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import br.com.zalf.prolog.webservice.frota.pneu.servico.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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


    static List<Servico> createServicos(final ResultSet rSet) throws SQLException {
        final List<Servico> servicos = new ArrayList<>();
        while (rSet.next()) {
            servicos.add(createServico(rSet, false));
        }
        return servicos;
    }

    static Servico createServico(final ResultSet resultSet,
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
        setAtributosComunsServico(servico, resultSet);
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
        qtdServicosFechados.setCodigoPneu(resultSet.getLong("COD_PNEU"));
        qtdServicosFechados.setCodigoPneuCliente(resultSet.getString("CODIGO_PNEU_CLIENTE"));
        qtdServicosFechados.setQtdServicosCalibragem(resultSet.getInt("TOTAL_CALIBRAGENS"));
        qtdServicosFechados.setQtdServicosInspecao(resultSet.getInt("TOTAL_INSPECOES"));
        qtdServicosFechados.setQtdServicosMovimentacao(resultSet.getInt("TOTAL_MOVIMENTACOES"));
        return qtdServicosFechados;
    }

    static VeiculoServico createVeiculoAberturaServico(ResultSet resultSet) throws SQLException {
        final VeiculoServico veiculo = new VeiculoServico();
        veiculo.setPlaca(resultSet.getString("PLACA_VEICULO"));
        veiculo.setKmAtual(resultSet.getInt("KM_ATUAL_VEICULO"));
        veiculo.setKmAberturaServico(resultSet.getInt("KM_ABERTURA_SERVICO"));

        final List<Pneu> pneus = new ArrayList<>();
        // Aqui precisa ser um do-while porque já é feito um resultSet.next() antes de chamar
        // esse método. Se fizessemos apenas um while, perderíamos o primeiro elemento.
        do {
            final Pneu pneu = new Pneu();
            pneu.setCodigo(resultSet.getLong("COD_PNEU"));
            pneu.setCodigoCliente(resultSet.getString("COD_PNEU_CLIENTE"));
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

    private static void setAtributosMovimentacao(final ServicoMovimentacao movimentacao, final ResultSet rSet)
            throws SQLException {
        final Sulcos sulcos = new Sulcos();
        sulcos.setExterno(rSet.getDouble("SULCO_EXTERNO_PNEU_NOVO"));
        sulcos.setCentralExterno(rSet.getDouble("SULCO_CENTRAL_EXTERNO_PNEU_NOVO"));
        sulcos.setCentralInterno(rSet.getDouble("SULCO_CENTRAL_INTERNO_PNEU_NOVO"));
        sulcos.setInterno(rSet.getDouble("SULCO_INTERNO_PNEU_NOVO"));
        movimentacao.setSulcosColetadosFechamento(sulcos);

        final Pneu pneuNovo = new Pneu();
        pneuNovo.setCodigo(rSet.getLong("COD_PNEU_NOVO"));
        pneuNovo.setCodigoCliente(rSet.getString("COD_PNEU_NOVO_CLIENTE"));
        pneuNovo.setSulcosAtuais(sulcos);
        pneuNovo.setPressaoAtual(rSet.getDouble("PRESSAO_COLETADA_FECHAMENTO"));
        // Podemos pegar da coluna POSICAO_PNEU_PROBLEMA pois o pneu novo foi movido para a posição onde o pneu com
        // problema se encontrava.
        pneuNovo.setPosicao(rSet.getInt("POSICAO_PNEU_PROBLEMA"));
        pneuNovo.setVidaAtual(rSet.getInt("VIDA_PNEU_NOVO"));
        movimentacao.setPneuNovo(pneuNovo);
    }

    private static void setAtributosComunsServico(final Servico servico, final ResultSet resultSet)
            throws SQLException {
        servico.setCodigo(resultSet.getLong("CODIGO_SERVICO"));
        servico.setCodUnidade(resultSet.getLong("COD_UNIDADE"));
        final Colaborador colaborador = new Colaborador();
        colaborador.setCpf(resultSet.getLong("CPF_RESPONSAVEL_FECHAMENTO"));
        colaborador.setNome(resultSet.getString("NOME_RESPONSAVEL_FECHAMENTO"));
        servico.setColaboradorResponsavelFechamento(colaborador);
        servico.setDataHoraAbertura(resultSet.getObject("DATA_HORA_ABERTURA", LocalDateTime.class));
        servico.setDataHoraFechamento(resultSet.getObject("DATA_HORA_FECHAMENTO", LocalDateTime.class));
        servico.setPlacaVeiculo(resultSet.getString("PLACA_VEICULO"));
        servico.setFechadoAutomaticamenteMovimentacao(resultSet.getBoolean("FECHADO_AUTOMATICAMENTE_MOVIMENTACAO"));

        // Cria pneu com problema, responsável por originar o serviço.
        final Pneu pneuProblema = new Pneu();
        pneuProblema.setCodigo(resultSet.getLong("COD_PNEU_PROBLEMA"));
        pneuProblema.setCodigoCliente(resultSet.getString("COD_PNEU_PROBLEMA_CLIENTE"));
        pneuProblema.setPosicao(resultSet.getInt("POSICAO_PNEU_PROBLEMA"));
        pneuProblema.setVidaAtual(resultSet.getInt("VIDA_PNEU_PROBLEMA"));
        pneuProblema.setPressaoAtual(resultSet.getDouble("PRESSAO_PNEU_PROBLEMA"));
        pneuProblema.setPressaoCorreta(resultSet.getDouble("PRESSAO_RECOMENDADA"));
        final Sulcos sulcosProblema = new Sulcos();
        sulcosProblema.setExterno(resultSet.getDouble("SULCO_EXTERNO_PNEU_PROBLEMA"));
        sulcosProblema.setCentralExterno(resultSet.getDouble("SULCO_CENTRAL_EXTERNO_PNEU_PROBLEMA"));
        sulcosProblema.setCentralInterno(resultSet.getDouble("SULCO_CENTRAL_INTERNO_PNEU_PROBLEMA"));
        sulcosProblema.setInterno(resultSet.getDouble("SULCO_INTERNO_PNEU_PROBLEMA"));
        pneuProblema.setSulcosAtuais(sulcosProblema);
        servico.setPneuComProblema(pneuProblema);

        servico.setKmVeiculoMomentoFechamento(resultSet.getInt("KM_VEICULO_MOMENTO_FECHAMENTO"));
        servico.setCodAfericao(resultSet.getLong("COD_AFERICAO"));
        servico.setTipoServico(TipoServico.fromString(resultSet.getString("TIPO_SERVICO")));
        servico.setQtdApontamentos(resultSet.getInt("QT_APONTAMENTOS"));
        servico.setTempoRealizacaoServicoInMillis(resultSet.getLong("TEMPO_REALIZACAO_MILLIS"));
        servico.setPressaoColetadaFechamento(resultSet.getDouble("PRESSAO_COLETADA_FECHAMENTO"));
    }

    static int getQuantidadeServicosEmAbertoPneu(ResultSet rSet) throws SQLException {
        return rSet.getInt("QTD_SERVICOS_ABERTOS");
    }
}