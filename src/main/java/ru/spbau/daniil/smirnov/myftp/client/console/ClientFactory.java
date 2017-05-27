package ru.spbau.daniil.smirnov.myftp.client.console;

import ru.spbau.daniil.smirnov.myftp.client.Client;

/**
 * Interface which is used to create {@link Client}
 */
interface ClientFactory {
    /**
     * Creates {@link Client}
     * @return created client
     */
    Client createClient(String serverAddress);
}
