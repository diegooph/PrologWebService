create or replace function func_checklist_get_modelos_selecao_realizacao(f_cod_unidade bigint,
                                                                         f_cod_cargo bigint)
    returns table
            (
                cod_modelo              bigint,
                cod_versao_atual_modelo bigint,
                cod_unidade_modelo      bigint,
                nome_modelo             text,
                cod_veiculo             bigint,
                placa_veiculo           text,
                km_atual_veiculo        bigint
            )
    language sql
as
$$
select cm.codigo           as cod_modelo,
       cm.cod_versao_atual as cod_versao_atual_modelo,
       cm.cod_unidade      as cod_unidade_modelo,
       cm.nome :: text     as nome_modelo,
       v.codigo            as cod_veiculo,
       v.placa :: text     as placa_veiculo,
       v.km                as km_atual_veiculo
from checklist_modelo cm
         join checklist_modelo_funcao cmf
              on cmf.cod_checklist_modelo = cm.codigo and cm.cod_unidade = cmf.cod_unidade
         join checklist_modelo_veiculo_tipo cmvt
              on cmvt.cod_modelo = cm.codigo and cmvt.cod_unidade = cm.cod_unidade
         join veiculo_tipo vt
              on vt.codigo = cmvt.cod_tipo_veiculo
         join veiculo v
              on v.cod_tipo = vt.codigo and v.cod_unidade = cm.cod_unidade
where cm.cod_unidade = f_cod_unidade
  and cmf.cod_funcao = f_cod_cargo
  and cm.status_ativo = true
  and v.status_ativo = true
order by cm.codigo, v.placa
$$;