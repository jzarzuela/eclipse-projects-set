
package jsat.exceptions;

/**
 * This exception is thrown when the input into a model does not match the expectation of the model. 
 * @author Edward Raff
 */
public class ModelMismatchException extends RuntimeException
{

    public ModelMismatchException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ModelMismatchException(Throwable cause)
    {
        super(cause);
    }

    public ModelMismatchException(String message)
    {
        super(message);
    }

    public ModelMismatchException()
    {
        super();
    }
    
}
