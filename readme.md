MISSÃO

Quero que você refaça no seu projeto, mas seguindo essas regras:

🎯 PASSO 1 — criar estrutura

Cria:

app/
routes/
schemas/
🎯 PASSO 2 — mover schema
tira a classe Tarefa do main.py
cria um arquivo só pra isso
🎯 PASSO 3 — mover rotas
cria routes/tarefas.py
usa APIRouter
troca @app por @router
🎯 PASSO 4 — conectar tudo no main
importa as rotas
usa include_router
🎯 PASSO 5 — melhorar lógica

Implementa:

prioridade automática
erro 404 quando não achar tarefa
status 201 no POST
🎯 PASSO 6 — validação
título com mínimo 3 caracteres