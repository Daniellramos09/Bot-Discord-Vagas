# Contexto do Projeto

Atue como um Engenheiro de Software Sênior. Nós temos um projeto Spring Boot (um bot para Discord) estruturado em Modular Monolith e que segue estritamente os princípios SOLID. O projeto já possui domínios como "Vaga", "Curso" e "Hackathon".

A regra de arquitetura atual para cada domínio é ter as seguintes classes isoladas:

1. `Entidade/Record` (Ex: Hackathon.java)
2. `Repository` (Ex: HackathonRepository.java)
3. `PromptProvider` (Implementa uma interface comum para gerar prompts para a IA)
4. `Orchestrator` (O Maestro que orquestra a busca, a IA e o envio pro Discord)
5. `Scheduler` (O CronJob)

**Regra de Banco de Dados:** Siga a mesma lógica dos domínios já existentes. Não crie atributos complexos novos. Mantenha os mesmos campos básicos (título, link, descrição da IA, etc).

# Nova Tarefa: Implementar 2 Novos Domínios

Quero expandir o meu projeto construindo mais 2 bots (módulos). Implemente as classes seguindo exatamente o padrão arquitetural acima.

## 🎯 Bot 1: Domínio "Certificações"

**Objetivo:** Auxiliar alunos a tirarem certificações valiosas no mercado, enviando no Discord: do que se trata, para quem é importante, a documentação oficial e um repositório no GitHub focado nos estudos para a prova.

**Fonte de Dados (Curadoria):** Use a base de dados abaixo para popular o sistema (pode ser via JSON inicial ou carga no banco):

1. **Amazon Web Services (AWS)**
   * `Brain2life/aws-exams-preparation-guide`: Um dos guias mais completos. Dicas de estudo, estratégias e indicações de laboratórios.
   * `Kash1405/aws-certifications`: Lista curada com materiais, cursos em vídeo e simulados.
   * `keenanromain/AWS-SAA-C02-Study-Guide`: Focado em Arquiteto de Soluções (SAA), com notas de IAM, S3, EC2, etc.

2. **Microsoft Azure**
   * `AzureMentor/Azure-AZ-900-Study-Guide`: Focado no exame fundamental (AZ-900).
   * `ricmmartins/guia-estudo-az900`: Guia de estudos em português.
   * `shiftavenue/awesome-azure-learning`: Roteiros do MS Learn e laboratórios.

3. **Oracle Cloud Infrastructure (OCI)**
   * `debabrata2050/Oracle-Certificate`: Materiais atualizados e simulados para OCI AI Foundations e Cloud Infrastructure Foundations.
   * `heltonricardo/estudo-certificacoes`: Repositório brasileiro de estudos gerais de nuvem.

4. **GitHub Foundations**
   * Buscar pela tag `github-certification-foundations`. Compartilha fluxos de PRs, Actions e repositórios.
   * Dica: O repo `GitHub Education / Foundations Certificate` oferece caminhos interativos e vouchers para estudantes.

5. **Certificações Linux (LPI / CompTIA Linux+ / LFCS)**
   * `caioross/Cursos-Certificados-Free`: Lista brasileira que mapeia cursos oficiais da The Linux Foundation.

## 🎯 Bot 2: Domínio "Open Source e Produtividade"

**Objetivo:** Fazer um mapeamento no GitHub para encontrar repositórios muito interessantes que possam auxiliar estudantes e desenvolvedores em produtividade, estudo e gestão. O bot deve analisar a ferramenta e enviar ao Discord com uma explicação da IA sobre como ela ajuda.

**Fonte de Dados (Curadoria Inicial):** Use os seguintes links como base de dados inicial para o bot buscar e analisar:

* https://github.com/yt-dlp/yt-dlp (Ferramenta CLI para vídeos)
* https://github.com/ollama/ollama (IA LLM Local)
* https://github.com/lllyasviel/Fooocus (Geração de imagens offline gratuita com SDXL)
* https://github.com/openai/whisper (Transcrição de áudio via IA)

**Nota para a IA:** O intuito é criar uma rotina que processe essas URLs, passe para o módulo do Gemini resumir do que se trata o software e poste no canal.

# Instruções de Entrega

Gere o código Java para estes dois novos domínios (`domain/certificacao` e `domain/opensource`). Mantenha o código limpo, siga o SRP (Single Responsibility Principle) rigorosamente nas classes `Orchestrator` e `PromptProvider`.
