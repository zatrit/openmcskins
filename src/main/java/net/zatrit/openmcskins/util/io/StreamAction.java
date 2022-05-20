package net.zatrit.openmcskins.util.io;

import java.io.OutputStream;

@FunctionalInterface
public interface StreamAction {
    void apply(OutputStream stream) throws Exception;
}
