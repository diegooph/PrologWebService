with cos_para_deletar as (
    select *
    from checklist_ordem_servico cos
             left join checklist_ordem_servico_itens cosi on cos.cod_unidade = cosi.cod_unidade
        and cos.codigo = cosi.cod_os
    where cosi.codigo is null
)

update checklist_ordem_servico_data
set deletado            = true,
    motivo_delecao      = 'PL-3603',
    data_hora_deletado  = now(),
    pg_username_delecao = SESSION_USER
where codigo_prolog in (select cos_para_deletar.codigo_prolog from cos_para_deletar);