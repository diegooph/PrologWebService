package br.com.zalf.prolog.webservice.v3.general.branch;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.sql.NotFoundException;
import br.com.zalf.prolog.webservice.errorhandling.sql.ServerSideErrorException;
import br.com.zalf.prolog.webservice.v3.general.branch._model.UnidadeEntity;
import br.com.zalf.prolog.webservice.v3.general.branch._model.UnidadeMapper;
import org.jetbrains.annotations.NotNull;
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
public class BranchService {
    private static final String TAG = BranchService.class.getSimpleName();
    @NotNull
    private final BranchDao dao;
    @NotNull
    private final UnidadeMapper mapper;

    @Autowired
    public BranchService(@NotNull final BranchDao branchDao,
                         @NotNull final UnidadeMapper mapper) {
        this.dao = branchDao;
        this.mapper = mapper;
    }

    @Transactional
    public SuccessResponse updateUnidade(@NotNull final UnidadeEntity unidadeEditada) {
        UnidadeEntity unidadeToUpdate = dao.findById(unidadeEditada.getId())
                .orElseThrow(NotFoundException::new);
        unidadeToUpdate = unidadeToUpdate.toBuilder()
                .name(unidadeEditada.getName())
                .additionalId(unidadeEditada.getAdditionalId())
                .branchLatitude(unidadeEditada.getBranchLatitude())
                .branchLongitude(unidadeEditada.getBranchLongitude())
                .build();
        final Long codigoAtualizacaoUnidade = Optional.of(dao.save(unidadeToUpdate))
                .orElseThrow(ServerSideErrorException::defaultNotLoggableException)
                .getId();
        return new SuccessResponse(codigoAtualizacaoUnidade, "Unidade atualizada com sucesso.");
    }

    @NotNull
    @Transactional
    public UnidadeEntity getByCod(@NotNull final Long codUnidade) {
        return dao.getUnidadeByCod(codUnidade).orElseThrow(NotFoundException::new);
    }

    @NotNull
    @Transactional
    public List<UnidadeEntity> getUnidadesListagem(@NotNull final Long codEmpresa,
                                                   final List<Long> codGrupos) {
        return dao.getUnidadesListagem(codEmpresa, codGrupos.isEmpty() ? null : codGrupos);
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
