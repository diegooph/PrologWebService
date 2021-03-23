select *,
       s.total_time / s.calls as avg_exec_time
from pg_stat_user_functions s;