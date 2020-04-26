# intro

O projeto corrente atende o trabalho de gerenciamento de transações de um cartão de crédito fictício, desenvolvido dentro da faculdade.
O trabalho atende a matéria de Spring lecionada no curso de MBA, que tem o objetivo de apresentar possibilidades com o framework Spring Boot.

###### Escola: FIAP
###### Curso: MBA FULLSTACK DEVELOPER, MICROSERVICES, CLOUD & IoT  
###### Matéria: SPRING
###### Prof. FABIO TADASHI MIYASATO

O projeto é versionado no github através do repositório [github.com/jeanvillete/fiap-spring](https://github.com/jeanvillete/fiap-spring)  
As atividades levantadas para serem desenvolvidas de acordo com cada ***caso de uso*** estão geridas no [projeto github jeanvillete/fiap-spring](https://github.com/jeanvillete/fiap-spring/projects/1)

---

# instrução de execução

### a partir do binário; download e execução do jar empacotado
***TBD***

### a partir do código fonte; clone e build/empacotamento do projeto
***TBD***

---

# requisitos trabalho

RF1 - Cadastro de Alunos  
RF2 - O cadastro inicial dos potenciais clientes do cartão será realizado via integração com um arquivo .txt disponibilizado https://drive.google.com/open?id=19ILqrYjOEe4C840ZRwhKDauvhDZCKcWa  
RF3 - As compras realizadas nos cartões dos clientes serão recebidas via integração com uma Autorizadora. Criar os endpoints necessários para receber as realizações de transações.  
RF4 - Deve ser possível gerar um extrato via download (endpoint) ou enviado no email do cliente (pode escolher uma opção).  
  
RNF1 - Utilizar o Spring Framework.  
RNF2 - Criar um arquivo readme.md com as instruções para subir o/os projeto/s.  
RNF3 - Criar testes unitários e integrados para o/os projeto/s.  
RNF4 - Gerar uma massa simulada de transações.  
RNF5 - Documentação Swagger  

---

# domínio e premissas

#### aluno (STUDENT)
 - composto das informações nome, número de inscrição e código, onde as duas informações número da inscrição e código compõem a identificação deste registro.

#### limite do cartão (LIMIT_CARD)
 - composto das informações data e hora do registro e o valor, e ainda uma chave estrangeira para o domínio aluno.  
 - o limite do cartão é uma informação que não pode ser apagada e nem alterada. caso um novo limite seja necessário, então um novo registro deve ser inserido.
 - o limite corrente do cartão é o último registro inserido, ou seja, o registro mais recente.  
 - domínio reponsável por responder perguntas como;
   - qual o limite corrente de determinado aluno?
   - quando o novo limite foi associado?

#### transações do cartão (TRANSACTION_CARD)
 - composto das informações data e hora da transação, o valor da transação, uma descrição que é opcional, e há também uma chave estrangeira para o domínio limite do cartão.
 - transações com valores positivos representam uma operação de débito, ou seja, uma compra.
 - transações com valores negativos representam uma operação de crédito, que pode ser por exemplo o pagamento de uma fatura ou o estorno de um débito.
 - o saldo corrente do cartão é calculado somando o valor de todas as operações já realizadas.
 - uma transação de débito só pode ser efetivada caso o limite corrente seja maior que o saldo corrente.
 - na inserção de uma transação, uma vez com sucesso, deve-se retornar um UUID para a mesma.

---

# casos de uso e seus endpoints

abaixo segue a lista de casos de uso e exemplos de requisições e respostas;  

##### criação de aluno; [issue #1](https://github.com/jeanvillete/fiap-spring/issues/1)
 - o caso de uso para criação de um aluno recebe no payload basicamente o nome do aluno.
 - aplicar trim no nome recebido e após esta aplicação, efetuar validação abaixo
 - o nome deve ser válido, contendo uma ocorrência de no mínimo três caracteres seguido por espaço e outros três caracteres.
 - caso já exista um aluno com mesmo nome, exceção de duplicidade deve ser lançada e com mensagem de erro explicando do problema e status code ***409 Conflict***
 - caso o nome seja válido, então gerar uma identificação para o aluno, composto do número da subscrição (7 dígitos) e o código (5 dígitos), onde este valor deve ser retornado; ***201 Created***
   - o valor para subscrição deve ser incremental, ou seja, procurar o maior corrente e incrementar.
   - o valor para o codigo deve ser um valor randomico entre 10000 e 99999, e não tem problema de conflitos.
   - o valor de identificação deve ser retornado formatado; ***####### ###-##***

```
[request]
POST /students
{
    "name": "SAMPLE USER NAME"
}

[response]
201 Created
{
    "id": "9999999 999-99",
    "name": "SAMPLE USER NAME"
}
```

##### atualização do nome do aluno; [issue #2](https://github.com/jeanvillete/fiap-spring/issues/2)
 - o caso de uso para atualização do nome do aluno recebe no payload o nome do aluno.
   - a identificação do aluno deve ser fornecida como ***path variable***
 - caso já exista um outro aluno com mesmo nome, exceção da duplicação é devolvida; ***409 Conflict***
 - caso o nome seja válido, então armazenar novo nome; ***200 Ok***
 - caso a identificação seja inválida ou não existir um registro para a mesma, devolver ***404 Not Found***

```
[request]
PUT /students/9999999 999-99
{
    "name": "UPDATED SAMPLE USER NAME"
}

[response]
200 Ok
```

##### busca por aluno baseado no nome; [issue #3](https://github.com/jeanvillete/fiap-spring/issues/3)
 - o caso de uso para busca/listagem de aluno, serve basicamente para possibilitar encontrar a identificação do aluno, mas também permite uma busca por parte do nome.
   - o nome de exemplo para busca deve ser fornecido  via ***query string***
   - é obrigatório o fornecimento da ***query string*** com nome de exemplo, e deve conter pelo ao menos 2 caracteres.
 
```
[request]
GET /students?name=SAMPLE

[response]
200 Ok
[
    {
        "id": "9999999 999-99",
        "name": "UPDATED SAMPLE USER NAME"
    }
]
```

##### cria novo limite para aluno; [issue #4](https://github.com/jeanvillete/fiap-spring/issues/4)
 - o caso de uso para criação de novo limite para determinado aluno recebe basicamente no payload a informação do valor.
   - a identificação do aluno deve ser fornecida como ***path variable***
   - o valor deve ser um inteiro maior ou igual a zero (0)
 - caso a identificação seja inválida ou não existir um registro para a mesma, devolver ***404 Not Found***
 
```
[request]
POST /students/9999999 999-99/card/limit
{
    "value": 100.00
}

[response]
201 Created
```

##### consulta limite corrente do aluno; [issue #5](https://github.com/jeanvillete/fiap-spring/issues/5)
 - o caso de uso para consulta do limite corrente para determinado aluno recebe apenas a identificação do aluno.
   - a identificação do aluno deve ser fornecida como ***path variable***
 - caso a identificação seja inválida ou não existir um registro para a mesma, devolver ***404 Not Found***
 
```
[request]
GET /students/9999999 999-99/card/limit

[response]
200 Ok
{
    "value": 100.00
}
```

##### lança transação de débito para aluno; [issue #6](https://github.com/jeanvillete/fiap-spring/issues/6)
 - uma transação de débito significa uma compra
 - valida se ***existe um limite associado para o aluno corrente***, caso requisito não seja atendido, devolver resposta explicando o problema; ***428 Precondition Required***
 - valida se o ***saldo corrente do aluno somado a transação fornecida é menor que o limite corrente***, caso requisito não seja atendido, devolver resposta explicando o problema; ***428 Precondition Required***
 - no caso de sucesso devolver o UUID que identifica a criação da transação
 - o corpo da requisição deve conter o valor da compra e uma descrição (opcional)
 - caso a identificação seja inválida ou não existir um registro para a mesma, devolver ***404 Not Found***
 
```
[request]
POST /students/9999999 999-99/card/debit
{
    "value": 100.00,
    "description": "compra no mercado Sr Luiz"
}

[response]
201 Created
{
    "uuid": "95963271-48a2-4dbd-abaa-93256de381d4"
}
```

##### lança transação de crédito para aluno (estorno de compra); [issue #7](https://github.com/jeanvillete/fiap-spring/issues/7)
 - uma transação de crédito significa por exemplo o pagamento de uma fatura ou o estorno de uma compra, no caso corrente um estorno
 - no caso do estorno, a identificação do pagamento deve estar presente via ***path variable***
 - deve ser verificado se a transação correspondente existe, caso não seja encontrado, devolver resposta informando do problema ***404 Not Found***
 - não precisa ter corpo na requisição
 - no caso de sucesso do estorno, devolver o UUID que identifica a transação em equestão, no caso, o lançamento do estorno
 - caso a identificação seja inválida ou não existir um registro para a mesma, devolver ***404 Not Found***

```
[request]
POST /students/9999999 999-99/card/chargeback/95963271-48a2-4dbd-abaa-93256de381d4

[response]
201 Created
{
    "uuid": "7930f6a6-4589-47dd-a3d2-319b9c5346f8"
}
```

##### lança transação de crédito para aluno (pagamento fatura); [issue #8](https://github.com/jeanvillete/fiap-spring/issues/8)
 - uma transação de crédito significa por exemplo o pagamento de uma fatura ou o estorno de uma compra, no caso corrente o pagamento de uma fatura
 - no caso de sucesso do estorno, devolver o UUID que identifica a transação do pagamento da fatura em equestão
 - caso a identificação seja inválida ou não existir um registro para a mesma, devolver ***404 Not Found***

```
[request]
POST /students/9999999 999-99/card/bill-payment
{
    "value": 58.63
}

[response]
201 Created
{
    "uuid": "7364259a-94aa-4c37-ad39-5e9837c0fd3e"
}
```

##### calcula extrato para um determinado mês; [issue #9](https://github.com/jeanvillete/fiap-spring/issues/9)
 - para o calculo do extrato de um mês específico, deve-se obter todas as transações até este mês em questão, e calcular o saldo deste mês
 - o mês e ano que se procura o extrato deve ser informado ambos via ***path variable***
 - caso a identificação seja inválida ou não existir um registro para a mesma, devolver ***404 Not Found***
 - o header Accept deve ser fornecido pare decisão do retorno como json estruturado ou texto formatado.

```
[request]
GET /students/9999999 999-99/card/statement/2019-01
Accept: application/json

[response]
200 Ok
{
    "statement-month": "2019-01"
    "current-limit-value": 100.00,
    "month-balance": 58.63,
    "transactions": [
        {
            "datetime": "2019-01-02T12:10:15",
            "uuid": "95963271-48a2-4dbd-abaa-93256de381d4",
            "value": 100.00,
            "description": "compra no mercado Sr Luiz"
        },
        {
            "datetime": "2019-01-03T11:05:01",
            "uuid": "7930f6a6-4589-47dd-a3d2-319b9c5346f8",
            "value": 100.00,
            "description": "ESTORNO DE COMPRA; compra no mercado Sr Luiz"
        },
        {
            "datetime": "2019-01-29T05:18:56",
            "uuid": "7364259a-94aa-4c37-ad39-5e9837c0fd3e",
            "value": 58.63,
            "description": "Pagamento de fatura 2019-01; $ 58.63"
        }
    ]
}
```

```
[request]
GET /students/9999999 999-99/card/statement/2019-01
Accept: text/plain

[response]
201 Ok

Janeiro 2019

02/01/2019 12:10:15 95963271-48a2-4dbd-abaa-93256de381d4
value           100.00
description     compra no mercado Sr Luiz

03/01/2019 11:05:01 7930f6a6-4589-47dd-a3d2-319b9c5346f8
value           100.00
description     ESTORNO DE COMPRA; compra no mercado Sr Luiz

29/01/2019 05:18:56 7364259a-94aa-4c37-ad39-5e9837c0fd3e
value           58.63
description     Pagamento de fatura 2019-01; $ 58.63

current limit   100.00

month balance   58.63
```
