-- Adicionado flag para verificar se o Diagrama possui ou não motor (thaisksf - PL-3215).
alter table veiculo_diagrama
    add column motorizado boolean;

update veiculo_diagrama vd
set motorizado = true
where codigo in (1, 2, 4, 6, 7, 8, 10, 12, 13, 14, 16, 19, 24);

update veiculo_diagrama vd
set motorizado = false
where codigo not in (1, 2, 4, 6, 7, 8, 10, 12, 13, 14, 16, 19, 24);

alter table veiculo_diagrama alter column motorizado set not null;