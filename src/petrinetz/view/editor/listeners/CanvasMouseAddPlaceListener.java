package petrinetz.view.editor.listeners;

import java.awt.event.MouseEvent;
import petrinetz.control.entities.PetriNet;
import petrinetz.control.entities.Place;
import petrinetz.view.editor.DocumentEditor;

/**
 * A mouse listener of the canvas that handles edition of places.
 * @author PIAPAAI.ELTE
 */
public class CanvasMouseAddPlaceListener extends CanvasMouseListener {

    // <editor-fold defaultstate="opened" desc="Constructor">

    public CanvasMouseAddPlaceListener(DocumentEditor editor)
    {
        super(editor);
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Event handler methods">

    /**
     * Mouse click handler method.
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        handleDragAndDropEnd(e);
    }

    /**
     * Mouse press handler method.
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {

        if(handleDragAndDropStart(e))
            return;

        PetriNet net = _editor.getDocument() != null && _editor.getDocument().net != null ? _editor.getDocument().net : null;

        if(net != null && _editor.canvas.placeToAdd == null)
        {
            Place newPlace = new Place(net);
            newPlace.x = (double)e.getX() / _editor.canvas.getZoom();
            newPlace.y = (double)e.getY() / _editor.canvas.getZoom();
            _editor.canvas.placeToAdd = newPlace;

            if(e.isControlDown() || e.isShiftDown())
                _editor.addToSelection(_editor.canvas.placeToAdd);
            else if(!e.isAltDown())
                _editor.setSelection(_editor.canvas.placeToAdd);

            _editor.canvas.calculateMinSize();
            _editor.canvas.repaint();
        }
    }

    /**
     * Mouse drag handler method
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if(handleDragAndDrop(e))
            return;

        if(_editor.canvas.placeToAdd != null)
        {
            _editor.canvas.placeToAdd.x = (double)e.getX() / _editor.canvas.getZoom();
            _editor.canvas.placeToAdd.y = (double)e.getY() / _editor.canvas.getZoom();
            _editor.canvas.calculateMinSize();
            _editor.canvas.repaint();
        }
    }

    /**
     * Mouse release handler method.
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        if(_editor.canvas.placeToAdd == null && handleDragAndDropEnd(e))
            return;

        PetriNet net = _editor.getDocument() != null && _editor.getDocument().net != null ? _editor.getDocument().net : null;

        if(net != null && _editor.canvas.placeToAdd != null)
        {
            _editor.canvas.placeToAdd.x = (double)e.getX() / _editor.canvas.getZoom();
            _editor.canvas.placeToAdd.y = (double)e.getY() / _editor.canvas.getZoom();

            net.addPlace(_editor.canvas.placeToAdd);

            _editor.canvas.placeToAdd = null;
            _editor.canvas.calculateMinSize();
        }

        super.mouseReleased(e);
    }

    // </editor-fold>

}
