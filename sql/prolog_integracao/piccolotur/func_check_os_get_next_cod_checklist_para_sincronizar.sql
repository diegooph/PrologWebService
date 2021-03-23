-- Sobre:
--
-- Function utilizada pela integração da Piccolotur para buscar o código do checklist que deverá ser sincronizado. Agora
-- essa function também retorna um booleano indicando que é o último código de checklist a ser sincronizado.
--
-- Essa function utiliza uma flag presente na tabela 'piccolotur.checklist_pendente_para_sincronizar' para saber qual é
-- o checklist que ela deve retornar. Caso não possua nenhuma flag setada na tabela, a function irá retornar o menor
-- código de checklist que precisa ser sincronizado e cujo não está sincronizado ainda.
-- Para induzir se está processando a última linha, a function verifica se a flag está associada ao código de checklist
-- maior.
--
-- É responsabilidade da function setar a flag para o próximo código de checklist a ser sincronizado.
--
-- Histórico:
-- 2019-08-07 -> Function criada (diogenesvanzella - PL-2212).
-- 2019-08-13 -> Adiciona variável de retorno (diogenesvanzella - PL-2212).
-- 2019-08-29 -> Corrige nome da propriedade da function (diogenesvanzella - PL-2212).
-- 2020-01-14 -> Melhora comentários da function (diogenesvanzella - PL-2416).
-- 2020-02-10 -> Evita que mais de um código seja retornado para sincronizar (diogenesvanzella - PLI-69).
-- 2020-02-25 -> Alteração do nome da function (diogenesvanzella - PLI-70).
-- 2020-10-09 -> Verifica se o checklist está com sincronia bloqueada (didivz - PL-2944).
create or replace function piccolotur.func_check_os_get_next_cod_checklist_para_sincronizar()
    returns table
            (
                cod_checklist bigint,
                is_last_cod   boolean
            )
    language plpgsql
as
$$
declare
    cod_checklist bigint;
    is_last_cod   boolean;
begin
    --   1° - verifica se existe um checklist para sincronizar, se não, seta o de menor código como apto a
    --   sincronização.
    if ((select cod_checklist_para_sincronizar
         from piccolotur.checklist_pendente_para_sincronizar
         where next_to_sync is true
           and sincronizado is false
           and precisa_ser_sincronizado is true
           and bloqueado_sincronia is false
         limit 1) is null)
    then
        update piccolotur.checklist_pendente_para_sincronizar
        set next_to_sync = true
        where cod_checklist_para_sincronizar = (select cpps.cod_checklist_para_sincronizar
                                                from piccolotur.checklist_pendente_para_sincronizar cpps
                                                where cpps.sincronizado is false
                                                  and cpps.precisa_ser_sincronizado is true
                                                  and bloqueado_sincronia is false
                                                order by cpps.cod_checklist_para_sincronizar
                                                limit 1);
    end if;

    --   2° - Verifica se o código marcado para sincronizar é o último código a ser sincronizado
    select cpps.next_to_sync
    from piccolotur.checklist_pendente_para_sincronizar cpps
    where cpps.precisa_ser_sincronizado
      and cpps.sincronizado is false
      and bloqueado_sincronia is false
    order by cpps.cod_checklist_para_sincronizar desc
    limit 1
    into is_last_cod;

    --   3° - Pega o código que está marcado para tentar sincronizar. Utilizamos limit 1 para evitar que mais de um
    --   código seja setado.
    select cod_checklist_para_sincronizar
    from piccolotur.checklist_pendente_para_sincronizar
    where next_to_sync = true
    order by cod_checklist_para_sincronizar desc
    limit 1
    into cod_checklist;

    --   4° - Remove a marcação do checklist que estava marcado par sincronizar
    update piccolotur.checklist_pendente_para_sincronizar
    set next_to_sync = false
    where cod_checklist_para_sincronizar = cod_checklist;

    --   5° - Marca o próximo código que precisa ser sincronizado, se for o último código, então seta o
    -- primeiro como o próximo a ser sincronizado
    if is_last_cod
    then
        update piccolotur.checklist_pendente_para_sincronizar
        set next_to_sync = true
        where cod_checklist_para_sincronizar = (select cpps.cod_checklist_para_sincronizar
                                                from piccolotur.checklist_pendente_para_sincronizar cpps
                                                where cpps.sincronizado is false
                                                  and cpps.precisa_ser_sincronizado is true
                                                  and bloqueado_sincronia is false
                                                order by cpps.cod_checklist_para_sincronizar
                                                limit 1);
    else
        update piccolotur.checklist_pendente_para_sincronizar
        set next_to_sync = true
        where cod_checklist_para_sincronizar = (select cod_checklist_para_sincronizar
                                                from piccolotur.checklist_pendente_para_sincronizar
                                                where sincronizado is false
                                                  and precisa_ser_sincronizado is true
                                                  and next_to_sync is false
                                                  and bloqueado_sincronia is false
                                                  and cod_checklist_para_sincronizar > cod_checklist
                                                order by cod_checklist_para_sincronizar
                                                limit 1);
    end if;

    --   6° - Retorna o código que será sincronizado
    return query
        select cod_checklist, is_last_cod;
end;
$$;