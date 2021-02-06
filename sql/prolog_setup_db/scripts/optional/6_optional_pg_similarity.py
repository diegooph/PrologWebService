from yoyo import step
import os
import os.path


def do_step(conn):
    from sys import platform as _platform

    if _platform == "linux" or _platform == "linux2":
        print("Instalando a extensão pg_similarity no Linux, siga as instruções.")
        pgVersion = input("Por favor, digite a versão do seu PG (11 ou 12)")
        print(os.system("sudo apt-get update"))
        print(os.system("sudo apt-get install postgresql-"+pgVersion+"-similarity"))
    elif _platform == "darwin":
        print("Instalando a extensão pg_similarity no MAC.")
        os.chdir(os.path.join(os.getcwd(), "pg_similarity"))
        print(os.system("USE_PGXS=1 make"))
        print(os.system("USE_PGXS=1 make install"))

step(do_step)