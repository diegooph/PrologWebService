revoke all privileges on all tables in schema public from username_here;
revoke all privileges on all sequences in schema public from username_here;
revoke all privileges on all functions in schema public from username_here;

revoke all privileges on all tables in schema implantacao from username_here;
revoke all privileges on all sequences in schema implantacao from username_here;
revoke all privileges on all functions in schema implantacao from username_here;

revoke all privileges on all tables in schema piccolotur from username_here;
revoke all privileges on all sequences in schema piccolotur from username_here;
revoke all privileges on all functions in schema piccolotur from username_here;

revoke all privileges on all tables in schema integracao from username_here;
revoke all privileges on all sequences in schema integracao from username_here;
revoke all privileges on all functions in schema integracao from username_here;

revoke all privileges on all tables in schema suporte from username_here;
revoke all privileges on all sequences in schema suporte from username_here;
revoke all privileges on all functions in schema suporte from username_here;

revoke all privileges on schema integracao from username_here;
revoke all privileges on schema piccolotur from username_here;
revoke all privileges on schema log from username_here;
revoke all privileges on schema implantacao from username_here;
revoke all privileges on schema public from username_here;
revoke all privileges on schema suporte from username_here;

revoke usage on schema integracao from username_here;
revoke usage on schema piccolotur from username_here;
revoke usage on schema log from username_here;
revoke usage on schema implantacao from username_here;
revoke usage on schema public from username_here;
revoke usage on schema suporte from username_here;

-- If you want to drop:
drop user username_here;

-- If you want to restrict access:
alter user username_here with nologin;