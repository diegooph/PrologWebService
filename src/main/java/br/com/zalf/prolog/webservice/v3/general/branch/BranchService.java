package br.com.zalf.prolog.webservice.v3.general.branch;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.sql.NotFoundException;
import br.com.zalf.prolog.webservice.errorhandling.sql.ServerSideErrorException;
import br.com.zalf.prolog.webservice.v3.general.branch._model.UnidadeEntity;
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
    @NotNull
    private final BranchDao dao;

    @Autowired
    public BranchService(@NotNull final BranchDao branchDao) {
        this.dao = branchDao;
    }

    @Transactional
    public SuccessResponse updateBranch(@NotNull final UnidadeEntity branch) {
        final UnidadeEntity savedBranch = dao.findById(branch.getId()).orElseThrow(NotFoundException::new);
        final UnidadeEntity branchUpdate = branch.toBuilder().withId(savedBranch.getId()).build();
        final Long updatedBranchId = Optional.of(dao.save(branchUpdate))
                .orElseThrow(ServerSideErrorException::defaultNotLoggableException)
                .getId();
        return new SuccessResponse(updatedBranchId, "Unidade atualizada com sucesso.");
    }

    @NotNull
    @Transactional
    public UnidadeEntity getBranchById(@NotNull final Long branchId) {
        return dao.getBranchById(branchId).orElseThrow(NotFoundException::new);
    }

    @NotNull
    @Transactional
    public List<UnidadeEntity> getAllBranches(@NotNull final Long companyId,
                                              final List<Long> groupsId) {
        return dao.getAllBranches(companyId, groupsId.isEmpty() ? null : groupsId);
    }

    @NotNull
    public List<UnidadeEntity> getBranchesByTokenUser(@NotNull final String tokenUser) {
        return dao.findAllByTokenUser(tokenUser);
    }

    @NotNull
    public List<UnidadeEntity> getBranchesByTokenApi(@NotNull final String tokenApi) {
        return dao.findAllByTokenApi(tokenApi);
    }
}
