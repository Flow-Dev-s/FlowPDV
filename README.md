# 🌊 FlowPDV - Sistema de Gestão ERP & PDV

<div align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/JavaFX-000000?style=for-the-badge&logo=java&logoColor=white" alt="JavaFX">
  <img src="https://img.shields.io/badge/H2_Database-003B57?style=for-the-badge&logo=sqlite&logoColor=white" alt="H2">
  <img src="https://img.shields.io/badge/Linux_Ready-FCC624?style=for-the-badge&logo=linux&logoColor=black" alt="Linux">
</div>

<br>

**FlowPDV** é um Sistema de Gestão de Ponto de Venda (PDV) e ERP, focado em agilidade de atendimento, controle de fluxo de caixa e integração direta com impressoras térmicas. 

Desenvolvido pela equipe **Flow Devs** como projeto acadêmico para o Departamento de Ciências Exatas (DCX) da Universidade Federal da Paraíba (UFPB).

---

## ✨ Principais Funcionalidades

* 🛒 **Frente de Caixa (Checkout):** Interface rápida com suporte a bipagem de código de barras e cálculo automático de subtotais, descontos e troco.
* 📦 **Gestão de Estoque:** Controle rigoroso de entrada e saída baseado em lotes (`stock_batches`), garantindo precisão no acompanhamento de produtos.
* 🖨️ **Impressão Térmica Nativa:** Integração robusta via protocolo RAW/Socket, permitindo a impressão instantânea de cupons não-fiscais (ESC/POS) com suporte nativo a acentuação (UTF-8).
* 🌙 **Design Moderno:** Interface de usuário construída com JavaFX, estilizada com CSS customizado e janelas modais responsivas para melhor experiência do operador.

## 🚀 Tecnologias Utilizadas

O projeto foi construído utilizando as seguintes ferramentas:

* **Linguagem Core:** Java
* **Interface Gráfica:** JavaFX + CSS
* **Banco de Dados:** Banco relacional via JDBC (Estrutura compatível com H2 Database / MySQL)
* **Design Pattern:** MVC (Model-View-Controller) / Service Pattern
* **Ambiente de Testes:** Homologado com sucesso em sistemas Linux (Ubuntu) e emuladores térmicos escritos em Rust.

## 👥 Equipe: Flow Devs

* **Albiere de Lima**
* **Ana Helena Barbosa**
* **Felipe Santos**
* **Luigi Truglio**

## ⚙️ Como Executar o Projeto

**Pré-requisitos:**
* JDK 17 ou superior instalado.
* Banco de dados configurado com as credenciais padrões do projeto.

**Passos:**
1. Clone este repositório:
   ```bash
   git clone https://github.com/Flow-Dev-s/FlowPDV.git
   ```
