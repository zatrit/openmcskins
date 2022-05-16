package net.zatrit.openmcskins.util.io;

import java.io.InputStream;

@FunctionalInterface
public interface StreamOpener {
    InputStream openStream() throws Exception;
}
