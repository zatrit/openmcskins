package net.zatrit.openmcskins.interfaces.io;

import java.io.InputStream;

@FunctionalInterface
public interface StreamOpener {
    InputStream openStream() throws Exception;
}
