package ru.spbau.daniil.smirnov.myftp.client;

/**
 * Interface which is used to create {@link Client}
 */
interface ClientFactory {
    /**
     * Creates {@link Client}
     * @return created client
     */
    Client createClient();
}
