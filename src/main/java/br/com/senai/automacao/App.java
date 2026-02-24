package br.com.senai.automacao;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

public class App {

    // ---- CONFIGURAÇÕES MQTT (HiveMQ Cloud - TLS) ----
    static final String BROKER   = "ssl://8499505b5a944d7fb9741e0ab74b8610.s1.eu.hivemq.cloud:8883";
    static final String CLIENT_ID = "JavaBackend_Ryhan_Motor";
    static final String TOPICO   = "senai/ryhan/motor/dados";
    static final String USUARIO  = "ryhan";
    static final String SENHA    = "Servidor123";

    public static void main(String[] args) throws Exception {

        // Cria cliente MQTT com persistência em memória
        MqttClient client = new MqttClient(BROKER, CLIENT_ID, new MemoryPersistence());

        // ---- OPÇÕES DE CONEXÃO ----
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USUARIO);
        options.setPassword(SENHA.toCharArray());
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);

        // TLS sem validação de certificado (equivalente ao setInsecure() do ESP32)
        // Em produção: carregar o certificado raiz da HiveMQ
        options.setSocketFactory(criarSSLInseguro());

        // ---- CONECTA ----
        System.out.println("Conectando ao Broker HiveMQ Cloud (TLS)...");
        client.connect(options);
        System.out.println("Conectado com sucesso!");

        // ---- SUBSCREVE NO TÓPICO ----
        client.subscribe(TOPICO, (topic, msg) -> {
            String payload = new String(msg.getPayload());

            // Payload esperado: "temp,vibra,corrente"
            String[] valores = payload.split(",");

            if (valores.length == 3) {
                double temp     = Double.parseDouble(valores[0]);
                int    vibra    = Integer.parseInt(valores[1]);
                int    corrente = Integer.parseInt(valores[2]);

                System.out.println("=== Dados coletados com sucesso ===");
                System.out.printf("  Temperatura : %.1f °C%n", temp);
                System.out.printf("  Vibração    : %d mm/s%n", vibra);
                System.out.printf("  Corrente    : %d A%n", corrente);
                System.out.println("===================================");
            } else {
                System.out.println("Payload inválido recebido: " + payload);
            }
        });

        System.out.println("Aguardando dados do ESP32 no tópico: " + TOPICO);

        // Mantém o programa rodando indefinidamente
        Thread.currentThread().join();
    }

    // ---- TLS SEM VALIDAÇÃO DE CERTIFICADO ----
    // Equivalente ao setInsecure() do WiFiClientSecure no ESP32
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