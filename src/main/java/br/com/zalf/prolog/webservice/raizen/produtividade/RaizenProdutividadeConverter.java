package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeColaborador;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeData;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemColaborador;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemData;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemVisualizacao;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenprodutividadeItemIndividual;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 30/07/2018
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
class RaizenProdutividadeConverter {

    private RaizenProdutividadeConverter() {
        throw new IllegalStateException(RaizenProdutividadeConverter.class.getSimpleName()
                + " cannot be instantiated!");
    }

    @NotNull
    static RaizenProdutividadeColaborador createRaizenProdutividadeColaborador(
            @NotNull final ResultSet rSet) throws Throwable {
        final List<RaizenProdutividadeItemData> itensData = new ArrayList<>();
        itensData.add(RaizenProdutividadeConverter.createRaizenProdutividadeItemData(rSet));
        final RaizenProdutividadeColaborador raizenProdutividadeColaborador = new RaizenProdutividadeColaborador(
                rSet.getLong("CPF_MOTORISTA"),
                rSet.getString("NOME_MOTORISTA"));
        raizenProdutividadeColaborador.setItensRaizen(itensData);
        return raizenProdutividadeColaborador;
    }

    @NotNull
    static RaizenProdutividadeData createRaizenProdutividadeData(@NotNull final ResultSet rSet) throws Throwable {
        final List<RaizenProdutividadeItemColaborador> itensColaborador = new ArrayList<>();
        itensColaborador.add(RaizenProdutividadeConverter.createRaizenProdutividadeItemColaborador(rSet));
        final RaizenProdutividadeData raizenProdutividadeData = new RaizenProdutividadeData(
                rSet.getObject("DATA_VIAGEM", LocalDate.class));
        raizenProdutividadeData.setItensRaizen(itensColaborador);
        return raizenProdutividadeData;
    }

    @NotNull
    static RaizenprodutividadeItemIndividual createRaizenProdutividadeItemIndividual(
            @NotNull final ResultSet rSet) throws Throwable {
        final RaizenprodutividadeItemIndividual item = new RaizenprodutividadeItemIndividual();
        item.setDataViagem(rSet.getDate("DATA_VIAGEM"));
        item.setValor(rSet.getBigDecimal("VALOR"));
        item.setPlaca(rSet.getString("PLACA"));
        item.setUsina(rSet.getString("USINA"));
        item.setFazenda(rSet.getString("FAZENDA"));
        item.setRaioKm(rSet.getBigDecimal("RAIO_KM"));
        item.setToneladas(rSet.getBigDecimal("TONELADAS"));
        return item;
    }

    @NotNull
    static RaizenProdutividadeItemColaborador createRaizenProdutividadeItemColaborador(
            @NotNull final ResultSet rSet) throws Throwable {
        final RaizenProdutividadeItemColaborador item = new RaizenProdutividadeItemColaborador(
                rSet.getLong("CPF_MOTORISTA"),
                rSet.getString("NOME_MOTORISTA"));
        item.setCodigo(rSet.getLong("CODIGO"));
        item.setPlaca(rSet.getString("PLACA"));
        item.setPlacaCadastrada(rSet.getBoolean("PLACA_CADASTRADA"));
        item.setValor(rSet.getBigDecimal("VALOR"));
        item.setUsina(rSet.getString("USINA"));
        item.setFazenda(rSet.getString("FAZENDA"));
        item.setRaioKm(rSet.getBigDecimal("RAIO_KM"));
        item.setToneladas(rSet.getBigDecimal("TONELADAS"));
        item.setCodColaboradorCadastro(rSet.getLong("COD_COLABORADOR_CADASTRO"));
        item.setCodColaboradorAlteracao(rSet.getLong("COD_COLABORADOR_ALTERACAO"));
        item.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        return item;
    }

    @NotNull
    static RaizenProdutividadeItemData createRaizenProdutividadeItemData(
            @NotNull final ResultSet rSet) throws Throwable {
        final RaizenProdutividadeItemData item = new RaizenProdutividadeItemData();
        item.setCodigo(rSet.getLong("CODIGO"));
        item.setPlaca(rSet.getString("PLACA"));
        item.setPlacaCadastrada(rSet.getBoolean("PLACA_CADASTRADA"));
        item.setDataViagem(rSet.getObject("DATA_VIAGEM", LocalDate.class));
        item.setValor(rSet.getBigDecimal("VALOR"));
        item.setUsina(rSet.getString("USINA"));
        item.setFazenda(rSet.getString("FAZENDA"));
        item.setRaioKm(rSet.getBigDecimal("RAIO_KM"));
        item.setToneladas(rSet.getBigDecimal("TONELADAS"));
        item.setCodColaboradorCadastro(rSet.getLong("COD_COLABORADOR_CADASTRO"));
        item.setCodColaboradorAlteracao(rSet.getLong("COD_COLABORADOR_ALTERACAO"));
        item.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        return item;
    }

    @NotNull
    static RaizenProdutividadeItemVisualizacao createRaizenProdutividadeItemVisualizacao(
            @NotNull final ResultSet rSet) throws Throwable {
        final RaizenProdutividadeItemVisualizacao item = new RaizenProdutividadeItemVisualizacao();
        item.setCodigo(rSet.getLong("CODIGO"));
        item.setPlaca(rSet.getString("PLACA"));
        item.setPlacaCadastrada(rSet.getBoolean("PLACA_CADASTRADA"));
        item.setDataViagem(rSet.getObject("DATA_VIAGEM", LocalDate.class));
        item.setCpfColaborador(rSet.getLong("CPF_MOTORISTA"));
        item.setColaboradorCadastrado(rSet.getBoolean("MOTORISTA_CADASTRADO"));
        item.setValor(rSet.getBigDecimal("VALOR"));
        item.setUsina(rSet.getString("USINA"));
        item.setFazenda(rSet.getString("FAZENDA"));
        item.setRaioKm(rSet.getBigDecimal("RAIO_KM"));
        item.setToneladas(rSet.getBigDecimal("TONELADAS"));
        item.setCodColaboradorCadastro(rSet.getLong("COD_COLABORADOR_CADASTRO"));
        item.setCodColaboradorAlteracao(rSet.getLong("COD_COLABORADOR_ALTERACAO"));
        item.setCodUnidade(rSet.getLong("COD_UNIDADE"));
        return item;
    }
}