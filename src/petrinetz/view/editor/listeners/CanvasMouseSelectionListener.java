package petrinetz.view.editor.listeners;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import petrinetz.control.entities.Entity;
import petrinetz.control.entities.PetriNet;
import petrinetz.control.entities.Place;
import petrinetz.control.entities.Transition;
import petrinetz.view.editor.DocumentEditor;

/**
 * An event listener class, that makes the canvas edit its own selection.
 * @author PIAPAAI.ELTE
 */
public class CanvasMouseSelectionListener extends CanvasMouseListener {

    // <editor-fold defaultstate="opened" desc="Constructor.">

    /**
     * Constructor.
     * @param editor
     */
    public CanvasMouseSelectionListener(DocumentEditor editor)
    {
        super(editor);
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Event handler methods">

    /**
     * Mouse click handler.
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        PetriNet net = _editor.getDocument() != null && _editor.getDocument().net != null ? _editor.getDocument().net : null;

        if(net != null)
        {
            Entity selected = null;

            Iterator<Place> placeIt = net.places.values().iterator();

            while (placeIt.hasNext()) {
                Place actPlace = placeIt.next();

                if(selected != null) {
                    break;
                }else if(isEntityAtPoint(actPlace, e.getPoint()))
                {
                    selected = actPlace;
                }
            }

            Iterator<Transition> transIt = net.transitions.values().iterator();

            while (transIt.hasNext()) {
                Transition actTransition = transIt.next();

                if(selected != null) {
                    break;
                }else if(isEntityAtPoint(actTransition, e.getPoint()))
                {
                    selected = actTransition;
                }
            }

            if(selected != null) {
                if(e.isShiftDown()) {
                    _editor.addToSelection(selected);
                } else if(e.isAltDown()) {
                    _editor.removeFromSelection(selected);
                } else if(e.isControlDown()) {
                    _editor.addToOrRemoveFromSelection(selected);
                } else {
                    _editor.setSelection(selected);
                }
            }
            else if(!e.isShiftDown()) {
                _editor.setSelection(new LinkedList<Entity>());
            }
        }
    }

    /**
     * Mouse press handler.
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {

        if(handleDragAndDropStart(e))
            return;

        _editor.selectionBegin = e.getPoint();
        _editor.selectionEnd = e.getPoint();
        _editor.canvas.repaint();
    }

    /**
     * Mouse release handler.
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        if(handleDragAndDropEnd(e)
                || _editor.selectionBegin == null)
            return;

        _editor.selectionEnd = e.getPoint();

        java.awt.Point a = new java.awt.Point(
                Math.min(_editor.selectionBegin.x, _editor.selectionEnd.x),
                Math.min(_editor.selectionBegin.y, _editor.selectionEnd.y));
        Dimension b = new Dimension(
                Math.max(_editor.selectionBegin.x, _editor.selectionEnd.x) - a.x,
                Math.max(_editor.selectionBegin.y, _editor.selectionEnd.y) - a.y);

        List<Entity> newSelection = new LinkedList<Entity>();

        Iterator<Place> placeIt = _editor.getDocument().net.places.values().iterator();

        while (placeIt.hasNext()) {
            Place actPlace = placeIt.next();

            if(isEntityInRect(actPlace, a, b))
            {
                newSelection.add(actPlace);
            }
        }

        Iterator<Transition> transIt = _editor.getDocument().net.transitions.values().iterator();

        while (transIt.hasNext()) {
            Transition actTransition = transIt.next();

            if(isEntityInRect(actTransition, a, b))
            {
                newSelection.add(actTransition);
            }
        }

        if(e.isShiftDown()) {
            _editor.addToSelection(newSelection);
        } else if(e.isAltDown()) {
            _editor.removeFromSelection(newSelection);
        } else if(e.isControlDown()) {
            _editor.addToOrRemoveFromSelection(newSelection);
        } else {
            _editor.setSelection(newSelection);
        }

        _editor.selectionBegin = null;
        _editor.selectionEnd = null;

        super.mouseReleased(e);
    }

    /**
     * Mouse drag handler.
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if(handleDragAndDrop(e))
            return;

        if(_editor.isBeingSelected()) {
            _editor.selectionEnd = e.getPoint();
            _editor.canvas.repaint();
        }
    }

    // </editor-fold>

}
