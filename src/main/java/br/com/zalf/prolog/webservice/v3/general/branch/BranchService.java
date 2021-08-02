package br.com.zalf.prolog.webservice.v3.general.branch;

import br.com.zalf.prolog.webservice.commons.network.SuccessResponse;
import br.com.zalf.prolog.webservice.errorhandling.sql.NotFoundException;
import br.com.zalf.prolog.webservice.errorhandling.sql.ServerSideErrorException;
import br.com.zalf.prolog.webservice.v3.general.branch._model.BranchEntity;
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
    public SuccessResponse updateBranch(@NotNull final BranchEntity branch) {
        final BranchEntity savedBranch = dao.findById(branch.getId()).orElseThrow(NotFoundException::new);
        final BranchEntity branchUpdate = savedBranch.toBuilder()
                .withName(branch.getName())
                .withAdditionalId(branch.getAdditionalId())
                .withBranchLatitude(branch.getBranchLatitude())
                .withBranchLongitude(branch.getBranchLongitude())
                .build();
        final Long updatedBranchId = Optional.of(dao.save(branchUpdate))
                .orElseThrow(ServerSideErrorException::defaultNotLoggableException)
                .getId();
        return new SuccessResponse(updatedBranchId, "Unidade atualizada com sucesso.");
    }

    @NotNull
    @Transactional
    public BranchEntity getBranchById(@NotNull final Long branchId) {
        return dao.getBranchById(branchId).orElseThrow(NotFoundException::new);
    }

    @NotNull
    @Transactional
    public List<BranchEntity> getAllBranches(@NotNull final Long companyId,
                                             final List<Long> groupsId) {
        return dao.getAllBranches(companyId, groupsId.isEmpty() ? null : groupsId);
    }

    @NotNull
    public List<BranchEntity> getBranchesByTokenUser(@NotNull final String tokenUser) {
        return dao.findAllByTokenUser(tokenUser);
    }

    @NotNull
    public List<BranchEntity> getBranchesByTokenApi(@NotNull final String tokenApi) {
        return dao.findAllByTokenApi(tokenApi);
    }
}
