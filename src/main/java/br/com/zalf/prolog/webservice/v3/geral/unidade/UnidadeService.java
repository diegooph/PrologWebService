package br.com.zalf.prolog.webservice.v3.geral.unidade;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.sql.NotFoundException;
import br.com.zalf.prolog.webservice.errorhandling.sql.ServerSideErrorException;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeEntity;
import br.com.zalf.prolog.webservice.v3.geral.unidade._model.UnidadeProjection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public SuccessResponse updateUnidade(@NotNull final UnidadeEntity unidadeEditada) {
        try {
            UnidadeEntity unidadeToUpdate = dao.findById(unidadeEditada.getCodigo())
                    .orElseThrow(NotFoundException::new);
            unidadeToUpdate = unidadeToUpdate.toBuilder()
                    .nome(unidadeEditada.getNome())
                    .codAuxiliar(unidadeEditada.getCodAuxiliar())
                    .latitudeUnidade(unidadeEditada.getLatitudeUnidade())
                    .longitudeUnidade(unidadeEditada.getLongitudeUnidade())
                    .build();
            final Long codigoAtualizacaoUnidade = Optional.of(dao.save(unidadeToUpdate))
                    .orElseThrow(ServerSideErrorException::defaultNotLoggableException)
                    .getCodigo();
            return new SuccessResponse(codigoAtualizacaoUnidade, "Unidade atualizada com sucesso.");
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao atualizar a unidade %d", unidadeEditada.getCodigo()), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao atualizar unidade, tente novamente.");
        }
    }

    @NotNull
    public UnidadeEntity getUnidadeByCodigo(@NotNull final Long codUnidade) {
        return dao.getUnidadeByCodigo(codUnidade);
    }

    @NotNull
    public UnidadeEntity getByCod(@NotNull final Long codUnidade) {
        return dao.getOne(codUnidade);
    }

    @NotNull
    @Transactional
    public List<UnidadeEntity> getUnidadesListagem(
            @NotNull final Long codEmpresa,
            @NotNull final List<Long> codsRegionais) {
        return dao.getUnidadesListagem(codEmpresa, codsRegionais.isEmpty() ? null : codsRegionais);
    }

    @NotNull
    public List<UnidadeEntity> getUnidadesByCodEmpresa(@NotNull final Long codEmpresa) {
        return dao.findAllByCodEmpresa(codEmpresa);
    }

    @NotNull
    public List<UnidadeEntity> getUnidadesByTokenUser(@NotNull final String tokenUser) {
        return dao.findAllByTokenUser(tokenUser);
    }

    @NotNull
    public List<UnidadeEntity> getUnidadesByTokenApi(@NotNull final String tokenApi) {
        return dao.findAllByTokenApi(tokenApi);
    }
}
