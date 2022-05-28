package net.zatrit.openmcskins.io.util;

import java.io.OutputStream;

@FunctionalInterface
public interface StreamAction {
    void apply(OutputStream stream) throws Exception;
}
