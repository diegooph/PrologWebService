-- Detecta os usos da placa como constraint em outras tabelas.
select distinct r.table_name
from information_schema.constraint_column_usage u
         inner join information_schema.referential_constraints fk
                    on u.constraint_catalog = fk.unique_constraint_catalog
                        and u.constraint_schema = fk.unique_constraint_schema
                        and u.constraint_name = fk.unique_constraint_name
         inner join information_schema.key_column_usage r
                    on r.constraint_catalog = fk.constraint_catalog
                        and r.constraint_schema = fk.constraint_schema
                        and r.constraint_name = fk.constraint_name
where u.column_name = 'placa'
  and u.table_schema = 'public'
  and u.table_name = 'veiculo_data';

-- Tabelas do GSD podem ser removidas, funcionalidade não existe mais.
drop table gsd_respostas;
drop table gsd_perguntas;
drop table pdv_gsd;
drop table gsd;

-- Tabela pode ser dropada, não é mais utilizada.
drop table veiculo_pneu_inconsistencia;

-- Tabelas que ainda utilizam a placa:
-- afericao_data
-- checklist_data
-- veiculo_pneu
-- movimentacao_origem
-- movimentacao_destino

-- Solução proposta:
--
-- A ideia é que façamos a migração em TRÊS partes.
--
------------------------------------------------------------------------------------------------------------------------
-- Primeira parte: iremos remover os usos da placa como FK para as tabelas que ainda à utilizam.
-- Detalhes:
-- Devemos adotar uma proposta diferente para lidar com cada tabela que ainda precisa ser migrada, pois cada caso
-- tem suas peculiaridades.
--
-- AFERICAO_DATA:
-- Nessa tabela, devemos alterar o insert e updates (se existirem - suporte) para não inserirem mais a placa
-- e sim o código do veículo.
-- Como essa tabela já utiliza uma VIEW chamada AFERICAO, podemos alterar a VIEW para que ela continue trazendo a PLACA.
-- Assim não iremos precisar alterar todas as buscas que lidam com essa tabela. Apenas verificar os locais que
-- explicitamente continuam usando AFERICAO_DATA.
--
-- CHECKLIST_DATA:
-- Nessa tabela, devemos alterar o insert e updates (se existem - suporte) para não inserirem mais a placa
-- e sim o código do veículo.
-- Como essa tabela já utiliza uma VIEW chamada CHECKLIST, podemos alterar a VIEW para que ela continue trazendo a PLACA.
-- Assim não iremos precisar alterar todas as buscas que lidam com essa tabela. Apenas verificar os locais que
-- explicitamente continuam usando CHECKLIST_DATA.
--
-- VEICULO_PNEU:
-- Essa tabela não possui uma VIEW, então o trabalho de testes que daria pra criar uma VIEW dela, não justifica o
-- empenho. Ou seja, para ela, iremos trocar a placa pelo código e alterar todos os locais do Prolog que usam a placa
-- presente nessa tabela para algum INSERT/UPDATE/SELECT/DELETE.
--
-- MOVIMENTACAO_ORIGEM e MOVIMENTACAO_DESTINO:
-- Assim como a tabela VEICULO_PNEU, as de movimentação não possuem VIEWs. Não valendo a pena criar por conta do teste
-- necessário.
-- Iremos trocar a placa pelo código e alterar todos os locais do Prolog que usam a placa presente nessa tabela para
-- algum INSERT/UPDATE/SELECT/DELETE.
--
-- As tabelas de movimentação e a VEICULO_PNEU serão as que darão mais trabalho para migrar.
--
-- Perceba que essa primeira parte da solução não remove a placa como PK, apenas remove as dependências dela.
------------------------------------------------------------------------------------------------------------------------
--
------------------------------------------------------------------------------------------------------------------------
-- Segunda parte: iremos remover a placa como PK, tornar o código PK e fazer a placa ser unique por empresa.
------------------------------------------------------------------------------------------------------------------------
--
------------------------------------------------------------------------------------------------------------------------
-- Terceira parte: iremos remover a placa das VIEWs CHECKLIST e AFERICAO, isso irá impactar na refatoração de todos
-- os locais que usam essas placas. Mas, ao mesmo tempo, vai deixar mais otimizado os locais que não usam. Essa parte
-- talvez possa ser dividida em outras duas, uma para cada VIEW.
------------------------------------------------------------------------------------------------------------------------