-- Sobre:
--
-- Método utilizado para buscar a quantidade de Itens apontados como Não Ok (NOK) em uma Placa,
-- agrupando por prioridade do Item. A quantidade de Itens pode ser filtrada
-- pelo status em que o Item se encontra, podendo ser Pendente ou Resolvido
--
-- Para buscar a quantidade de Itens apontados de TODOS os veículos o parâmetro placa deve ser NULL
--
-- Esta busca utiliza paginação, então deve-se explicitar a quantidade de dados que serão buscados
-- através dos parâmetros limit e offset.
--
-- Histórico:
-- 2020-03-02 -> Atualização de arquivo e documentação (wvinim - PL-2494).
-- 2020-11-09 -> Impede de trazer itens de O.S. de outra empresa (luizfp - PL-3285).
create or replace function func_checklist_os_get_qtd_itens_placa_listagem(f_cod_unidade bigint,
                                                                          f_cod_tipo_veiculo bigint,
                                                                          f_placa_veiculo text,
                                                                          f_status_itens_os text,
                                                                          f_limit integer,
                                                                          f_offset integer)
    returns table
            (
                placa_veiculo                text,
                qtd_itens_prioridade_critica bigint,
                qtd_itens_prioridade_alta    bigint,
                qtd_itens_prioridade_baixa   bigint,
                total_itens                  bigint
            )
    language plpgsql
as
$$
declare
    tipo_item_prioridade_critica text := 'CRITICA';
    tipo_item_prioridade_alta    text := 'ALTA';
    tipo_item_prioridade_baixa   text := 'BAIXA';
begin
    return query
        select v.placa :: text           as placa_veiculo,
               count(case
                         when cap.prioridade = tipo_item_prioridade_critica
                             then 1 end) as qtd_itens_prioridade_critica,
               count(case
                         when cap.prioridade = tipo_item_prioridade_alta
                             then 1 end) as qtd_itens_prioridade_alta,
               count(case
                         when cap.prioridade = tipo_item_prioridade_baixa
                             then 1 end) as qtd_itens_prioridade_baixa,
               count(cap.prioridade)     as total_itens
        from veiculo v
                 join checklist c
                      -- Queremos apenas veículos da unidade onde o checklist foi feito.
                      -- Isso evita de trazer itens de O.S. de outra empresa em caso de transferência de veículos.
                      on v.placa = c.placa_veiculo and v.cod_unidade = c.cod_unidade
                 join checklist_ordem_servico cos
                      on c.codigo = cos.cod_checklist
                 join checklist_ordem_servico_itens cosi
                      on cos.codigo = cosi.cod_os
                          and cos.cod_unidade = cosi.cod_unidade
                 join checklist_alternativa_pergunta cap
                      on cap.codigo = cosi.cod_alternativa_primeiro_apontamento
                 join veiculo_tipo vt
                      on v.cod_tipo = vt.codigo
        where v.cod_unidade = f_cod_unidade
          and case when f_cod_tipo_veiculo is null then true else vt.codigo = f_cod_tipo_veiculo end
          and case when f_placa_veiculo is null then true else v.placa = f_placa_veiculo end
          and case when f_status_itens_os is null then true else cosi.status_resolucao = f_status_itens_os end
        group by v.placa
        order by qtd_itens_prioridade_critica desc,
                 qtd_itens_prioridade_alta desc,
                 qtd_itens_prioridade_baixa desc,
                 placa_veiculo asc
        limit f_limit offset f_offset;
end;
$$;