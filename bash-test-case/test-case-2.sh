#!/bin/bash +x

# apresentação de caso de uso de aluno que não teve limite associado, logo não é possível efetuar compra

studentid=$( curl -s -w '\n' localhost:8080/students -H "Content-Type: application/json" -H "Accept: application/json" --data '{"name":"HINGRYD SILVA COSTA"}' | jq .id | tr -d '"' )
formattedStudentId=${studentid/ /%20}

echo $formattedStudentId

echo -e "\n tentativa de adição débito de 43.12, que deve ter erro pois ainda não foi criado limite"
curl -s -w '\n' localhost:8080/students/$formattedStudentId/card/debit -H "Content-Type: application/json" -H "Accept: application/json" --data '{"value":43.12}'
