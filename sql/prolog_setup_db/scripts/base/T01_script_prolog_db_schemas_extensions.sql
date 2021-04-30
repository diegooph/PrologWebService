--
-- file: scripts/01_script_prolog_db_schemas_extensions.sql
--

create extension if not exists citext;
create extension if not exists fuzzystrmatch;
create extension if not exists pg_trgm;
create extension if not exists unaccent;
create extension if not exists postgis;

create schema aferidor;
create schema agendador;
create schema audit;
create schema audit_implantacao;
create schema avilan;
create schema backup;
create schema comercial;
create schema cs;
create schema implantacao;
create schema integracao;
create schema piccolotur;
create schema prolog_analises;
create schema raizen;
create schema suporte;
create schema messaging;
create schema migration_checklist;