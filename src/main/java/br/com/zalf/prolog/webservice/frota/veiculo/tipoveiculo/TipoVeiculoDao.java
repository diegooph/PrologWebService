package br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Empresa;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 22/02/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface TipoVeiculoDao {

    /**
     * Insere um {@link TipoVeiculo tipo de veículo} no banco de dados.
     *
     * @param tipoVeiculo Objeto contendo as informações do tipo de veículo a ser inserido.
     * @return O código do {@link TipoVeiculo tipo de veículo} inserido.
     * @throws Throwable Caso ocorra algum erro ao salvar no banco de dados.
     */
    @NotNull
    Long insertTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable;

    /**
     * Atualiza as informações de um {@link TipoVeiculo tipo de veículo}.
     * Atualmente este método atualiza as informações baseado na seguinte lógica:
     *
     * Se estiver atualizando um tipo que não tenha nenhum diagrama vinculado, um será vinculado caso tenha sido
     * enviado no atributo {@link TipoVeiculo#codDiagrama};
     * Se for um tipo que já tenha diagrama e o diagrama enviado for igual, vai atualizar apenas o nome;
     * E se o código de diagrama enviado em ({@link TipoVeiculo#codDiagrama}) for diferente do código atual do tipo no
     * banco, retornará um erro, pois um tipo não pode ter seu código de diagrama trocado, já que isso impactaria nos
     * pneus atualmente vinculados.
     *
     * @param tipoVeiculo Objeto contendo as novas informações para o tipo do veículo.
     * @throws Throwable Se algum erro ocorrer no processo de atualização.
     */
    void updateTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable;

    /**
     * Método utilizado para buscar a lista de {@link TipoVeiculo tipos de veículos} da empresa.
     *
     * @param codEmpresa Código da {@link Empresa empresa} para a qual serão buscados os tipos de veículos.
     * @return {@link List <TipoVeiculo> Lista de tipos de veículos} disponíveis na empresa.
     * @throws Throwable Se algum erro ocorrer na busca dos tipos de veículos.
     */
    @NotNull
    List<TipoVeiculo> getTiposVeiculosByEmpresa(@NotNull final Long codEmpresa) throws Throwable;

    /**
     * Método utilizado para buscar um {@link TipoVeiculo tipo de veículo} específico através
     * do <code>codTipoVeiculo</code>.
     *
     * @param codTipoVeiculo {@link TipoVeiculo#codigo Código} do tipo de veículo a ser buscado.
     * @return {@link TipoVeiculo Tipo de veículo} do código especificado.
     * @throws Throwable Se ocorrer algum erro na busca das informações.
     */
    @NotNull
    TipoVeiculo getTipoVeiculo(@NotNull final Long codTipoVeiculo) throws Throwable;

    /**
     * Método utilizado para deletar um {@link TipoVeiculo tipo de veículo} de uma empresa.
     * A deleção do tipo de veículo só acontece se o {@link TipoVeiculo#codigo} não tiver nenhum vínculo no sistema.
     *
     * @param codEmpresa     Código da {@link Empresa empresa} à qual o tipo de veículo pertence.
     * @param codTipoVeiculo Código do {@link TipoVeiculo tipo de veículo} a ser deletado.
     * @throws Throwable Se algum problema ocorrer no processo de deleção.
     */
    void deleteTipoVeiculoByEmpresa(@NotNull final Long codEmpresa,
                                    @NotNull final Long codTipoVeiculo) throws Throwable;
}
