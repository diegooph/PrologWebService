package br.com.zalf.prolog.webservice.geral.imei;

import br.com.zalf.prolog.webservice.geral.imei.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 16/07/19
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface ImeiDao {

    /**
     * Este método é utilizado para buscar todos as {@link MarcaCelularSelecao marcas de celular}.
     *
     * @return Uma lista com todos as {@link MarcaCelularSelecao marcas de celular}.
     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
     */
    @NotNull
    List<MarcaCelularSelecao> getMarcasCelular() throws Throwable;


    /**
     * Este método é utilizado para buscar todos os {@link Imei IMEIs} por empresa.
     *
     * @return Uma lista com os {@link Imei IMEIs}.
     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
     */
    @NotNull
    List<Imei> getImeisPorEmpresa(@NotNull final Long codEmpresa) throws Throwable;

//    /**
//     * Este método é utilizado para buscar todos os {@link Imei IMEIs} da empresa.
//     *
//     * @param codEmpresa Código da Empresa a qual os IMEIs serão buscados.
//     * @return Uma lista com todos os {@link Imei IMEIs} da empresa.
//     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
//     */
//    @NotNull
//    List<Imei> getTodosImeiEmpresa(@NotNull final Long codEmpresa) throws Throwable;


//    /**
//     * Este método é utilizado para buscar um {@link Imei IMEI} específico pelo código do registro e código da empresa.
//     *
//     * @param codEmpresa Código da Empresa a qual o IMEI está cadastrado
//     * @param codImei Código do registro do IMEI no banco de dados.
//     * @return O objeto com as informações do {@link Imei IMEI}.
//     * @throws Throwable Caso ocorra qualquer erro na busca dos dados.
//     */
//    @NotNull
//    List<Imei> getImei(@NotNull final Long codEmpresa, @NotNull final Long codImei) throws Throwable;



}