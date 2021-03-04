-- Busca os usos como constraint de uma coluna específica em todas as tabelas do schema informado.
select r.table_name
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

-- Lista todas FKs que a tabela filtrada possui.
SELECT
    tc.table_schema,
    tc.constraint_name,
    tc.table_name,
    kcu.column_name,
    ccu.table_schema AS foreign_table_schema,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM
    information_schema.table_constraints AS tc
    JOIN information_schema.key_column_usage AS kcu
      ON tc.constraint_name = kcu.constraint_name
      AND tc.table_schema = kcu.table_schema
    JOIN information_schema.constraint_column_usage AS ccu
      ON ccu.constraint_name = tc.constraint_name
      AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.table_name='veiculo_data';