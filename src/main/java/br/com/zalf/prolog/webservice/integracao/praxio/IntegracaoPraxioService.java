package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.datetime.Now;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicaoStatus;
import br.com.zalf.prolog.webservice.integracao.BaseIntegracaoService;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.agendador.AgendadorService;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoCadastroPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoEdicaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoTransferenciaPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistParaSincronizar;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ItemOSAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.OrdemServicoAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.response.SuccessResponseIntegracao;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class IntegracaoPraxioService extends BaseIntegracaoService {
    @NotNull
    private static final String TAG = IntegracaoPraxioService.class.getSimpleName();
    @NotNull
    private final IntegracaoPraxioDao dao = new IntegracaoPraxioDaoImpl();

    @NotNull
    public SuccessResponseIntegracao inserirVeiculoPraxio(
            final String tokenIntegracao,
            final VeiculoCadastroPraxio veiculoCadastroPraxio) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            final List<Long> codUnidadesBloquedas = Injection
                    .provideIntegracaoDao()
                    .getCodUnidadesIntegracaoBloqueadaByTokenIntegracao(
                            tokenIntegracao,
                            SistemaKey.API_PROLOG,
                            RecursoIntegrado.VEICULOS);
            if (codUnidadesBloquedas.contains(veiculoCadastroPraxio.getCodUnidadeAlocado())) {
                throw new GenericException(
                        String.format("Unidade (%s) está com a integração bloqueada",
                                veiculoCadastroPraxio.getCodUnidadeAlocado()));
            }
            dao.inserirVeiculoCadastroPraxio(tokenIntegracao, veiculoCadastroPraxio);
            return new SuccessResponseIntegracao("Veículo inserido no ProLog com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir veículo do Globus no ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir veículo no ProLog");
        }
    }

    @NotNull
    public SuccessResponseIntegracao atualizarVeiculoPraxio(
            final String tokenIntegracao,
            final Long codUnidadeVeiculoAntesEdicao,
            final String placaVeiculoAntesEdicao,
            final VeiculoEdicaoPraxio veiculoEdicaoPraxio) throws ProLogException {
        try {
            ensureValidToken(tokenIntegracao, TAG);
            if (codUnidadeVeiculoAntesEdicao == null) {
                throw new GenericException("O código da Unidade deve ser fornecido");
            }
            final List<Long> codUnidadesBloquedas = Injection
                    .provideIntegracaoDao()
                    .getCodUnidadesIntegracaoBloqueadaByTokenIntegracao(
                            tokenIntegracao,
                            SistemaKey.API_PROLOG,
                            RecursoIntegrado.VEICULOS);
            if (codUnidadesBloquedas.contains(codUnidadeVeiculoAntesEdicao)) {
                throw new GenericException(
                        String.format("Unidade (%s) está com a integração bloqueada", codUnidadeVeiculoAntesEdicao));
            }
            dao.atualizarVeiculoPraxio(
                    tokenIntegracao,
                    codUnidadeVeiculoAntesEdicao,
                    placaVeiculoAntesEdicao,
                    veiculoEdicaoPraxio);
            return new SuccessResponseIntegracao("Veículo atualizado no ProLog com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar veículo do Globus no ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar veículo no ProLog");
        }
    }

    @NotNull
    public SuccessResponseIntegracao transferirVeiculoPraxio(
            final String tokenIntegracao,
            final VeiculoTransferenciaPraxio veiculoTransferenciaPraxio) throws ProLogException {
        if (StringUtils.isNullOrEmpty(StringUtils.trimToNull(
                veiculoTransferenciaPraxio.getCpfColaboradorRealizacaoTransferencia()))) {
            throw new GenericException("O CPF do colaborador deve ser informado na transferência de veículo");
        }
        try {
            ensureValidToken(tokenIntegracao, TAG);
            final List<Long> codUnidadesBloquedas = Injection
                    .provideIntegracaoDao()
                    .getCodUnidadesIntegracaoBloqueadaByTokenIntegracao(
                            tokenIntegracao,
                            SistemaKey.API_PROLOG,
                            RecursoIntegrado.VEICULO_TRANSFERENCIA);
            if (codUnidadesBloquedas.contains(veiculoTransferenciaPraxio.getCodUnidadeOrigem())) {
                throw new GenericException(
                        String.format("Unidade (%s) está com a integração bloqueada",
                                veiculoTransferenciaPraxio.getCodUnidadeOrigem()));
            }
            dao.transferirVeiculoPraxio(tokenIntegracao, veiculoTransferenciaPraxio);
            return new SuccessResponseIntegracao("Veículo do Globus transferido com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao realizar transferências de veículos do Globus no ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar transferências de veículos do Globus no ProLog");
        }
    }

    @NotNull
    public SuccessResponseIntegracao ativarDesativarVeiculoPraxio(final String tokenIntegracao,
                                                                  final String placaVeiculo,
                                                                  final Boolean veiculoAtivo) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um token deve ser fornecido");
            }
            if (placaVeiculo == null) {
                throw new GenericException("A placa do veículo deve ser fornecida");
            }
            if (veiculoAtivo == null) {
                throw new GenericException("A informação para ativar ou desativar o veículo não foi fornecida");
            }
            ensureValidToken(tokenIntegracao, TAG);
            final VeiculoEdicaoStatus veiculo = dao.getVeiculoEdicaoStatus(placaVeiculo, veiculoAtivo, tokenIntegracao);
            if (!veiculo.isStatusAtivo() && veiculo.isAcoplado()) {
                throw new GenericException("Não é possível inativar um veículo acoplado.");
            }
            dao.ativarDesativarVeiculoPraxio(tokenIntegracao, placaVeiculo, veiculoAtivo);
            return new SuccessResponseIntegracao(
                    "Veículo foi " + (veiculoAtivo ? "ativado" : "desativado") + " com sucesso no ProLog");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao ativar/desativar o veículo do Globus no ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao ativar/desativar o veículo do Globus no ProLog");
        }
    }

    @NotNull
    public List<MedicaoIntegracaoPraxio> getAfericoesRealizadas(final String tokenIntegracao,
                                                                final Long codUltimaAfericao) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (codUltimaAfericao == null) {
                throw new GenericException("Um código para a busca deve ser fornecido");
            }
            ensureValidToken(tokenIntegracao, TAG);
            return dao.getAfericoesRealizadas(tokenIntegracao, codUltimaAfericao);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar as novas aferições da Integração\n" +
                    "Código da última aferição sincronizada: %d", codUltimaAfericao), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar aferições para sincronizar");
        }
    }

    @NotNull
    public SuccessResponseIntegracao inserirOrdensServicoGlobus(
            final String tokenIntegracao,
            final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (ordensServicoAbertas == null || ordensServicoAbertas.isEmpty()) {
                throw new GenericException("Nenhuma informação de O.S. aberta foi recebida");
            }
            ensureValidToken(tokenIntegracao, TAG);
            dao.inserirOrdensServicoGlobus(tokenIntegracao, ordensServicoAbertas);
            // Ao receber uma O.S sincronizada, disparamos a sincronia do próximo checklist.
            sincronizaProximoChecklist();
            return new SuccessResponseIntegracao("Ordens de Serviços Abertas foram inseridas no ProLog");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir as Ordens de Serviços Abertas no banco de dados do ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir as Ordens de Serviços Abertas no ProLog");
        }
    }

    @NotNull
    public SuccessResponseIntegracao resolverMultiplosItens(
            final String tokenIntegracao,
            final List<ItemResolvidoGlobus> itensResolvidos) throws ProLogException {
        //Realiza validação para CPF
        for (final ItemResolvidoGlobus item : itensResolvidos) {
            validaCpfColaborador(item.getCpfColaboradorResolucao());
        }
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (itensResolvidos.isEmpty()) {
                throw new GenericException("Nenhum item resolvido foi recebido");
            }
            ensureValidToken(tokenIntegracao, TAG);
            dao.resolverMultiplosItens(tokenIntegracao, itensResolvidos);
            return new SuccessResponseIntegracao("Todos os itens foram resolvidos com sucesso no ProLog");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao resolver os itens no ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao resolver os itens no ProLog");
        }
    }

    @NotNull
    public ChecklistParaSincronizar getCodChecklistParaSincronizar() {
        try {
            return dao.getCodChecklistParaSincronizar();
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao buscar código de checklist para sincronizar", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar código de checklist para sincronizar");
        }
    }

    private void sincronizaProximoChecklist() {
        // Não queremos que o processo de nova sincronia trave o fim do processo de inserção de um Nova O.S.
        Executors.newSingleThreadExecutor().execute(() -> new AgendadorService().sincronizaChecklists());
    }

    private void validateVeiculoCadastro(
            @NotNull final VeiculoCadastroPraxio veiculoCadastroPraxio) throws ProLogException {
        if (veiculoCadastroPraxio.getCodUnidadeAlocado() <= 0) {
            throw new GenericException("A propriedade 'codUnidadeAlocado' deve ser um número positivo");
        }
        if (veiculoCadastroPraxio.getPlacaVeiculo().isEmpty()
                || veiculoCadastroPraxio.getPlacaVeiculo().length() > 7) {
            throw new GenericException(
                    "A propriedade 'placaVeiculo' não pode ser vazia nem conter mais que 7 caracteres");
        }
        if (veiculoCadastroPraxio.getKmAtualVeiculo() <= 0) {
            throw new GenericException("A propriedade 'kmAtualVeiculo' deve ser um número positivo maior que zero");
        }
        if (veiculoCadastroPraxio.getCodModeloVeiculo() <= 0) {
            throw new GenericException("A propriedade 'codModeloVeiculo' deve ser um número positivo");
        }
        if (veiculoCadastroPraxio.getCodTipoVeiculo() <= 0) {
            throw new GenericException("A propriedade 'codTipoVeiculo' deve ser um número positivo");
        }
    }

    ////----------------------------------------------------------------------------------------------------------------////
    ////---------------------------------- DUMMY MÉTODOS
    // ---------------------------------------------------------------////
    ////----------------------------------------------------------------------------------------------------------------////

    private void validateVeiculoEdicao(@NotNull final VeiculoEdicaoPraxio veiculoEdicaoPraxio) throws ProLogException {
        if (veiculoEdicaoPraxio.getNovoKmVeiculo() <= 0) {
            throw new GenericException("A propriedade 'kmAtualVeiculo' deve ser um número positivo maior que zero");
        }
        if (veiculoEdicaoPraxio.getNovoCodModeloVeiculo() <= 0) {
            throw new GenericException("A propriedade 'codModeloVeiculo' deve ser um número positivo");
        }
        if (veiculoEdicaoPraxio.getNovoCodTipoVeiculo() <= 0) {
            throw new GenericException("A propriedade 'codTipoVeiculo' deve ser um número positivo");
        }
    }

    private void validateTransferenciaVeiculoPraxio(
            @NotNull final VeiculoTransferenciaPraxio veiculoTransferenciaPraxio) {
        if (veiculoTransferenciaPraxio.getCodUnidadeOrigem() <= 0) {
            throw new GenericException("A propriedade 'codUnidadeOrigem' deve ser um número positivo");
        }
        if (veiculoTransferenciaPraxio.getCodUnidadeDestino() <= 0) {
            throw new GenericException("A propriedade 'codUnidadeDestino' deve ser um número positivo");
        }
        if (veiculoTransferenciaPraxio.getCpfColaboradorRealizacaoTransferencia().isEmpty()
                || veiculoTransferenciaPraxio.getCpfColaboradorRealizacaoTransferencia().length() > 11) {
            throw new GenericException("A propriedade 'cpfColaboradorRealizacaoTransferencia' " +
                    "não pode ser vazio ou ter mais que 11 caracteres");
        }
        if (veiculoTransferenciaPraxio.getPlacaTransferida().isEmpty()
                || veiculoTransferenciaPraxio.getPlacaTransferida().length() > 7) {
            throw new GenericException(
                    "A propriedade 'placaTransferida' não pode ser vazia nem conter mais que 7 caracteres");
        }
    }

    private void validateOrdensServico(
            @NotNull final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws ProLogException {
        for (final OrdemServicoAbertaGlobus ordemServico : ordensServicoAbertas) {
            if (ordemServico.getCodOsGlobus() <= 0) {
                throw new GenericException("A propriedade 'codOsGlobus' deve ser um número positivo");
            }
            if (ordemServico.getCodUnidadeOs() <= 0) {
                throw new GenericException("A propriedade 'codUnidadeItemOs' deve ser um número positivo");
            }
            if (ordemServico.getCodChecklistProLog() <= 0) {
                throw new GenericException("A propriedade 'codChecklistProLog' deve ser um número positivo");
            }
            if (ordemServico.getItensOSAbertaGlobus().size() <= 0) {
                throw new GenericException("A lista de itens não pode estar vazia");
            }

            final List<ItemOSAbertaGlobus> itensOSAbertaGlobus = ordemServico.getItensOSAbertaGlobus();
            for (final ItemOSAbertaGlobus itemOS : itensOSAbertaGlobus) {
                if (itemOS.getCodItemGlobus() <= 0) {
                    throw new GenericException("A propriedade 'codItemGlobus' deve ser um número positivo");
                }
                if (itemOS.getCodContextoPerguntaItemOs() <= 0) {
                    throw new GenericException("A propriedade 'codPerguntaItemOs' deve ser um número positivo");
                }
                if (itemOS.getCodContextoAlternativaItemOs() <= 0) {
                    throw new GenericException("A propriedade 'codAlternativaItemOs' deve ser um número positivo");
                }
            }
        }
    }

    private void validateDadosItensResolvidos(
            @NotNull final LocalDateTime dataHoraAtualUtc,
            @NotNull final List<ItemResolvidoGlobus> itensResolvidos) throws ProLogException {
        for (final ItemResolvidoGlobus itemResolvido : itensResolvidos) {
            if (itemResolvido.getCodUnidadeItemOs() <= 0) {
                throw new GenericException(String.format(
                        "O 'codUnidadeItemOs = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getCodUnidadeItemOs()));
            }
            if (itemResolvido.getCodOsGlobus() <= 0) {
                throw new GenericException(String.format(
                        "O 'codOsGlobus = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getCodOsGlobus()));
            }
            if (itemResolvido.getCodItemResolvidoGlobus() <= 0) {
                throw new GenericException(String.format(
                        "O 'codItemResolvidoGlobus = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getCodItemResolvidoGlobus()));
            }
            if (itemResolvido.getCpfColaboradorResolucao().isEmpty()) {
                throw new GenericException(String.format(
                        "O 'cpfColaboradorResolucao = %s' não pode ser vazio ou nulo.",
                        itemResolvido.getCpfColaboradorResolucao()));
            }
            if (itemResolvido.getPlacaVeiculoItemOs().isEmpty()) {
                throw new GenericException(String.format(
                        "A 'placaVeiculoItemOs = %s' não pode ser vazio ou nulo.",
                        itemResolvido.getPlacaVeiculoItemOs()));
            }
            if (itemResolvido.getKmColetadoResolucao() < 0) {
                throw new GenericException(String.format(
                        "O 'kmColetadoResolucao = %d' deve ser um número positivo e não nulo.",
                        itemResolvido.getKmColetadoResolucao()));
            }
            if (itemResolvido.getDuracaoResolucaoItemOsMillis() < 0) {
                throw new GenericException(
                        "A 'duracaoResolucaoItemOsMillis' deve ser um número positivo e não nulo.");
            }
            if (itemResolvido.getFeedbackResolucaoItemOs().isEmpty()) {
                throw new GenericException(String.format(
                        "O 'feedbackResolucaoItemOs = %s' não pode ser vazio ou nulo.",
                        itemResolvido.getFeedbackResolucaoItemOs()));
            }
            if (itemResolvido.getDataHoraInicioResolucaoItemOsUtc()
                    .isAfter(itemResolvido.getDataHoraFimResolucaoItemOsUtc())) {
                final String msg = String.format(
                        "A data/hora de ínicio da resolução é posterior à data/hora de fim do conserto para o item %d",
                        itemResolvido.getCodItemResolvidoGlobus());
                throw new GenericException(msg);
            }
            if (itemResolvido.getDataHoraInicioResolucaoItemOsUtc().isAfter(dataHoraAtualUtc)) {
                final String msg = String.format(
                        "A data/hora de ínicio da resolução é posterior à data/hora atual para o item %d",
                        itemResolvido.getCodItemResolvidoGlobus());
                throw new GenericException(msg);
            }
            if (itemResolvido.getDataHoraFimResolucaoItemOsUtc().isAfter(dataHoraAtualUtc)) {
                final String msg = String.format(
                        "A data/hora de fim da resolução é posterior à data/hora atual para o item %d",
                        itemResolvido.getCodItemResolvidoGlobus());
                throw new GenericException(msg);
            }
        }
    }

    private void validaCpfColaborador(@NotNull final String cpfColaborador) throws GenericException {
        if (StringUtils.isNullOrEmpty(StringUtils.trimToNull(cpfColaborador))) {
            throw new GenericException("O CPF do colaborador deve ser informado no fechamento de O.S");
        }
    }

    @NotNull
    SuccessResponseIntegracao validateTokenIntegracao(final String tokenIntegracao) {
        // Esse método é utilizado para a parametrização do sistema parceiro. Lá ao inserir o token que será utilizado
        // o Sistema realiza uma requisição afim de validar se o token é de fato o que será utilizado para autenticar
        // os métodos integrados.
        try {
            ensureValidToken(tokenIntegracao, TAG);
            return new SuccessResponseIntegracao("Token está autorizado a realizar requisições");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao validar o token na integração", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao validar o token na integração");
        }
    }

    @NotNull
    SuccessResponseIntegracao inserirVeiculoCadastroPraxioDummy(
            final String tokenIntegracao,
            final VeiculoCadastroPraxio veiculoCadastroPraxio) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (!tokenIntegracao.equals("kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck")) {
                throw new GenericException("O Token fornecido é inválido");
            }
            validateVeiculoCadastro(veiculoCadastroPraxio);
            return new SuccessResponseIntegracao("Veículo do Globus inserido no ProLog com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir o veículo do Globus no ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir o veículo do Globus no ProLog");
        }
    }

    @NotNull
    SuccessResponseIntegracao atualizarVeiculoPraxioDummy(
            final String tokenIntegracao,
            final Long codUnidadeVeiculoAntesEdicao,
            final String placaVeiculoAntesEdicao,
            final VeiculoEdicaoPraxio veiculoEdicaoPraxio) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (codUnidadeVeiculoAntesEdicao == null) {
                throw new GenericException("A propriedade 'codUnidadeVeiculoAntesEdicao' deve ser fornecida");
            }
            if (placaVeiculoAntesEdicao == null
                    || placaVeiculoAntesEdicao.isEmpty()
                    || placaVeiculoAntesEdicao.length() > 7) {
                throw new GenericException(
                        "A propriedade 'placaVeiculoAntesEdicao' deve ser fornecida e ter 7 caracteres");
            }
            if (!tokenIntegracao.equals("kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck")) {
                throw new GenericException("O Token fornecido é inválido");
            }
            validateVeiculoEdicao(veiculoEdicaoPraxio);
            return new SuccessResponseIntegracao("Veículo do Globus atualizado no ProLog com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao atualizar o veículo do Globus no ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar o veículo do Globus no ProLog");
        }
    }

    @NotNull
    SuccessResponseIntegracao transferirVeiculoPraxioDummy(
            final String tokenIntegracao,
            final VeiculoTransferenciaPraxio veiculoTransferenciaPraxio) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (!tokenIntegracao.equals("kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck")) {
                throw new GenericException("O Token fornecido é inválido");
            }
            validateTransferenciaVeiculoPraxio(veiculoTransferenciaPraxio);
            return new SuccessResponseIntegracao("Veículo do Globus transferido com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao realizar transferências de veículos do Globus no ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao realizar transferências de veículos do Globus no ProLog");
        }
    }

    @NotNull
    List<MedicaoIntegracaoPraxio> getAfericoesRealizadasDummy() {
        final List<MedicaoIntegracaoPraxio> afericoes = new ArrayList<>();
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPlacaSulcoPressao());
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPlacaSulco());
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPlacaPressao());
        afericoes.add(MedicaoIntegracaoPraxio.createDummyAfericaoPneuAvulsoSulco());
        return afericoes;
    }

    @NotNull
    SuccessResponseIntegracao inserirOrdensServicoGlobusDummy(
            final String tokenIntegracao,
            final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (!tokenIntegracao.equals("kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck")) {
                throw new GenericException("O Token fornecido é inválido");
            }
            if (ordensServicoAbertas == null || ordensServicoAbertas.isEmpty()) {
                throw new GenericException("Nenhuma informação de O.S. aberta foi recebida");
            }
            validateOrdensServico(ordensServicoAbertas);
            return new SuccessResponseIntegracao("Ordens de Serviços Abertas foram inseridas no ProLog");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir as Ordens de Serviços Abertas no banco de dados do ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir as Ordens de Serviços Abertas no ProLog");
        }
    }

    @NotNull
    SuccessResponseIntegracao resolverMultiplosItensDummy(
            final String tokenIntegracao,
            final List<ItemResolvidoGlobus> itensResolvidos) throws ProLogException {
        try {
            if (tokenIntegracao == null) {
                throw new GenericException("Um Token deve ser fornecido");
            }
            if (!tokenIntegracao.equals("kffdm2ba5ai3lsk79kqur9rb3mq7hv59qa8pr0sho4mcr56clck")) {
                throw new GenericException("O Token fornecido é inválido");
            }
            if (itensResolvidos == null || itensResolvidos.isEmpty()) {
                throw new GenericException("Nenhuma informação de O.S. aberta foi recebida");
            }
            final LocalDateTime dataHoraAtualUtc = Now.getLocalDateTimeUtc();
            validateDadosItensResolvidos(dataHoraAtualUtc, itensResolvidos);
            return new SuccessResponseIntegracao("Todos os itens foram resolvidos com sucesso no ProLog");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao resolver os itens no ProLog", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao resolver os itens no ProLog");
        }
    }
}