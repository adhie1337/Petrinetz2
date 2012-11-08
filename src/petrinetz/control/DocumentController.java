package petrinetz.control;

import java.nio.file.Paths;
import java.util.LinkedList;
import org.jdesktop.application.Action;
import petrinetz.Petrinetz;
import petrinetz.view.MainFrame;

/**
 * The class responsible for the control of the documents being currently open.
 * @author PIAPAAI.ELTE
 */
public class DocumentController {

    // <editor-fold defaultstate="opened" desc="Properties">

    /**
     * The storage of the currently opened document references.
     */
    public LinkedList<PetriNetDocument> openedDocuments;

    private FileController   _fileController;

    private PetriNetDocument _currentDocument;

    private int _nextNewIndex = 0;

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Internal functions">

    /**
     * The function that actually loads the file from the given path.
     * @param path The path of the file to read
     * @return the contents of the file
     * @throws PNException when an error is raised during the read (like file doesn't exist or file not readable)
     */
    private String reloadFile(String path)
        throws PNException
    {
        return _fileController.readFile(path);
    }

    /**
     * Writes a document to the specified file path, using the Petrinet serializer.
     * @param document the document to write to the file
     * @param _fileName the path to the file to write to.
     * @throws PNException when the document is null, or an error occured while writing the file
     */
    public void writeToFile(PetriNetDocument document, String _fileName)
            throws PNException
    {
        if(document != null)
        {
            if(_fileName == null || _fileName.equals(""))
            {
                _fileName = _fileController.selectFile(false);
            }

            if(_fileName != null && !_fileName.trim().equals(""))
            {
                document.documentName = Paths.get(_fileName).getFileName().toString();
                _fileController.writeFile(_fileName, document.getFileContents());
            }
        }
        else
        {
            throw new PNException("NothingToBeDone", "Warning");
        }
    }

    /**
     * Writes the document to it's given file path. When the path is not given to the document instance, the application will make the user select a file.
     * @param document the document to write to the file
     * @throws PNException when the document is null or an error occured while writing the file
     */
    public void writeToFile(PetriNetDocument document)
            throws PNException
    {
        if(document != null)
        {
            if(document.filePath == null || document.filePath.equals(""))
            {
                writeToFile(document, _fileController.selectFile(false));
            }
            else
            {
                writeToFile(document, document.filePath);
            }
        }
        else
        {
            throw new PNException("NothingToBeDone", "Warning");
        }
    }

    /**
     * Writes the currently opened document to a file. When to filepath is specified by the document, the user will have to select a file.
     * @throws PNException when an error occured while writing the file
     */
    public void writeToFile()
            throws PNException
    {
        writeToFile(_currentDocument);
    }

    /**
     * Removes the document from the currently opened documents.
     * @param value the document that is being removed
     */
    public void removeDocument(PetriNetDocument value)
    {
        if(openedDocuments.contains(value))
        {
            openedDocuments.remove(value);

            getMainFrame().removePage(value);

            if(_currentDocument == value)
            {
                if(openedDocuments.size() == 0)
                {
                    newFileAction();
                }
                else
                {
                    setCurrentDocument(openedDocuments.getFirst());
                }
            }
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Methods">

    private MainFrame getMainFrame()
    {
        return (MainFrame)Petrinetz.getApplication().getMainView();
    }

    /**
     * Sets the document to the currently showed document. It commands the view to show the given document, as a side-effect. If document is not stored as a currently opened document, the method will do the addition, and create an editor for the document, in the view section.
     * @param value
     */
    public void setCurrentDocument(PetriNetDocument value)
    {
        if(_currentDocument == null || !_currentDocument.equals(value))
        {
            _currentDocument = value;

            if(!openedDocuments.contains(value))
            {
                getMainFrame().getNewPage(value);
                openedDocuments.add(value);
            }
            else
            {
                getMainFrame().setCurrentPage(value);
            }
        }
    }

    /**
     * Gives the document that is currently being shown to the user.
     * @return the document
     */
    public PetriNetDocument getCurrentDocument()
    {
        return _currentDocument;
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Actions">

    @Action
    public void newFileAction()
    {
        setCurrentDocument(
                new PetriNetDocument(
                    null,
                    "",
                    "New Document"
                        + (_nextNewIndex > 0 ? (" " + _nextNewIndex) : "")));
        ++_nextNewIndex;
    }

    @Action
    public void loadFileAction()
    {
        try
        {
            String filePath = _fileController.selectFile(true);

            if(filePath == null || filePath.equals(""))
            {
                return;
            }

            String fileContents = _fileController.readFile(filePath);
            PetriNetDocument doc = new PetriNetDocument(filePath, fileContents);
            doc.documentName = Paths.get(filePath).getFileName().toString();
            setCurrentDocument(doc);
        }
        catch(PNException e)
        {
            ErrorController.showError(e.getMessage(), e.getLevel());
        }

    }

    @Action
    public void reloadFileAction()
    {
        try
        {
            if(_currentDocument == null)
            {
                throw new PNException("NothingToBeDone", "Error");
            }

            _currentDocument.setFileContents(reloadFile(_currentDocument.filePath));

            getMainFrame().updateCurrentLabel();
            getMainFrame().getCurrentEditor().setDocument(_currentDocument);
        }
        catch(PNException e)
        {
            ErrorController.showError(e.getMessage(), e.getLevel());
        }

    }

    @Action
    public void saveFileAction()
    {
        try
        {
            writeToFile();

            getMainFrame().updateCurrentLabel();
        }
        catch(PNException e)
        {
            ErrorController.showError(e.getMessage(), e.getLevel());
        }
    }

    @Action
    public void saveFileAsAction()
    {
        try
        {
            writeToFile(_currentDocument, "");

        }
        catch(PNException e)
        {
            ErrorController.showError(e.getMessage(), e.getLevel());
        }
    }

    @Action
    public void closeFileAction()
    {
        removeDocument(getCurrentDocument());
    }


    @Action
    public void changeCurrentFileAction()
    {
        setCurrentDocument(getMainFrame().getCurrentPage());
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Singleton design pattern">

    /**
     * The singleton reference to the only instance of the controller class.
     */
    private static DocumentController _instance;

    /**
     * Gets the singleton reference to the only instance of the controller class.
     */
    public static DocumentController getInstance()
    {
        if(_instance == null)
        {
            _instance = new DocumentController();
        }

        return _instance;
    }

    /**
     * Constructor.
     */
    private DocumentController()
    {
        if(_instance != null)
        {
            throw new Error("Invalid use of singleton pattern!");
        }

        _fileController  = new FileController();
        openedDocuments = new LinkedList<PetriNetDocument>();
    }

    // </editor-fold>
}
