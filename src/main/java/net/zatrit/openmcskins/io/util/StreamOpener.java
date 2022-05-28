package net.zatrit.openmcskins.io.util;

import java.io.InputStream;

@FunctionalInterface
public interface StreamOpener {
    InputStream openStream() throws Exception;
}
