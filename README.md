# Supervisão de Máquinas Elétricas via Telemetria MQTT

**Curso Técnico em CiberSistemas para Automação — SENAI CentroWEG**  
Unidade Curricular: Programação para Coleta de Dados em Automação  
Autor: Ryhan Gabriel Schutz · Turma: T TCPA 2025/1 INT1

---

## Sobre o projeto

Protótipo de telemetria industrial utilizando ESP32 para coleta de dados de **temperatura**, **vibração** e **corrente** de um motor elétrico simulado, com transmissão via **MQTT sobre canal seguro TLS** e recepção em backend Java.

---

## Arquitetura

```
DHT22 + Potenciômetros → ESP32 → MQTT/TLS (8883) → HiveMQ Cloud → Java Backend
```

| Camada | Tecnologia |
|---|---|
| Firmware | C++ / Arduino Framework (ESP32) |
| Protocolo | MQTT v3.1.1 — TLS porta 8883 |
| Broker | HiveMQ Cloud (Free Tier) |
| Backend | Java 17 + Maven + Eclipse Paho |
| Simulação | Wokwi |

---

## Hardware (Pinagem ESP32)

| Componente | GPIO | Função |
|---|---|---|
| DHT22 | 15 | Temperatura (°C) |
| Potenciômetro 1 | 34 | Vibração simulada (mm/s) |
| Potenciômetro 2 | 35 | Corrente simulada (A) |
| LCD 16x2 I2C | SDA→21 / SCL→22 | Display local |
| LED Verde | 12 | Indicador de envio ativo |

---

## Estrutura do repositório

```
├── firmware/
│   └── firmware_motor_ryhan.ino   # Código C++ ESP32
├── src/
│   └── main/java/br/com/senai/automacao/
│       └── App.java               # Backend Java
├── pom.xml                        # Dependências Maven
└── README.md
```

---

## Como executar

### Firmware (Wokwi)
1. Acesse [wokwi.com](https://wokwi.com) e importe o projeto
2. Monte o circuito conforme a pinagem acima
3. Execute a simulação — o ESP32 conecta ao HiveMQ Cloud via TLS e publica a cada 2 segundos

### Backend Java (GitHub Codespaces ou local)
```bash
mvn compile
mvn exec:java -Dexec.mainClass="br.com.senai.automacao.App"
```

Saída esperada no console:
```
Conectando ao Broker MQTT...
Conectado!
Aguardando dados no tópico: senai/ryhan/motor/dados
Dados de Telemetria Coletados com Sucesso: 27.3,45,18
```

---

## Segurança

O projeto utiliza **TLS na porta 8883** com autenticação por credenciais, em substituição ao broker público sem criptografia. A conexão segura é estabelecida tanto no firmware (`WiFiClientSecure`) quanto no backend (`SSLSocketFactory`).

Em ambiente industrial real, o passo seguinte seria a implantação do **Mosquitto local** com certificados X.509 e autenticação mútua.

---

## Dependências

**C++ (Wokwi / Arduino Library Manager)**
```
DHT sensor library
PubSubClient
LiquidCrystal I2C
```

**Java (pom.xml)**
```xml
<dependency>
    <groupId>org.eclipse.paho</groupId>
    <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
    <version>1.2.5</version>
</dependency>
```

---

## Referências

- LAMB, Frank. *Automação Industrial na Prática*. McGraw-Hill / Bookman, 2015.
- CHAPMAN, Stephen J. *Fundamentos de Máquinas Elétricas*. McGraw-Hill / Bookman, 2013.
- [Eclipse Paho Java Client](https://www.eclipse.org/paho)
- [HiveMQ Cloud](https://www.hivemq.com)
- [Wokwi Simulator](https://wokwi.com)

---

*Desenvolvido com auxílio da IA Claude (Anthropic) para estruturação e revisão do código.*
