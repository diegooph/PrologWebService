-- Para identificar se um mapa tem um colaborador vinculado no Prolog, é preciso que o código da unidade do colaborador
-- e a matrícula ambev tenham um correspondente na tabela mapa.
--
-- Um mapa pode retornar mais de uma vez, sendo o máximo de 3: uma para o motorista e outras duas para os
-- dois ajudantes.
--
-- Obs.: existem casos em que podem vir dados duplicados, precisamos manter com 'union'.
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

