-- Busca todas as tabelas e os indexes que elas possuem.
select
     n.nspname  as "Schema"
    ,t.relname  as "Table"
    ,c.relname  as "Index"
from
          pg_catalog.pg_class c
     join pg_catalog.pg_namespace n on n.oid        = c.relnamespace
     join pg_catalog.pg_index i     on i.indexrelid = c.oid
     join pg_catalog.pg_class t     on i.indrelid   = t.oid
where
        c.relkind = 'i'
    and n.nspname not in ('pg_catalog', 'pg_toast')
    and pg_catalog.pg_table_is_visible(c.oid)
order by
     n.nspname
    ,t.relname
    ,c.relname;

-- The following query will list all foreign key constraints in the database that do not have an index on the source
-- columns:
SELECT c.conrelid::regclass AS "table",
       /* list of key column names in order */
       string_agg(a.attname, ',' ORDER BY x.n) AS columns,
       pg_catalog.pg_size_pretty(
          pg_catalog.pg_relation_size(c.conrelid)
       ) AS size,
       c.conname AS constraint,
       c.confrelid::regclass AS referenced_table
FROM pg_catalog.pg_constraint c
   /* enumerated key column numbers per foreign key */
   CROSS JOIN LATERAL
      unnest(c.conkey) WITH ORDINALITY AS x(attnum, n)
   /* name for each key column */
   JOIN pg_catalog.pg_attribute a
      ON a.attnum = x.attnum
         AND a.attrelid = c.conrelid
WHERE NOT EXISTS
        /* is there a matching index for the constraint? */
        (SELECT 1 FROM pg_catalog.pg_index i
         WHERE i.indrelid = c.conrelid
           /* the first index columns must be the same as the
              key columns, but order doesn't matter */
           AND (i.indkey::smallint[])[0:cardinality(c.conkey)-1]
               @> c.conkey)
  AND c.contype = 'f'
GROUP BY c.conrelid, c.conname, c.confrelid
ORDER BY pg_catalog.pg_relation_size(c.conrelid) DESC;