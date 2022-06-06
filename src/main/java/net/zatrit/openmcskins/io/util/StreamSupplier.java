package net.zatrit.openmcskins.io.util;

import java.io.InputStream;

@FunctionalInterface
public interface StreamSupplier {
    InputStream openStream() throws Exception;
}
