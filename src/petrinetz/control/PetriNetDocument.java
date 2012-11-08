package petrinetz.control;

import petrinetz.util.PetrinetUtil;
import petrinetz.control.entities.PetriNet;
import petrinetz.util.CompareUtil;

/**
 * A class that reprezents a petri net document file.
 * @author PIAPAAI.ELTE
 */
public class PetriNetDocument{

    // <editor-fold defaultstate="opened" desc="Properties">

    private String _fileContents;

    /**
     * The path to the file containing this net. "" or null means there isn't any.
     */
    public String filePath;

    /**
     * The name of the document. New documents get the name "New document", saved ones get the file name as document name.
     */
    public String documentName;

    /**
     * The Petri net instance.
     */
    public PetriNet net;

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructors">

    /**
     * Constructor.
     */
    public PetriNetDocument() {
        _initPetriNetDocument(null, "", "New Document");
    }

    /**
     * Constructor.
     * @param filePath the path to the file containing these contents.
     * @param fileContents the contents (in ndr format).
     */
    public PetriNetDocument(String filePath, String fileContents) {
        _initPetriNetDocument(filePath, fileContents, "New Document");
    }

    /**
     * Constructor.
     * @param filePath the path to the file containing these contents.
     * @param fileContents the contents (in ndr format).
     * @param documentName the name of the document
     */
    public PetriNetDocument(String filePath, String fileContents, String documentName) {
        _initPetriNetDocument(filePath, fileContents, documentName);
    }

    private void _initPetriNetDocument(String filePath, String fileContents, String documentName) {
        this.documentName = documentName;
        this.filePath = filePath;
        this.setFileContents(fileContents);
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Misc functions">

    /**
     * File contetns getter.
     * @return file contents
     */
    public String getFileContents() {
        _fileContents = PetrinetUtil.fromPetriNet(net);

        return _fileContents;
    }

    /**
     * File contetns setter.
     * @param value new contetns of the file. Setting "" or null means erasing.
     */
    public void setFileContents(String value) {
        _fileContents = value;

        if(value != null && !value.equals(""))
        {
            try {
                net = PetrinetUtil.fromString(_fileContents);
            }
            catch(PNException e) {
                ErrorController.showError(e.getMessage(), e.getLevel());
            }
        }
        else
        {
            net = new PetriNet();
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Overridden methods">

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof PetriNetDocument)) {
            return false;
        }

        PetriNetDocument otherD = (PetriNetDocument)other;

        return CompareUtil.compare(getFileContents(), otherD.getFileContents())
                && CompareUtil.compare(filePath, otherD.filePath)
                && CompareUtil.compare(documentName,otherD.documentName)
                && CompareUtil.compare(net, otherD.net);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this._fileContents != null ? this._fileContents.hashCode() : 0);
        hash = 37 * hash + (this.filePath != null ? this.filePath.hashCode() : 0);
        hash = 37 * hash + (this.documentName != null ? this.documentName.hashCode() : 0);
        hash = 37 * hash + (this.net != null ? this.net.hashCode() : 0);
        return hash;
    }

    // </editor-fold>

}
