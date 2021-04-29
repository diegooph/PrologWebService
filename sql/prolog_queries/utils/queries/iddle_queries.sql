select pid,
       now() - pg_stat_activity.query_start as duration,
       query,
       state
from pg_stat_activity
where (now() - pg_stat_activity.query_start) > interval '5 minutes';