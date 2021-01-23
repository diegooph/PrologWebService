package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoCadastroPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoEdicaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoTransferenciaPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistParaSincronizar;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.OrdemServicoAbertaGlobus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
interface IntegracaoPraxioDao {

    void inserirVeiculoCadastroPraxio(@NotNull final String tokenIntegracao,
                                      @NotNull final VeiculoCadastroPraxio veiculoCadastroPraxio) throws Throwable;

    void atualizarVeiculoPraxio(@NotNull final String tokenIntegracao,
                                @NotNull final Long codUnidadeVeiculoAntesEdicao,
                                @NotNull final String placaVeiculoAntesEdicao,
                                @NotNull final VeiculoEdicaoPraxio veiculoEdicaoPraxio) throws Throwable;

    void transferirVeiculoPraxio(@NotNull final String tokenIntegracao,
                                 @NotNull final VeiculoTransferenciaPraxio veiculoTransferenciaPraxio) throws Throwable;

    void ativarDesativarVeiculoPraxio(@NotNull final String tokenIntegracao,
                                      @NotNull final String placaVeiculo,
                                      @NotNull final Boolean veiculoAtivo) throws Throwable;

    @NotNull
    List<MedicaoIntegracaoPraxio> getAfericoesRealizadas(@NotNull final String tokenIntegracao,
                                                         @NotNull final Long codUltimaAfericao) throws Throwable;

    void inserirOrdensServicoGlobus(
            @NotNull final String tokenIntegracao,
            @NotNull final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws Throwable;

    void resolverMultiplosItens(@NotNull final String tokenIntegracao,
                                @NotNull final List<ItemResolvidoGlobus> itensResolvidos) throws Throwable;

    @NotNull
    ChecklistParaSincronizar getCodChecklistParaSincronizar() throws Throwable;
}