from flask import Flask, jsonify, request, send_from_directory
import json
import os
import re

import requests


app = Flask(__name__, static_folder="static")

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
TASKS_FILE = os.path.join(BASE_DIR, "tarefas.json")

OPENROUTER_API_KEY = os.getenv(
    "OPENROUTER_API_KEY",
    "",
)
OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions"


def carregar_tarefas():
    if not os.path.exists(TASKS_FILE) or os.path.getsize(TASKS_FILE) == 0:
        return []

    try:
        with open(TASKS_FILE, "r", encoding="utf-8") as arquivo:
            dados = json.load(arquivo)
            return dados if isinstance(dados, list) else []
    except (json.JSONDecodeError, OSError):
        return []


def salvar_tarefas(tarefas):
    with open(TASKS_FILE, "w", encoding="utf-8") as arquivo:
        json.dump(tarefas, arquivo, ensure_ascii=False, indent=2)


def normalizar_texto(texto):
    return re.sub(r"\s+", " ", texto.strip())


def executar_comando(mensagem):
    texto = normalizar_texto(mensagem)
    texto_lower = texto.lower()

    if texto_lower.startswith("adicionar tarefa "):
        tarefa = texto[len("adicionar tarefa ") :].strip()
        if not tarefa:
            return "Diga qual tarefa deseja adicionar."

        tarefas = carregar_tarefas()
        tarefas.append({"tarefa": tarefa, "status": "pendente"})
        salvar_tarefas(tarefas)
        return f"Tarefa adicionada: {tarefa}"

    if texto_lower == "listar tarefas":
        tarefas = carregar_tarefas()
        if not tarefas:
            return "Nenhuma tarefa cadastrada."

        linhas = []
        for indice, tarefa in enumerate(tarefas, start=1):
            nome = tarefa.get("tarefa", "")
            status = tarefa.get("status", "pendente")
            linhas.append(f"{indice}. {nome} ({status})")
        return "\n".join(linhas)

    if texto_lower.startswith("remover tarefa "):
        alvo = texto[len("remover tarefa ") :].strip()
        if not alvo:
            return "Diga qual tarefa deseja remover."

        tarefas = carregar_tarefas()
        tarefas_restantes = []
        removida = None

        for indice, tarefa in enumerate(tarefas, start=1):
            nome = tarefa.get("tarefa", "")
            remover_por_numero = alvo.isdigit() and int(alvo) == indice
            remover_por_nome = alvo.lower() in nome.lower()

            if removida is None and (remover_por_numero or remover_por_nome):
                removida = nome
                continue

            tarefas_restantes.append(tarefa)

        if removida is None:
            return f"Não encontrei a tarefa: {alvo}"

        salvar_tarefas(tarefas_restantes)
        return f"Tarefa removida: {removida}"

    return None


def perguntar_ia(mensagem):
    headers = {
        "Authorization": f"Bearer {OPENROUTER_API_KEY}",
        "Content-Type": "application/json",
        "HTTP-Referer": "http://localhost:5000",
        "X-Title": "Assistente Automacao",
    }

    payload = {
        "model": "openai/gpt-3.5-turbo",
        "messages": [
            {
                "role": "system",
                "content": "Você é um assistente pessoal técnico focado em produtividade, automação e projetos.",
            },
            {"role": "user", "content": mensagem},
        ],
    }

    try:
        resposta = requests.post(
            OPENROUTER_URL,
            headers=headers,
            json=payload,
            timeout=30,
        )
        resposta.raise_for_status()
        dados = resposta.json()
        return dados["choices"][0]["message"]["content"].strip()
    except (requests.RequestException, KeyError, IndexError, ValueError):
        return "Erro ao responder"


@app.route("/chat", methods=["POST"])
def chat():
    dados = request.get_json(silent=True) or {}
    mensagem = normalizar_texto(dados.get("message", ""))

    if not mensagem:
        return jsonify({"response": "Digite uma mensagem."})

    resposta_comando = executar_comando(mensagem)
    if resposta_comando is not None:
        return jsonify({"response": resposta_comando})

    resposta = perguntar_ia(mensagem)
    return jsonify({"response": resposta})


@app.route("/")
def home():
    return send_from_directory(app.static_folder, "index.html")


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
