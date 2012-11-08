package petrinetz.control;

import java.util.Collection;
import java.util.LinkedList;
import org.jdesktop.application.Action;
import petrinetz.Petrinetz;
import petrinetz.control.entities.Entity;
import petrinetz.control.entities.PetriNet;
import petrinetz.util.PetrinetUtil;
import petrinetz.view.MainFrame;
import petrinetz.view.Toolbar;
import petrinetz.view.editor.Canvas;
import petrinetz.view.editor.DocumentEditor;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class EditorController {

    // <editor-fold defaultstate="opened" desc="Singleton pattern">

    /**
     * The singleton reference to the only instance of the controller class.
     */
    private static EditorController _instance;

    /**
     * Gets the singleton reference to the only instance of the controller class.
     */
    public static EditorController getInstance(){

        if(_instance == null)
            _instance = new EditorController();

        return _instance;
    }

    /**
     * Constructor
     */
    private EditorController() {}

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Actions">

    /**
     * Sets the current editor state to "Selection".
     */
    @Action
    public void setSelectionEditorStateAction()
    {
        MainFrame mainFrame = (MainFrame)Petrinetz.getApplication().getMainView();
        if(!((Toolbar)mainFrame.getToolBar()).btnSelection.isSelected())
        {
            ((Toolbar)mainFrame.getToolBar()).btnSelection.setSelected(true);
        }
        mainFrame.setCurrentMode(DocumentEditor.EditorMode.Selection);
    }

    /**
     * Sets the current editor state to "add places".
     */
    @Action
    public void setAddPlaceEditorStateAction()
    {
        MainFrame mainFrame = (MainFrame)Petrinetz.getApplication().getMainView();
        if(!((Toolbar)mainFrame.getToolBar()).btnAddPlace.isSelected())
        {
            ((Toolbar)mainFrame.getToolBar()).btnAddPlace.setSelected(true);
        }

        mainFrame.setCurrentMode(DocumentEditor.EditorMode.AddPlace);
    }

    /**
     * Sets the current editor state to "add transitions".
     */
    @Action
    public void setAddTransitionEditorStateAction()
    {
        MainFrame mainFrame = (MainFrame)Petrinetz.getApplication().getMainView();
        if(!((Toolbar)mainFrame.getToolBar()).btnAddTransition.isSelected())
        {
            ((Toolbar)mainFrame.getToolBar()).btnAddTransition.setSelected(true);
        }

        mainFrame.setCurrentMode(DocumentEditor.EditorMode.AddTransition);
    }

    /**
     * Sets the current editor state to "add edges".
     */
    @Action
    public void setAddEdgeEditorStateAction()
    {
        MainFrame mainFrame = (MainFrame)Petrinetz.getApplication().getMainView();
        if(!((Toolbar)mainFrame.getToolBar()).btnAddEdge.isSelected())
        {
            ((Toolbar)mainFrame.getToolBar()).btnAddEdge.setSelected(true);
        }

        mainFrame.setCurrentMode(DocumentEditor.EditorMode.AddEdge);
    }

    /**
     * Sets the current editor state to "set properties".
     */
    @Action
    public void setSetPropertiesEditorStateAction()
    {
        MainFrame mainFrame = (MainFrame)Petrinetz.getApplication().getMainView();
        if(!((Toolbar)mainFrame.getToolBar()).btnSetProperties.isSelected())
        {
            ((Toolbar)mainFrame.getToolBar()).btnSetProperties.setSelected(true);
        }

        mainFrame.setCurrentMode(DocumentEditor.EditorMode.SetProperties);
    }

    /**
     * Sets the current editor state to "simulation".
     */
    @Action
    public void simulationEditorStateAction()
    {
        MainFrame mainFrame = (MainFrame)Petrinetz.getApplication().getMainView();
        if(!((Toolbar)mainFrame.getToolBar()).btnSimulate.isSelected())
        {
            ((Toolbar)mainFrame.getToolBar()).btnSimulate.setSelected(true);
        }

        mainFrame.setCurrentMode(DocumentEditor.EditorMode.Simulation);
    }

    /**
     * Copies the selection of the currently selected editor to the clipboard.
     */
    @Action
    public void copyAction()
    {
        MainFrame mainFrame = (MainFrame)Petrinetz.getApplication().getMainView();
        PetrinetUtil.toClipBoard(mainFrame.getCurrentPage().net.getSelection());
    }

    /**
     * Cuts the selection of the currently selected editor to the clipboard.
     */
    @Action
    public void cutAction()
    {
        MainFrame mainFrame = (MainFrame)Petrinetz.getApplication().getMainView();
        PetrinetUtil.toClipBoard(mainFrame.getCurrentPage().net.getSelection(true));
        mainFrame.getCurrentEditor().repaint();
    }

    /**
     * Pastes the contents of the clipboard to the currently selected editor.
     */
    @Action
    public void pasteAction()
    {
        MainFrame mainFrame = (MainFrame)Petrinetz.getApplication().getMainView();
        PetriNet net = PetrinetUtil.fromClipboard();
        mainFrame.getCurrentPage().net.addAll(net);
        Collection<Entity> selected = new LinkedList<Entity>();
        selected.addAll(net.places.values());
        selected.addAll(net.transitions.values());
        mainFrame.getCurrentEditor().setSelection(selected);
        mainFrame.getCurrentEditor().repaint();
    }

    /**
     * Duplicates the selection.
     */
    @Action
    public void duplicateAction()
    {
        MainFrame mainFrame = (MainFrame)Petrinetz.getApplication().getMainView();
        PetriNet net = mainFrame.getCurrentPage().net.getSelection(false);
        net.translate(Canvas.TRANSITION_WIDTH, Canvas.TRANSITION_WIDTH);
        mainFrame.getCurrentPage().net.addAll(net);
        Collection<Entity> selected = new LinkedList<Entity>();
        selected.addAll(net.places.values());
        selected.addAll(net.transitions.values());
        mainFrame.getCurrentEditor().setSelection(selected);
        mainFrame.getCurrentEditor().repaint();
    }

    /**
     * Deletes all items in the selection.
     */
    @Action
    public void deleteAction()
    {
        MainFrame mainFrame = (MainFrame)Petrinetz.getApplication().getMainView();
        mainFrame.getCurrentPage().net.getSelection(true);
        mainFrame.getCurrentEditor().repaint();
    }

    // </editor-fold>

}
