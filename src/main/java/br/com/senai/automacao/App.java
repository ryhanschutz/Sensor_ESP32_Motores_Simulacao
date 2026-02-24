package br.com.senai.automacao;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.sql.*;

public class App {

    // ---- MQTT ----
    static final String BROKER    = "ssl://8499505b5a944d7fb9741e0ab74b8610.s1.eu.hivemq.cloud:8883";
    static final String CLIENT_ID = "JavaBackend_Ryhan_Motor";
    static final String TOPICO    = "senai/ryhan/motor/dados";
    static final String USUARIO   = "ryhan";
    static final String SENHA     = "Servidor123";

    // ---- POSTGRESQL ----
    static final String DB_URL  = "jdbc:postgresql://ep-delicate-cell-ac9rn1t6-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require";
    static final String DB_USER = "neondb_owner";
    static final String DB_PASS = "npg_wNG3mtapb1QA";

    public static void main(String[] args) throws Exception {

        // Testa conexão com o banco antes de tudo
        System.out.println("Conectando ao PostgreSQL...");
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        System.out.println("PostgreSQL OK!");

        // Prepara o INSERT uma vez só, reutiliza a cada leitura
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO leituras (temperatura, vibracao, corrente) VALUES (?, ?, ?)"
        );

        // ---- MQTT ----
        MqttClient client = new MqttClient(BROKER, CLIENT_ID, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USUARIO);
        options.setPassword(SENHA.toCharArray());
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setSocketFactory(criarSSLInseguro());

        System.out.println("Conectando ao Broker MQTT...");
        client.connect(options);
        System.out.println("MQTT OK!");

        client.subscribe(TOPICO, (topic, msg) -> {
            String payload = new String(msg.getPayload());
            System.out.println("Dados de Telemetria Coletados com Sucesso: " + payload);

            // Parse do payload "temp,vibra,corrente"
            String[] v = payload.split(",");
            if (v.length == 3) {
                try {
                    double temp     = Double.parseDouble(v[0].trim());
                    int    vibra    = Integer.parseInt(v[1].trim());
                    int    corrente = Integer.parseInt(v[2].trim());

                    // Grava no banco
                    stmt.setDouble(1, temp);
                    stmt.setInt(2, vibra);
                    stmt.setInt(3, corrente);
                    stmt.executeUpdate();

                    System.out.println("✓ Gravado no banco — T:" + temp + " V:" + vibra + " I:" + corrente);

                } catch (Exception e) {
                    System.out.println("Erro ao gravar no banco: " + e.getMessage());
                }
            }
        });

        System.out.println("Aguardando dados no tópico: " + TOPICO);
        Thread.currentThread().join();
    }

    static SSLSocketFactory criarSSLInseguro() throws Exception {
        TrustManager[] trustAll = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] c, String a) {}
                public void checkServerTrusted(X509Certificate[] c, String a) {}
            }
        };
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, trustAll, new java.security.SecureRandom());
        return ctx.getSocketFactory();
    }
}