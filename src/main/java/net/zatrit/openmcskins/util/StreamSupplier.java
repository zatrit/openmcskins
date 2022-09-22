package net.zatrit.openmcskins.util;

import java.io.InputStream;

@FunctionalInterface
public interface StreamSupplier {
    InputStream openStream() throws Exception;
}
