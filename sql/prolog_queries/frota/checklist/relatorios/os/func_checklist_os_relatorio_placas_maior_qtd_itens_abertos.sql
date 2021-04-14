create or replace function
    func_checklist_os_relatorio_placas_maior_qtd_itens_abertos(f_cod_unidades bigint[],
                                                               f_total_placas_para_buscar integer)
    returns table
            (
                nome_unidade                      text,
                placa                             text,
                quantidade_itens_abertos          bigint,
                quantidade_itens_criticos_abertos bigint
            )
    language plpgsql
as
$$
declare
    status_itens_abertos char    := 'P';
    prioridade_critica   varchar := 'CRITICA';
begin
    return query
        with placas as (
            select v.placa                   as placa_veiculo,
                   count(cosi.codigo)        as quantidade_itens_abertos,
                   count(case
                             when cap.prioridade = prioridade_critica
                                 then 1 end) as quantidade_itens_criticos_abertos
            from checklist_ordem_servico_itens cosi
                     join checklist_ordem_servico cos
                          on cosi.cod_os = cos.codigo
                              and cosi.cod_unidade = cos.cod_unidade
                              -- Este filtro é importante! Ele nos previne de selecionar muitas OSs, filtrando aqui com
                              -- o index que existe na COS. Vindo menos linhas aqui, menos linhas também são
                              -- trazidas no join com a CAP abaixo. Assim, preveninos de usar disco.
                              and cos.cod_unidade = any (f_cod_unidades)
                     join checklist_alternativa_pergunta cap
                          on cosi.cod_alternativa_primeiro_apontamento = cap.codigo
                     join checklist c
                          on c.codigo = cos.cod_checklist
                     join veiculo v on c.cod_veiculo = v.codigo
            where c.cod_unidade = any (f_cod_unidades)
              and cosi.status_resolucao = status_itens_abertos
            group by v.codigo
            order by quantidade_itens_abertos desc,
                     v.codigo
            limit f_total_placas_para_buscar
        )

        select u.nome::text as nome_unidade,
               p.placa_veiculo::text,
               p.quantidade_itens_abertos,
               p.quantidade_itens_criticos_abertos
        from placas p
                 join veiculo v on v.placa = p.placa_veiculo
                 join unidade u on v.cod_unidade = u.codigo;
end;
$$;