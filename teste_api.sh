#!/bin/bash
BASE_URL="http://localhost:8080"
IDEMPOTENCY_KEY=$(date +%s)

echo "--- 1. LOGIN ---"
TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" -H "Content-Type: application/json" -d '{"username": "balanca_admin", "password": "serasa123"}' | grep -o '"token":"[^"]*' | grep -o '[^"]*$')
echo "Token obtido!"

api_post() {
  echo -e "\nEnviando para $1..."
  curl -X POST "$BASE_URL$1" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "$2"
  sleep 1
}

echo -e "\n--- 2. CADASTROS (SEM ACENTOS) ---"
# Filial
api_post "/api/v1/unidades/filiais" '{"nome": "Filial Maranhao", "cidade": "Sao Luis"}'

# Grao
api_post "/api/v1/produtos/graos" '{"nome": "Soja", "precoPorKg": 2.50}'

# Balanca
api_post "/api/v1/unidades/balancas" '{"id": "BAL-01", "modelo": "T2000", "localizacao": "Portao A"}'

# Caminhao (Importante: Verifique se o ID da filial/grao sao 1 ou use os novos)
api_post "/api/v1/caminhoes" '{"placa": "ABC1234", "tara": 15000.0, "filialId": 1, "graoId": 1}'

echo -e "\n\n--- 3. PESAGENS (ESTABILIZACAO) ---"
for i in {1..5}; do
   # balancaId bate com seu Request
   curl -s -X POST "$BASE_URL/api/v1/pesagens/pesagem" \
     -H "Authorization: Bearer $TOKEN" \
     -H "Idempotency-Key: key_${IDEMPOTENCY_KEY}_$i" \
     -H "Content-Type: application/json" \
     -d '{"plate": "ABC1234", "weight": 50000.0, "balancaId": "BAL-01"}'
   echo "Leitura $i enviada..."
   sleep 0.2
done

echo -e "\n--- 4. RELATORIO ---"
curl -X GET "$BASE_URL/api/v1/relatorios?placa=ABC1234" -H "Authorization: Bearer $TOKEN"