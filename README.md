# 🚛 Balança Digital API - Serasa Tech Challenge

API REST desenvolvida em **Java 11 + Spring Boot 2.6** para gerenciamento de pesagens automatizadas, estabilização de telemetria e análise de lucratividade logística.

---

## 🚀 Tecnologias Utilizadas

* **Java 17**
* **Spring Boot 2.6.x**
* **Spring Data JPA**
* **H2 Database** (In-memory)
* **Spring Security** (Custom Stateless Auth)
* **JUnit 4 & Mockito**
* **Maven**
* **Lombok**

---

## 🧱 Arquitetura

A aplicação segue os princípios de separação de responsabilidades, garantindo a manutenibilidade do código:

controller → service → repository → domain
↓
mapper
↓
requests/responses

* **Controller:** Camada de entrada HTTP e segurança.
* **Service:** Regras de negócio (Estabilização e Cálculos).
* **Repository:** Persistência de dados.
* **Mapper:** Conversão eficiente entre Entidades e DTOs.

---

## 📌 Funcionalidades

* **Autenticação Stateless:** Controle de acesso via Bearer Token.
* **Algoritmo de Estabilização:** Processa telemetria bruta e valida pesos estáveis (variação < 0.5kg) em séries de 5 leituras.
* **Idempotência:** Header `Idempotency-Key` para evitar duplicidade no registro de pesagens.
* **Cálculo de Rentabilidade:** Cálculo automático de Peso Líquido, Custo de Carga e Margem de Lucro (15%).
* **Relatórios Dinâmicos:** Filtros por Placa, Filial, Tipo de Grão e Período Temporal.

---

## 🔐 Segurança

O sistema utiliza um `tokenStore` em memória para validação rápida de sessões.

### Login

`POST /auth/login`

**Body:**
```json
{
  "username": "balanca_admin",
  "password": "serasa123"
}
```
## 🧪 Testes

Testes unitários implementados com:

* JUnit 4
* Mockito
* Spring WebMvcTest

## 🧪 Testes de Integração
Para validar o fluxo completo da API (da pesagem ao relatório), você pode executar o script automatizado:
```bash
./teste_api.sh
```

## 👨‍💻 Autor

Christopher Castro
