#!/bin/bash +x

# efetua cadastro de aluno duas vezes, onde quando com nome repetido, deve receber exceção informando do problema

curl -s -w '\n' localhost:8080/students -H "Content-Type: application/json" -H "Accept: application/json" --data '{"name":"LEONARDO BELMONTE BOGON"}' 
curl -s -w '\n' localhost:8080/students -H "Content-Type: application/json" -H "Accept: application/json" --data '{"name":"LEONARDO BELMONTE BOGON"}' 
