package petrinetz.control;

/**
 * An exception class reprezents the errors that are specific to this application.
 * @author PIAPAAI.ELTE
 */
public class PNException extends Exception {

    // <editor-fold defaultstate="opened" desc="Properties">

    private String _level;

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructor">

    /**
     * Constructor.
     * @param message
     * @param level can be "Error" or "Warning"
     */
    public PNException(String message, String level) {

        super(message);

        _level = level;
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Methods">

    /**
     * Getter of the error level.
     * @return the level of the error ("Warning" or "Error")
     */
    public String getLevel()
    {
        return _level;
    }

    // </editor-fold>
}
