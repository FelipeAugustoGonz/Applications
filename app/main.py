from fastapi import FastAPI
from pydantic import BaseModel
from typing import List

app = FastAPI()
tarefas = []

class Tarefa (BaseModel):
    titulo : str
    descricao : str
    concluida: bool = False

@app.get("/")
def read_root():
    return {"mensagem": "Olá, seja bem-vindo à minha API!"}

@app.post("/tarefas")
def adicionartarefas(tarefa : Tarefa):
    nova_tarefas = {
    "id": len(tarefas) + 1,
    "titulo": tarefa.titulo,
    "descricao": tarefa.descricao,
    "concluida": False
    }

    tarefas.append(nova_tarefas)

    return {"mensagem": "Nova tarefa criada com sucesso!", "tarefa:" : nova_tarefas}

# PUT /tarefas/{id}
# {
#        "concluida" : True
#  }
@app.put("/tarefas/{id}")
def  marcarConclusao(id : int) :
    #tem que percorrer a lista tarefas, encontrar a tarefa com id correspondente e alterar o campo para concluida
    for tarefa in tarefas:
        if tarefa["id"] == id:
            tarefa["concluida"] = True
            return tarefa
        else :
            return {"erro": "Tarefa não encontrada"}
@app.delete("/tarefas/{id}")
def deletarTarefa( id : int):
    global tarefas
    tarefas = [t for t in tarefas if t["id"] != id]
    return {"mensagem" : "Tarefa excluida com sucesso!"}

@app.get("/tarefas")
def listar_tarefas():
    return tarefas