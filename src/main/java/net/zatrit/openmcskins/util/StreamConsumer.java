package net.zatrit.openmcskins.util;

import java.io.OutputStream;

@FunctionalInterface
public interface StreamConsumer {
    void apply(OutputStream stream) throws Exception;
}