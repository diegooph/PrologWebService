-- Queries running for long than 5 minutes.
select pid,
       now() - pg_stat_activity.query_start as duration,
       query,
       state
from pg_stat_activity
where (now() - pg_stat_activity.query_start) > interval '5 minutes';

-- Shows current locks being held.
select l.database as db_oid,
       d.datname,
       l.relation,
       n.nspname,
       c.relname,
       a.pid,
       a.usename,
       l.locktype,
       l.mode,
       l.granted,
       l.tuple
from pg_locks l
         join pg_class c on c.oid = l.relation
         join pg_namespace n on n.oid = c.relnamespace
         join pg_database d on d.oid = l.database
         join pg_stat_activity a on a.pid = l.pid
order by database,
         relation,
         pid;

-- Shows all permissions from an user.
select n.nspname as "Schema",
       case c.relkind
           when 'r' then 'table'
           when 'v' then 'view'
           when 'm' then 'materialized view'
           when 'S' then 'sequence'
           when 'f' then 'foreign table'
           end   as "Type"
from pg_catalog.pg_class c
         left join pg_catalog.pg_namespace n on n.oid = c.relnamespace
where pg_catalog.array_to_string(c.relacl, E'\n') like '%username%';