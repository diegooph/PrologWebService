-- Query usada:
-- explain analyse
-- select * from view_mapa_colaborador;

-- Antes:
--- Planning Time: 1.738 ms
-- Execution Time: 2359.080 ms

-- Depois:
-- Planning Time: 1.908 ms
-- Execution Time: 94813.837 ms

-- Altera 'union all' por 'union', precisamos remover duplicatas.
create or replace view view_mapa_colaborador as
select m.mapa        as mapa,
       c.cpf         as cpf,
       c.cod_unidade as cod_unidade
from unidade_funcao_produtividade ufp
         join colaborador c
              on c.cod_unidade = ufp.cod_unidade
                  and c.cod_funcao = ufp.cod_funcao_motorista
         join mapa m
              on m.cod_unidade = c.cod_unidade
                  and m.matricmotorista = c.matricula_ambev
union
select m.mapa        as mapa,
       c.cpf         as cpf,
       c.cod_unidade as cod_unidade
from unidade_funcao_produtividade ufp
         join colaborador c
              on c.cod_unidade = ufp.cod_unidade
                  and c.cod_funcao = ufp.cod_funcao_ajudante
         join mapa m
              on m.cod_unidade = c.cod_unidade
                  and m.matricajud1 = c.matricula_ambev
union
select m.mapa        as mapa,
       c.cpf         as cpf,
       c.cod_unidade as cod_unidade
from unidade_funcao_produtividade ufp
         join colaborador c
              on c.cod_unidade = ufp.cod_unidade
                  and c.cod_funcao = ufp.cod_funcao_ajudante
         join mapa m
              on m.cod_unidade = c.cod_unidade
                  and m.matricajud2 = c.matricula_ambev;

comment on view view_mapa_colaborador
    is 'View utilizada para linkar os mapas relizados por cada colaborador';
