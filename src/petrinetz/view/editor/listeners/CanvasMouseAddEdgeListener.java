package petrinetz.view.editor.listeners;

import java.awt.event.MouseEvent;
import java.util.Iterator;
import petrinetz.control.entities.Edge;
import petrinetz.control.entities.Entity;
import petrinetz.control.entities.PetriNet;
import petrinetz.control.entities.Place;
import petrinetz.control.entities.Transition;
import petrinetz.view.editor.DocumentEditor;

/**
 * A mouse listener of the canvas that handles edition of edges.
 * @author PIAPAAI.ELTE
 */
public class CanvasMouseAddEdgeListener extends CanvasMouseListener {

    // <editor-fold defaultstate="opened" desc="Constructor">

    /**
     * Constructor.
     * @param editor the editor instance whose events it listens to.
     */
    public CanvasMouseAddEdgeListener(DocumentEditor editor)
    {
        super(editor);
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Event handler methods">

    /**
     * Mouse pressed event handler.
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        PetriNet net = _editor.getDocument() != null && _editor.getDocument().net != null ? _editor.getDocument().net : null;

        if(net != null && _editor.getEditorMode() == DocumentEditor.EditorMode.AddEdge)
        {
            Entity found = null;

            Iterator<Place> placeIt = net.places.values().iterator();

            while (placeIt.hasNext()) {
                Place actPlace = placeIt.next();

                if(found != null) {
                    break;
                }else if(isEntityAtPoint(actPlace, e.getPoint()))
                {
                    found = actPlace;
                }
            }

            Iterator<Transition> transIt = net.transitions.values().iterator();

            while (transIt.hasNext()) {
                Transition actTransition = transIt.next();

                if(found != null) {
                    break;
                }else if(isEntityAtPoint(actTransition, e.getPoint()))
                {
                    found = actTransition;
                }
            }

            if(found != null)
            {
                _editor.canvas.edgeToAddStart = found;
            }

            _editor.canvas.repaint();
        }
    }

    /**
     * Mouse dragged event handler.
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        PetriNet net = _editor.getDocument() != null && _editor.getDocument().net != null ? _editor.getDocument().net : null;

        if(net != null && _editor.canvas.edgeToAddStart != null && _editor.getEditorMode() == DocumentEditor.EditorMode.AddEdge)
        {
            Entity found = null;

            Iterator<Place> placeIt = net.places.values().iterator();

            while (placeIt.hasNext()) {
                Place actPlace = placeIt.next();

                if(found != null) {
                    break;
                }else if(isEntityAtPoint(actPlace, e.getPoint()))
                {
                    found = actPlace;
                }
            }

            Iterator<Transition> transIt = net.transitions.values().iterator();

            while (transIt.hasNext()) {
                Transition actTransition = transIt.next();

                if(found != null) {
                    break;
                }else if(isEntityAtPoint(actTransition, e.getPoint()))
                {
                    found = actTransition;
                }
            }

            if(found == null)
            {
                _editor.canvas.edgeToAddEnd = new java.awt.Point(e.getX(), e.getY());
                _editor.canvas.edgeToAddFinish = null;
            }
            else if(found != null && found != _editor.canvas.edgeToAddStart)
            {
                _editor.canvas.edgeToAddFinish = found;
            }

            _editor.canvas.repaint();
        }
    }

    /**
     * Mouse released event handler.
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        PetriNet net = _editor.getDocument() != null && _editor.getDocument().net != null ? _editor.getDocument().net : null;

        if(net != null)
        {
            if(_editor.canvas.edgeToAddStart != null
                    && _editor.canvas.edgeToAddFinish != null
                    && !_editor.canvas.edgeToAddStart.getClass().equals(_editor.canvas.edgeToAddFinish.getClass()))
            {
                Edge newEdge = new Edge(net);
                newEdge.from = _editor.canvas.edgeToAddStart;
                newEdge.to = _editor.canvas.edgeToAddFinish;
                net.edges.add(newEdge);
            }
        }

        _editor.canvas.edgeToAddStart  = null;
        _editor.canvas.edgeToAddFinish = null;
        _editor.canvas.edgeToAddEnd = null;

        super.mouseReleased(e);
    }


    // </editor-fold>

}
