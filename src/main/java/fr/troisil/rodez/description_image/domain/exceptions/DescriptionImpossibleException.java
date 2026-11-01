package fr.troisil.rodez.description_image.domain.exceptions;

public class DescriptionImpossibleException extends Exception {

    public DescriptionImpossibleException(String errorMessage) {
        super(errorMessage);
    }

    public DescriptionImpossibleException(
        String errorMessage,
        Throwable cause
    ) {
        super(errorMessage, cause);
    }
}
