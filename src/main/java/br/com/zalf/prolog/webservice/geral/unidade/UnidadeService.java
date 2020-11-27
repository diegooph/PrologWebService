package br.com.zalf.prolog.webservice.geral.unidade;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.sql.NotFoundException;
import br.com.zalf.prolog.webservice.errorhandling.sql.ServerSideErrorException;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEdicaoDto;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeEntity;
import br.com.zalf.prolog.webservice.geral.unidade._model.UnidadeVisualizacaoDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Created on 2020-03-12
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
@Service
public class UnidadeService {
    private static final String TAG = UnidadeService.class.getSimpleName();
    @NotNull
    private final UnidadeDao dao;

    @Autowired
    public UnidadeService(@NotNull final UnidadeDao unidadeDao) {
        this.dao = unidadeDao;
    }

    @Transactional
    public SuccessResponse updateUnidade(@Valid @NotNull final UnidadeEdicaoDto unidadeEdicao) {
        try {
            final UnidadeEntity unidade = dao.findById(unidadeEdicao.getCodUnidade())
                    .orElseThrow(() -> new NotFoundException("O registro não foi encontrado para ser atualizado.",
                                                             "A chave enviada para atualização não existe na tabela " +
                                                                     "de unidades para poder ser atualizada.\n"
                                                                     + "Certifique-se da existẽncia da chave e tente " +
                                                                     "novamente,",
                                                             "A chave da unidade não existe na tabela unidade. " +
                                                                     "Primeiro crie o registro e depois o atualize!"));
            unidade.setNome(unidadeEdicao.getNomeUnidade());
            unidade.setCodAuxiliar(unidadeEdicao.getCodAuxiliarUnidade());
            unidade.setLatitudeUnidade(unidadeEdicao.getLatitudeUnidade());
            unidade.setLongitudeUnidade(unidadeEdicao.getLongitudeUnidade());
            final Long codigoAtualizacaoUnidade = Optional.of(dao.save(unidade))
                    .orElseThrow(() -> new ServerSideErrorException("Ocorreu um erro ao atualizar a unidade!",
                                                                    "O servidor sofreu um erro no banco de " +
                                                                            "dados ao atualizar a unidade." +
                                                                            "Houve um erro ao fazer o update de" +
                                                                            " veículo."))
                    .getCodigo();
            return new SuccessResponse(codigoAtualizacaoUnidade, "Unidade atualizada com sucesso.");
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao atualizar a unidade %d", unidadeEdicao.getCodUnidade()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar unidade, tente novamente.");
        }
    }

    @NotNull
    @Transactional
    public UnidadeVisualizacaoDto getUnidadeByCodigo(@NotNull final Long codUnidade) {
        try {
            return dao.getUnidadeByCodigo(codUnidade);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar unidade.\n" +
                                             "Código da Unidade: %d", codUnidade), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar unidade, tente novamente.");
        }
    }

    @NotNull
    @Transactional
    public List<UnidadeVisualizacaoDto> getUnidadesListagem(
            @NotNull final Long codEmpresa,
            @Nullable final List<Long> codigosRegionais) {
        try {
            return dao.getUnidadesListagem(codEmpresa, codigosRegionais);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar lista de unidades da empresa.\n" +
                                             "Código da Empresa: %d\n" +
                                             "Código da Regional: %s", codEmpresa, codigosRegionais), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar unidades, tente novamente.");
        }
    }
}
