package ru.spbau.daniil.smirnov.myftp.client;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CommandLineArgumentsHandlerTest {
    @Test
    public void handleTest() throws Exception {
        ClientSavingFactory factory = new ClientSavingFactory();
        CommandLineArgumentsHandler handler = new CommandLineArgumentsHandler(factory, mock(PrintStream.class));
        handler.handle(new String[]{"list", "/Users"});
        handler.handle(new String[]{"get", "/file"});

        assertEquals(2, factory.clients.size());
        verify(factory.clients.get(0), times(1)).list("/Users");
        verify(factory.clients.get(1), times(1)).get("/file");
    }

    private static class ClientSavingFactory implements ClientFactory {
        @NotNull
        final List<Client> clients;

        ClientSavingFactory() {
            this.clients = new LinkedList<>();
        }

        @Override
        public Client createClient() {
            Client client = mock(Client.class);
            clients.add(client);
            return client;
        }
    }
}