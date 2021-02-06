-- Sobre:
-- Retorna placas que possuem itens críticos em aberto e estão bloqueadas.
--
-- Histórico:
-- 2020-10-27 -> Function criada (thaisksf - PL-2791).
create or replace function func_checklist_os_relatorio_get_placas_bloqueadas(f_cod_unidades bigint[])
    returns table
            (
                nome_unidade          text,
                placa_bloqueada       text,
                data_hora_abertura_os text,
                qtd_itens_criticos    bigint
            )
    language sql
    strict
as
$$;
        select u.nome ::text                                   as nome_unidade,
               v.placa ::text                                  as placa_bloqueada,
               to_char(max(c.data_hora), 'DD/MM/YYYY HH24:MI') as data_hora_abertura_os,
               count(cap.prioridade)                           as qtd_itens_criticos
        from unidade u
                 join checklist c
                      on c.cod_unidade = u.codigo
                 join veiculo v on v.codigo = c.cod_veiculo
                 join checklist_ordem_servico cos
                      on cos.cod_unidade = any (f_cod_unidades)
                          and cos.status = 'A'
                          and c.codigo = cos.cod_checklist
                 join checklist_ordem_servico_itens cosi
                      on cosi.status_resolucao = 'P'
                          and cos.codigo = cosi.cod_os
                          and cosi.cod_unidade = cos.cod_unidade
                 join checklist_alternativa_pergunta cap
                      on cosi.cod_alternativa_primeiro_apontamento = cap.codigo
                          and cap.prioridade = 'CRITICA'
        where cos.cod_unidade = any (f_cod_unidades)
        group by v.placa, u.nome
        order by qtd_itens_criticos desc;
$$;