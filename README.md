<div align="center">

# Do Sensor à Nuvem
### Telemetria Industrial em Tempo Real

![Status](https://img.shields.io/badge/status-operational-000000?style=flat-square)
![License](https://img.shields.io/badge/license-MIT-000000?style=flat-square)
![Java](https://img.shields.io/badge/Java-17-000000?style=flat-square&logo=openjdk&logoColor=white)
![C++](https://img.shields.io/badge/C%2B%2B-Arduino-000000?style=flat-square&logo=cplusplus&logoColor=white)
![MQTT](https://img.shields.io/badge/MQTT-TLS%208883-000000?style=flat-square&logo=eclipsemosquitto&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-000000?style=flat-square&logo=postgresql&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-Cloud-000000?style=flat-square&logo=grafana&logoColor=white)

**SENAI CentroWEG — Jaraguá do Sul, SC**  
Ryhan Gabriel Schutz · Técnico em CiberSistemas para Automação · 2026

</div>

---

## Arquitetura

```mermaid
flowchart LR
    A[ESP32\nWokwi] -->|MQTT / TLS 8883| B[HiveMQ Cloud]
    B -->|subscribe| C[Java Backend\nCodespaces]
    C -->|JDBC / SSL| D[(Neon\nPostgreSQL)]
    D -->|SQL query| E[Grafana Cloud\nDashboard]

    style A fill:#000,color:#fff,stroke:#333
    style B fill:#000,color:#fff,stroke:#333
    style C fill:#000,color:#fff,stroke:#333
    style D fill:#000,color:#fff,stroke:#333
    style E fill:#000,color:#fff,stroke:#333
```

---

## Stack

```mermaid
graph TD
    FW[Firmware C++\nArduino Framework]
    SEC[WiFiClientSecure\nTLS sem CA]
    PROTO[MQTT v3.1.1\nPubSubClient]
    BACK[Java 17\nMaven + Paho]
    DB[PostgreSQL\nNeon Serverless]
    VIZ[Grafana Cloud\nTime Series]

    FW --> SEC --> PROTO --> BACK --> DB --> VIZ

    style FW fill:#000,color:#fff,stroke:#444
    style SEC fill:#000,color:#fff,stroke:#444
    style PROTO fill:#000,color:#fff,stroke:#444
    style BACK fill:#000,color:#fff,stroke:#444
    style DB fill:#000,color:#fff,stroke:#444
    style VIZ fill:#000,color:#fff,stroke:#444
```

---

## Pinagem ESP32

```mermaid
block-beta
  columns 5
  A["DHT22\nGPIO 15\nTemperatura"]:1
  B["Pot. 1\nGPIO 34\nVibração"]:1
  C["Pot. 2\nGPIO 35\nCorrente"]:1
  D["LCD I2C\nSDA 21 / SCL 22\nDisplay"]:1
  E["LED Verde\nGPIO 12\nStatus"]:1
```

---

## Payload

O ESP32 publica no tópico `senai/ryhan/motor/dados` no formato:

```
"27.5,42,18"
 └─┬─┘ └┬┘ └┬┘
   │    │   └── Corrente (A)
   │    └─────── Vibração (mm/s)
   └──────────── Temperatura (°C)
```

---

## Banco de dados

```sql
CREATE TABLE leituras (
    id          SERIAL PRIMARY KEY,
    timestamp   TIMESTAMPTZ DEFAULT NOW(),
    temperatura NUMERIC(5,2),
    vibracao    INTEGER,
    corrente    INTEGER
);
```

---

## Execução

**Firmware**

Importe no Wokwi, monte o circuito e inicie a simulação.

**Backend Java**

```bash
mvn compile
mvn exec:java -Dexec.mainClass="br.com.senai.automacao.App"
```

**Grafana**

```sql
SELECT
  timestamp   AS "time",
  temperatura AS "Temperatura (°C)",
  vibracao    AS "Vibração (mm/s)",
  corrente    AS "Corrente (A)"
FROM leituras
WHERE timestamp >= $__timeFrom() AND timestamp <= $__timeTo()
ORDER BY timestamp ASC;
```

---

## Dependências

```xml
<dependency>
    <groupId>org.eclipse.paho</groupId>
    <artifactId>org.eclipse.paho.client.mqttv3</artifactId>
    <version>1.2.5</version>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
</dependency>
```

---

## Segurança

Canal TLS na porta 8883 com autenticação por credenciais tanto no ESP32 (`WiFiClientSecure`) quanto no Java (`SSLSocketFactory`). Conexão ao Neon com `sslmode=require`.

Em produção: Mosquitto local com certificados X.509 e autenticação mútua.

---

<div align="center">

![ESP32](https://img.shields.io/badge/ESP32-Espressif-000000?style=for-the-badge&logo=espressif&logoColor=white)
![HiveMQ](https://img.shields.io/badge/HiveMQ-Cloud-000000?style=for-the-badge&logoColor=white)
![Neon](https://img.shields.io/badge/Neon-PostgreSQL-000000?style=for-the-badge&logo=postgresql&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-Cloud-000000?style=for-the-badge&logo=grafana&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-Codespaces-000000?style=for-the-badge&logo=github&logoColor=white)
![WEG](https://img.shields.io/badge/WEG-Automação-000000?style=for-the-badge&logoColor=white)

*Código estruturado com auxílio da IA Claude (Anthropic).*  
*Arquitetura, integração e lógica definidas pelo autor.*

> `Ler → Compreender → Fazer.`

</div>
