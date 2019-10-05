

# Prática CSLP - 1 
### por Diogo Silva, 89348 e Pedro Oliveira, [NMEC]

## Introdução
Serve o seguinte relatório como reportório, explicação e prova de execução dos exercícios propstos no primeiro guião prático da disciplina de Complementos de Linguagens de Programação (CSLP).

## Projetos Escolhidos
Para esta prática foi nos pedido que escolhessemos 2-3 projetos criados em prévias cadeiras, de forma a que pudessemos, sobre estes, aprender os conceitos de Documentação, GIT e Docker-Containers.
Escolhemos então os seguintes projetos:

### SpamBot Finder 
- **Cadeira**: Métodos Probabilísticos para Engenhaira Informática
- **Autores**: Diogo Silva, Vasco Rámos
- **Linguagem**: Java (8)
- **Utilização**: 
	-	A) Executando os comandos (através do diretório MPEI): 
		-	$javac src/*.java
		-	$java src.actualFinalTest
	-	B) Abrindo o diretório 'Old Eclipse Files' no Eclipse IDE e executando através daí ; 
- **Descrição**: Este projeto baseia-se na utilização dos vários conceitos aprendidos nas aulas de MPEI e aplicação destes a um problema real. Foram utilizados neste projeto conceitos como procura de similaridades com uso de MinHash, distáncias de Jacqard, contadores estocásticos, counting bloom filters, entre outros. A ideia base deste projeto consiste na procura de e identificação de possíveis "Spambots" através da analise de um conjunto de críticas de jogos postadas por utilizadores no site [GoodOlGames.com](https://www.gog.com/) através da analise e procura de reviews similares.


### [CD PROJETO] - PEDRO
- **Cadeira**: Computação Distribuida
- **Autores**: Pedro Oliveira, ???
- **Linguagem**: Java (8)
- **Utilização**: 
	-	A) Executando os comandos (através do diretório MPEI): 
		-	$javac src/*.java
		-	$java src.actualFinalTest
	-	B) Abrindo o diretório 'Old Eclipse Files' no Eclipse IDE e executando através daí ; 
- **Descrição**: Este projeto baseia-se na utilização dos vários conceitos aprendidos nas aulas de MPEI e aplicação destes a um problema real. Foram utilizados neste projeto conceitos como procura de similaridades com uso de MinHash, distáncias de Jacqard, contadores estocásticos, counting bloom filters, entre outros. A ideia base deste projeto consiste na procura de e identificação de possíveis "Spambots" através da analise de um conjunto de críticas de jogos postadas por utilizadores no site [GoodOlGames.com](https://www.gog.com/) através da analise e procura de reviews similares.


### Flappy Bird Machine Learning
- **Cadeira**: -
- **Autores**: Diogo Silva, Pedro Escaleira
- **Linguagem**: Python
- **Utilização**: 
	-	A) Executando os comandos (através do diretório flappy_bird_ml): 
		-	$python3 main.py
- **Descrição**: Este projeto foi realizado para diversão, não estando portanto associado a nenhuma UC. Como introdução, tanto a desenvolvimento de jogos utilizando o PyGame, como à criação de um algorítmo genético de machine learning, este projeto baseiou-se na execução, tanto de um "Flappy Bird clone", como num algorítmo que gerasse vários agentes inteligentes, que fossem evoluindo de geração em geração (sendo que cada geração termina assim que todos os membros desta morrerem).

# Repositório GitHub
Todos os ficheiros, tanto desta como de todas as próximas práticas, poderá ser encontrado no repositório:

https://github.com/HerouFenix/CSLP

 Para esta prática em especifico, basta navegar para o diretório Prática1.
 
 Posteriormente, todos os ficheiros relativos a um projeto (código, Doxyfile, Dockerfile, docs, etc.) encontra-se no subdiretório com o nome do projeto.

# Docs
Para encontrar a documentação, gerada pelo Doxygen, relativa a cada um dos projetos basta navegar para o diretório:
**Pratica1 > _Nome do projeto_  > docs**

Os Doxyfiles utilizados para a geração das documentações pode ser encontrada na pasta raiz de cada projeto (Pratica1 > _Nome do projeto_)

# Dockerhub

### SpamBot Finder 
- **Dockerhub**: https://cloud.docker.com/repository/registry-1.docker.io/fenixds/spambot-finder
-  **Utilização**: 
	- $sudo docker build --tag=spambotfinder .
	- $sudo docker run -i spambotfinder
-  **Dockerfile**: 
``` 
FROM openjdk:8
COPY . /app
WORKDIR /app
RUN javac src/*.java
CMD ["java", "src/actualFinalTest"]
```
- **Notas**: Na altura, este projeto foi criado em Eclipse-IDE. O problema com isto é a que a gestão das packages feita automáticamente pelo Eclipse não se aplica aquando da compilação do source code por terminal (i.e, quando executavamos o comando $javac algumas classes de Teste que dependiam de classes Modules não estavam a conseguir ser compiladas por não conseguirem encontrar estas ultimas). Como forma de evitar dores de cabeças desnecessárias, alteramos a estrutura do projeto, metendo todos os ficheiros .java num subdiretório da raiz do projeto - src, e todos os ficheiros de suporte (como os ficheiros de texto com a informação analisada), num outro subdiretório da raiz do projeto - Files.

### CD
- **Dockerhub**: https://cloud.docker.com/repository/registry-1.docker.io/fenixds/spambot-finder
-  **Utilização**: 
	- $sudo docker build --tag=spambotfinder .
	- $sudo docker run -i spambotfinder
-  **Dockerfile**: 
``` 
FROM openjdk:8
COPY . /app
WORKDIR /app
RUN javac src/*.java
CMD ["java", "src/actualFinalTest"]
```
- **Notas**:

### Flappy Bird Machine Learning
- **Dockerhub**: https://cloud.docker.com/repository/registry-1.docker.io/fenixds/spambot-finder
-  **Utilização**: 
	- $sudo docker build --tag=spambotfinder .
	- $sudo docker run -i spambotfinder
-  **Dockerfile**: 
``` 
FROM openjdk:8
COPY . /app
WORKDIR /app
RUN javac src/*.java
CMD ["java", "src/actualFinalTest"]
```
- **Notas**: Bela poop

# Git Activity
[PEDRO]
