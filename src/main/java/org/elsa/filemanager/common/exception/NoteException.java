package org.elsa.filemanager.common.exception;

/**
 * @author valor
 * @date 2018/9/25 16:22
 */
public class NoteException extends RuntimeException {

    public NoteException(String message) {
        super(message);
    }

    public NoteException(Throwable cause) {
        super(cause);
    }
}
