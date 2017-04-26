package ru.spbau.daniil.smirnov.myftp.server;

/**
 * Interface which is used to create {@link Server}
 */
interface ServerFactory {
    /**
     * Creates {@link Server}
     * @return created server
     */
    Server createServer();
}
