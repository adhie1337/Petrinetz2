package petrinetz.view.editor.listeners;

import java.awt.event.MouseEvent;
import petrinetz.control.entities.PetriNet;
import petrinetz.control.entities.Transition;
import petrinetz.view.editor.DocumentEditor;

/**
 * A mouse listener of the canvas that handles edition of transitions.
 * @author PIAPAAI.ELTE
 */
public class CanvasMouseAddTransitionListener extends CanvasMouseListener {

    // <editor-fold defaultstate="opened" desc="Constructor">

    /**
     * Constructor.
     * @param editor
     */
    public CanvasMouseAddTransitionListener(DocumentEditor editor)
    {
        super(editor);
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Event handler methods">

    /**
     * Mouse click listener.
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        handleDragAndDropEnd(e);
    }

    /**
     * Mouse press listener.
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {

        if(handleDragAndDropStart(e))
            return;

        PetriNet net = _editor.getDocument() != null && _editor.getDocument().net != null ? _editor.getDocument().net : null;

        if(net != null && _editor.canvas.transitionToAdd == null)
        {
            Transition newTransition = new Transition(net);
            newTransition.x = (double)e.getX() / _editor.canvas.getZoom();
            newTransition.y = (double)e.getY() / _editor.canvas.getZoom();
            _editor.canvas.transitionToAdd = newTransition;

            if(e.isControlDown() || e.isShiftDown())
                _editor.addToSelection(_editor.canvas.transitionToAdd);
            else if(!e.isAltDown())
                _editor.setSelection(_editor.canvas.transitionToAdd);

            _editor.canvas.calculateMinSize();
            _editor.canvas.repaint();
        }
    }

    /**
     * Mouse drag listener.
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if(handleDragAndDrop(e))
            return;


        if(_editor.canvas.transitionToAdd != null)
        {
            _editor.canvas.transitionToAdd.x = (double)e.getX() / _editor.canvas.getZoom();
            _editor.canvas.transitionToAdd.y = (double)e.getY() / _editor.canvas.getZoom();
            _editor.canvas.calculateMinSize();
            _editor.canvas.repaint();
        }
    }

    /**
     * Mouse release listener.
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        if(_editor.canvas.transitionToAdd == null && handleDragAndDropEnd(e))
            return;

        PetriNet net = _editor.getDocument() != null && _editor.getDocument().net != null ? _editor.getDocument().net : null;

        if(net != null && _editor.canvas.transitionToAdd != null)
        {
            _editor.canvas.transitionToAdd.x = (double)e.getX() / _editor.canvas.getZoom();
            _editor.canvas.transitionToAdd.y = (double)e.getY() / _editor.canvas.getZoom();

            net.addTransition(_editor.canvas.transitionToAdd);

            _editor.canvas.transitionToAdd = null;
            _editor.canvas.calculateMinSize();
        }

        super.mouseReleased(e);
    }

    // </editor-fold>

}
