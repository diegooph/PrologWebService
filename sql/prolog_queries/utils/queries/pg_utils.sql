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