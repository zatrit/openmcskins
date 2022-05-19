package net.zatrit.openmcskins.util.io;

import java.io.OutputStream;

public interface StreamAction {
    void apply(OutputStream stream) throws Exception;
}
