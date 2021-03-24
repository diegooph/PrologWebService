package br.com.zalf.prolog.webservice.frota.pneu.v3.service;

import br.com.zalf.prolog.webservice.frota.pneu.v3._model.PneuEntity;
import br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto.PneuCadastroDto;
import br.com.zalf.prolog.webservice.frota.pneu.v3.dao.PneuV3Dao;
import br.com.zalf.prolog.webservice.frota.pneu.v3.mapper.PneuMapper;
import br.com.zalf.prolog.webservice.interceptors.v3.OperacoesBloqueadasYaml;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

/**
 * Created on 2021-03-12
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Service
public class PneuV3Service {

    private final PneuV3Dao dao;
    private final OperacoesBloqueadasYaml operacoesBloqueadas;
    private final PneuMapper<PneuEntity, PneuCadastroDto> pneuCadastroMapper;

    @Autowired
    public PneuV3Service(@NotNull final PneuV3Dao dao,
                         @NotNull final OperacoesBloqueadasYaml operacoesBloqueadas,
                         @NotNull final PneuMapper<PneuEntity, PneuCadastroDto> pneuCadastroMapper) {
        this.dao = dao;
        this.operacoesBloqueadas = operacoesBloqueadas;
        this.pneuCadastroMapper = pneuCadastroMapper;
    }

    @NotNull
    public PneuEntity insert(@NotNull final PneuCadastroDto pneuCadastroDto) {
        final PneuEntity pneu = this.pneuCadastroMapper.toEntity(pneuCadastroDto);
        this.operacoesBloqueadas.validateEmpresa(pneu.getCodEmpresa());
        this.operacoesBloqueadas.validateUnidade(pneu.getCodUnidade());
        validatePneuToInsert(pneu);
        return this.dao.save(pneu);
    }

    private void validatePneuToInsert(@NotNull final PneuEntity pneu) {
        if (this.dao.exists(Example.of(pneu))) {
            throw new EntityExistsException("Pneu já existe no banco de dados.");
        }
    }
}

