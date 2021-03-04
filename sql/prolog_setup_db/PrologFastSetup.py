from yoyo import read_migrations
from yoyo import get_backend
from psycopg2 import connect, extensions, sql
import os

print("\nScript automático para gerar bancos locais de teste")
print("\nDigite os dados de acesso ao servidor")

username = input("username: ")
password = input("password: ")

print("\nDigite o nome do banco a ser criado")

dbname = input("database: ")

progresspath = input("Caso deseje executar scripts em progress, digite a pasta: ")

print("\nCriando banco de dados: " + dbname)

# Declare a new PostgreSQL connection object.
conn = connect(
    dbname="postgres",
    user=username,
    host="localhost",
    password=password,
    port="5432"
)

# Get the isolation leve for autocommit.
autocommit = extensions.ISOLATION_LEVEL_AUTOCOMMIT

# Set the isolation level for the connection's cursors
# Will raise ActiveSqlTransaction exception otherwise.
conn.set_isolation_level(autocommit)

# Instantiate a cursor object from the connection.
cursor = conn.cursor()

# Use the sql module to avoid SQL injection attacks.
cursor.execute(sql.SQL(
    "CREATE DATABASE {}"
).format(sql.Identifier(dbname)))

# Close the cursor to avoid memory leaks.
cursor.close()

# Close the connection to avoid memory leaks.
conn.close()

backend = get_backend("postgres://" + username + ":" + password + "@localhost/" + dbname)
migrationsBase = read_migrations(os.path.join(os.path.join(os.getcwd(), "scripts"), "base"))
baseDir = os.path.dirname(os.getcwd())
migrationsDone = read_migrations(os.path.join(os.path.join(baseDir, "migrations"), "done"))
if progresspath:
    migrationsProgress = read_migrations(os.path.join(os.path.join(baseDir, "migrations/progress"), progresspath))
with backend.lock():
    # Apply migrations.
    print("Aplicando migrations da pasta scripts/base")
    backend.apply_migrations(backend.to_apply(migrationsBase))
    print("Aplicando migrations da pasta migrations/done")
    backend.apply_migrations(backend.to_apply(migrationsDone))
    # Apply progress migrations (optional).
    if progresspath:
        print("Aplicando progress da pasta migrations/progress/" + progresspath)
        backend.apply_migrations(backend.to_apply(migrationsProgress))
    print("Banco de dados pronto para uso!")
