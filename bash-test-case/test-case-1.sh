#!/bin/bash +x

# cria aluno
# efetua associação de limite
# efetua débitos/compras
# efetua estorno
# efetua pagamento fatura
# consulta extrato; texto
# consulta extrato; json

studentid=$( curl -s -w '\n' localhost:8080/students -H "Content-Type: application/json" -H "Accept: application/json" --data '{"name":"ADALBERTO ANTONIORI SILVA"}' | jq .id | tr -d '"' )
formattedStudentId=${studentid/ /%20}

echo $formattedStudentId

echo -e "\n setar limite em 500.00"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/limit -H "Content-Type: application/json" -H "Accept: application/json" --data '{"value":500}'

echo -e "\n adição débito de 43.12"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/debit -H "Content-Type: application/json" -H "Accept: application/json" --data '{"value":43.12}'

echo -e "\n adição de débito de 138.66"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/debit -H "Content-Type: application/json" -H "Accept: application/json" --data '{"value":138.66}'

echo -e "\n adição de débito de 85.22, que será estornado depois"
transactionToBeChargedBack=$( curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/debit -H "Content-Type: application/json" -H "Accept: application/json" --data '{"value":85.22}' | jq .uuid | tr -d '"' )

echo $transactionToBeChargedBack

echo -e "\n adição de débito de 230.50"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/debit -H "Content-Type: application/json" -H "Accept: application/json" --data '{"value":230.50}'

echo -e "\n aplicação de estorno"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/charge-back/$transactionToBeChargedBack -H "Content-Type: application/json" -H "Accept: application/json" -X POST

echo -e "\n adição de débito de 100.00 que deve falhar, pois o limite não permite"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/debit -H "Content-Type: application/json" -H "Accept: application/json" --data '{"value":100.00}'

echo -e "\n adição de débito de 70.00"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/debit -H "Content-Type: application/json" -H "Accept: application/json" --data '{"value":70.00}'

echo -e "\n aplica pagamento de 350.00"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/bill-payment -H "Content-Type: application/json" -H "Accept: application/json" --data '{"value":350.00}'

echo -e "\n o mesmo pagamento de 100.00 que deve funcionar"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/debit -H "Content-Type: application/json" -H "Accept: application/json" --data '{"value":100.00}'

echo -e "\n\n-- extrato em texto --"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/statement/2020-04 -H "Content-Type: application/json" -H "Accept: text/plain" 

echo -e "\n\n-- extrato em formato json --"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/statement/2020-04 -H "Content-Type: application/json" -H "Accept: application/json"  | jq
