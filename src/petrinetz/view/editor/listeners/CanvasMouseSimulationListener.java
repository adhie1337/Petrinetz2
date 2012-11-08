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
 * A mouse listener off the canvas that handles the simulation commands.
 * @author PIAPAAI.ELTE
 */
public class CanvasMouseSimulationListener extends CanvasMouseListener {

    // <editor-fold defaultstate="opened" desc="Constructor">

    /**
     * Constructor.
     * @param editor
     */
    public CanvasMouseSimulationListener(DocumentEditor editor)
    {
        super(editor);
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Event handler methods">

    /**
     * Mouse release handler.
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        PetriNet net = _editor.getDocument() != null && _editor.getDocument().net != null ? _editor.getDocument().net : null;

        if(net == null)
            return;

        Entity selected = null;

        Iterator<Transition> transIt = net.transitions.values().iterator();

        while (transIt.hasNext()) {
            Transition actTransition = transIt.next();

            if(isEntityAtPoint(actTransition, e.getPoint()))
            {
                selected = actTransition;
            }
        }

        Iterator<Edge> it = net.edges.iterator();

        if(selected != null && (selected instanceof Transition)) {
            while(it.hasNext()) {
                Edge edge = it.next();
                if(edge.to.equals(selected) && ((Place)edge.from).weight < edge.weight)
                    return;
            }
        }
        else return;

        it = net.edges.iterator();
        while(it.hasNext()) {
            Edge edge = it.next();
            if(edge.to.equals(selected))
                ((Place)edge.from).weight -= edge.weight;
            if(edge.from.equals(selected))
                ((Place)edge.to).weight += edge.weight;
        }

        super.mouseReleased(e);
    }

    // </editor-fold>

}
