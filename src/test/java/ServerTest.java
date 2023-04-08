import org.example.ClientConnection;
import org.example.Server;
import org.junit.jupiter.api.Test;

import static org.junit.gen5.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerTest {
    @Test
    public void testBroadcast() throws IOException {

        String message = "Hello, world!";
        Map<String, ClientConnection> connections = new HashMap<>();
        ClientConnection connection1 = mock(ClientConnection.class);
        ClientConnection connection2 = mock(ClientConnection.class);
        connections.put("client1", connection1);
        connections.put("client2", connection2);


        Server.broadcast(message, connections);


        (connection1).sendMessage(message);
        (connection1).sendMessage("Server: OK!");
        (connection2).sendMessage(message);
        (connection2).sendMessage("Server: OK!");
    }
    @Test
    void testRemoveConnection() throws IOException {
        // Arrange
        Map<String, ClientConnection> connections = new HashMap<>();
        ClientConnection connection = mock(ClientConnection.class);
        String clientName = "testClient";
        when(connection.getClientName()).thenReturn(clientName);
        connections.put(clientName, connection);

        // Act
        Server.removeConnection(connection);

        // Assert
        (connections).remove(clientName);
        (connection).getClientName();

    }
    @Test
    public void testGenerateClientName() {
        String name = Server.generateClientName();
        assertTrue(name.startsWith("client-"), "Client name does not start with 'client-'");
    }
}
