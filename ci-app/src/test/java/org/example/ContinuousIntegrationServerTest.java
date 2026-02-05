package org.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContinuousIntegrationServerTest {

    @Test
    void rootRequestReturnsCiJobDone() throws Exception {
        Server server = new Server(0); // 0 = pick a free port
        server.setHandler(new ContinuousIntegrationServer());

        try {
            server.start();

            int port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
            URL url = new URL("http://localhost:" + port + "/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int status = connection.getResponseCode();
            assertEquals(200, status);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (body.length() > 0) {
                        body.append('\n');
                    }
                    body.append(line);
                }
                assertEquals("CI job done", body.toString());
            }
        } finally {
            server.stop();
        }
    }
}

