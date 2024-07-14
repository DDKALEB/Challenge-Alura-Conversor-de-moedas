# Challenge-Alura-Conversor-de-moedas
Passo 1: Configuração do Ambiente Java
Para começar, obtive Java Development Kit (JDK) instalado em sua máquina. Além disso, usei um IDE (Integrated Development Environment) como Eclipse, IntelliJ IDEA ou NetBeans para desenvolver o projeto.
Passo 2: Criação do Projeto
Criei um novo projeto Java no IDE 
Passo 3: Consumo da API
Para obter as taxas de câmbio em tempo real, vamos usar a API do ExchangeRate-API. Crie uma conta gratuita e obtenha uma chave de API.
Criei uma classe ApiConnector para consumir a API:

Substitua YOUR_API_KEY com sua chave de API.

Your API Key: d4365a2b5b0e5617dd7022cab
Example Request: https://v6.exchangerate-api.com/v6/d4365a2b5b0e56187dd7022ca/latest/USD

Passo 4: Análise da Resposta JSON
Criei uma classe Currency para representar as moedas:
Criei uma classe CurrencyParser para analisar a resposta JSON e criar objetos Currency:
Passo 5: Filtro de Moedas
Criei uma classe CurrencyFilter para filtrar as moedas de interesse:
Passo 6: Exibição de Resultados aos Usuários
Criei uma classe ConsoleUI para interagir com os usuários via console:

o código está completo e pronto para ser executado!
