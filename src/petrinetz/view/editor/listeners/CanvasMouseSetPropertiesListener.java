package petrinetz.view.editor.listeners;

import java.awt.event.MouseEvent;
import java.util.Iterator;
import petrinetz.Petrinetz;
import petrinetz.control.entities.Edge;
import petrinetz.control.entities.Entity;
import petrinetz.control.entities.PetriNet;
import petrinetz.control.entities.Place;
import petrinetz.control.entities.Transition;
import petrinetz.view.editor.DocumentEditor;
import petrinetz.view.editor.EdgePropertiesDialog;
import petrinetz.view.editor.PlacePropertiesDialog;
import petrinetz.view.editor.TransitionPropertiesDialog;

/**
 * An event listener class that is responsible for the "propery editing" of the entities and edges.
 * @author PIAPAAI.ELTE
 */
public class CanvasMouseSetPropertiesListener extends CanvasMouseListener {

    // <editor-fold defaultstate="opened" desc="Properties">

    private TransitionPropertiesDialog transitionProperties;
    private PlacePropertiesDialog placeProperties;
    private EdgePropertiesDialog edgeProperties;

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructor">

    /**
     * Constructor.
     * @param editor
     */
    public CanvasMouseSetPropertiesListener(DocumentEditor editor)
    {
        super(editor);
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Event handler methods">

    /**
     * Mouse click event handler.
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        PetriNet net = _editor.getDocument() != null && _editor.getDocument().net != null ? _editor.getDocument().net : null;

        if(net != null)
        {
            Entity selected = null;
            Edge selectedEdge = null;

            Iterator<Edge> edgeIt = net.edges.iterator();

            while (edgeIt.hasNext()) {
                Edge actEdge = edgeIt.next();

                if(isEdgeAtPoint(actEdge, e.getPoint()))
                {
                    selectedEdge = actEdge;
                }
            }

            System.out.println("");

            Iterator<Transition> transIt = net.transitions.values().iterator();

            while (transIt.hasNext()) {
                Transition actTransition = transIt.next();

                if(isEntityAtPoint(actTransition, e.getPoint()))
                {
                    selected = actTransition;
                }
            }

            Iterator<Place> placeIt = net.places.values().iterator();

            while (placeIt.hasNext()) {
                Place actPlace = placeIt.next();

                if(isEntityAtPoint(actPlace, e.getPoint()))
                {
                    selected = actPlace;
                }
            }

            if(selected != null) {
                if(selected instanceof Transition) {
                    if(transitionProperties == null) {
                        transitionProperties = new TransitionPropertiesDialog(Petrinetz.getApplication().getMainFrame());
                    }

                    transitionProperties.setTransition((Transition)selected);
                    Petrinetz.getApplication().show(transitionProperties);
                }
                else if(selected instanceof Place) {
                    if(placeProperties == null) {
                        placeProperties = new PlacePropertiesDialog(Petrinetz.getApplication().getMainFrame());
                    }

                    placeProperties.setPlace((Place)selected);
                    Petrinetz.getApplication().show(placeProperties);
                }
            }
            else if(selectedEdge != null) {
                if(edgeProperties == null) {
                    edgeProperties = new EdgePropertiesDialog(Petrinetz.getApplication().getMainFrame());
                }

                edgeProperties.setEdge(selectedEdge);
                Petrinetz.getApplication().show(edgeProperties);
            }
        }
    }
}
