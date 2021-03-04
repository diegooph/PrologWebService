begin transaction;
-- #####################################################################################################################
delete
from pneu_servico_realizado_incrementa_vida
where cod_servico_realizado in (select codigo
                                     from pneu_servico_realizado
                                     where cod_pneu in (select cod_pneu_cadastro_prolog
                                                        from integracao.pneu_cadastrado pc
                                                        where pc.cod_empresa_cadastro = 58));

delete
from pneu_servico_cadastro
where cod_pneu in (select cod_pneu_cadastro_prolog
                   from integracao.pneu_cadastrado pc
                   where pc.cod_empresa_cadastro = 58);

with dados as (select psriv.cod_servico_realizado
               from pneu_servico_realizado psr
                        join pneu_servico_realizado_incrementa_vida psriv
                             on psr.codigo = psriv.cod_servico_realizado
                                 and psr.fonte_servico_realizado = psriv.fonte_servico_realizado
               where psr.cod_pneu in
                     (select cod_pneu_cadastro_prolog
                      from integracao.pneu_cadastrado pc
                      where pc.cod_empresa_cadastro = 58)
)

delete
from pneu_servico_realizado_incrementa_vida
where cod_servico_realizado in (select d.cod_servico_realizado from dados d);

delete
from pneu_servico_realizado
where cod_pneu in (select cod_pneu_cadastro_prolog
                   from integracao.pneu_cadastrado pc
                   where pc.cod_empresa_cadastro = 58);
-- #####################################################################################################################
-- #####################################################################################################################
delete
from pneu_transferencia_informacoes
where cod_pneu in (select cod_pneu_cadastro_prolog
                   from integracao.pneu_cadastrado pc
                   where pc.cod_empresa_cadastro = 58);
delete
from pneu_transferencia_processo
where codigo not in (select pti.cod_processo_transferencia from pneu_transferencia_informacoes pti);
-- #####################################################################################################################

DROP TRIGGER tg_func_audit_veiculo_pneu ON veiculo_pneu;

delete
from veiculo_pneu
where cod_pneu in (select cod_pneu_cadastro_prolog
                   from integracao.pneu_cadastrado pc
                   where pc.cod_empresa_cadastro = 58);

CREATE TRIGGER TG_FUNC_AUDIT_VEICULO_PNEU
  AFTER INSERT OR UPDATE OR DELETE
  ON VEICULO_PNEU
  FOR EACH ROW EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();
-- #####################################################################################################################
delete
from afericao_valores_data
where cod_pneu in (select cod_pneu_cadastro_prolog
                   from integracao.pneu_cadastrado pc
                   where pc.cod_empresa_cadastro = 58);
delete
from afericao_manutencao_data
where cod_pneu in (select cod_pneu_cadastro_prolog
                   from integracao.pneu_cadastrado pc
                   where pc.cod_empresa_cadastro = 58);
delete
from afericao_data
where placa_veiculo in (select placa_veiculo_cadastro
                        from integracao.veiculo_cadastrado vc
                        where vc.cod_empresa_cadastro = 58);
-- #####################################################################################################################
-- #####################################################################################################################
delete
from integracao.pneu_cadastrado pc
where pc.cod_empresa_cadastro = 58;

DROP TRIGGER tg_func_audit_pneu ON pneu_data;

delete
from pneu_data
where cod_empresa = 58;

CREATE TRIGGER TG_FUNC_AUDIT_PNEU
    AFTER INSERT OR UPDATE OR DELETE
    ON PNEU_DATA
    FOR EACH ROW
EXECUTE PROCEDURE AUDIT.FUNC_AUDIT();
-- #####################################################################################################################
delete from integracao.veiculo_cadastrado where cod_empresa_cadastro = 58;

delete from veiculo_data where cod_unidade in (select u.codigo from unidade u where u.cod_empresa = 58);
-- #####################################################################################################################
end transaction;