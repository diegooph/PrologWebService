package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividade;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeIndividualHolder;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeItemInsert;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface RaizenProdutividadeDao {

    void insertOrUpdateProdutividadeRaizen(
            @NotNull final String token,
            @NotNull final List<RaizenProdutividadeItemInsert> raizenItens) throws Throwable;

    void insertRaizenProdutividadeItem(@NotNull final String token,
                                       @NotNull final RaizenProdutividadeItemInsert item) throws Throwable;

    void updateRaizenProdutividadeItem(@NotNull final String token,
                                       @NotNull final Long codItem,
                                       @NotNull final RaizenProdutividadeItemInsert item) throws Throwable;

    @NotNull
    List<RaizenProdutividade> getRaizenProdutividadeColaborador(@NotNull final Long codUnidade,
                                                                @NotNull final LocalDate dataInicial,
                                                                @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    List<RaizenProdutividade> getRaizenProdutividadeData(@NotNull final Long codUnidade,
                                                         @NotNull final LocalDate dataInicial,
                                                         @NotNull final LocalDate dataFinal) throws Throwable;

    @NotNull
    RaizenProdutividadeItemVisualizacao getRaizenProdutividadeItemVisualizacao(@NotNull final Long codItem)
            throws Throwable;

    @NotNull
    RaizenProdutividadeIndividualHolder getRaizenProdutividadeIndividual(@NotNull final Long codUnidade,
                                                                         @NotNull final Long codColaborador,
                                                                         final int mes,
                                                                         final int ano) throws Throwable;

    void deleteRaizenProdutividadeItens(@NotNull final List<Long> codRaizenProdutividades) throws Throwable;
}
