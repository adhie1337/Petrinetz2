package petrinetz.view.editor.listeners;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Iterator;
import javax.swing.JScrollBar;
import petrinetz.control.entities.Edge;
import petrinetz.control.entities.Entity;
import petrinetz.control.entities.PetriNet;
import petrinetz.control.entities.Place;
import petrinetz.control.entities.Transition;
import petrinetz.view.editor.Canvas;
import petrinetz.view.editor.DocumentEditor;

/**
 * The base mouse listener of the canvas. Sets up itself, provides functions that determine whether an entity or edge is at a given point or not. Also provides functions to determine the contents of a selection rect. Handles drag and drop and zoom operations.
 * @author PIAPAAI.ELTE
 */
public abstract class CanvasMouseListener extends MouseAdapter {

    // <editor-fold defaultstate="opened" desc="Properties">

    protected DocumentEditor _editor;

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructor">

    /**
     * Constructor.
     * @param editor
     */
    public CanvasMouseListener(DocumentEditor editor)
    {
        _editor = editor;
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Helper methods">

    private int getYAt(double fromX, double fromY, double toX, double toY, double atX) {
        if(Math.abs(fromX - toX) < 25 || atX < fromX && atX < toX || atX > fromX && atX > toX)
            return -1000;

        return (int)(fromY + (float)(atX - fromX) / (float)(toX - fromX) * (float)(toY - fromY));
    }

    private int getXAt(double fromY, double fromX, double toY, double toX, double atY) {
        if(Math.abs(fromY - toY) < 25 || atY < fromY && atY < toY || atY > fromY && atY > toY)
            return -1000;

        return (int)(fromX + (float)(atY - fromY) / (float)(toY - fromY) * (float)(toX - fromX));
    }

    protected Boolean isEdgeAtPoint(Edge e, java.awt.Point p) {
        double z = _editor.canvas.getZoom();

        java.awt.Point p1 = new java.awt.Point((int)e.from.x, (int)e.from.y);
        java.awt.Point p2 = new java.awt.Point((int)e.to.x, (int)e.to.y);

        return (p1.x < p2.x)
                && (Math.abs(p.y - getYAt(p1.x * z, p1.y * z, p2.x * z, p2.y * z, p.x)) <= 15)
            || (p1.x >= p2.x)
                && (Math.abs(p.y - getYAt(p2.x * z, p2.y * z, p1.x * z, p1.y * z, p.x)) <= 15)
            || (p1.y < p2.y)
                && (Math.abs(p.x - getXAt(p1.y * z, p1.x * z, p2.y * z, p2.x * z, p.y)) <= 15)
            || (p1.y >= p2.y)
                && (Math.abs(p.x - getXAt(p2.y * z, p2.x * z, p1.y * z, p1.x * z, p.y)) <= 15);
    }

    protected Boolean isEntityAtPoint(Entity e, java.awt.Point p) {
        double z = _editor.canvas.getZoom();
        if (e instanceof petrinetz.control.entities.Place
                && Math.pow(e.x * z - p.getX(), 2) + Math.pow(e.y * z - p.getY(), 2) <= Math.pow((Canvas.PLACE_RADIUS + 1) * z, 2)) {
            return true;
        } else if (e instanceof Transition
                && (Math.abs(e.x * z - p.getX()) <= (Canvas.TRANSITION_WIDTH / 2 + 1) * z)
                && (Math.abs(e.y * z - p.getY()) <= (Canvas.TRANSITION_WIDTH / 2 + 1) * z)) {
            return true;
        }
        return false;
    }

    protected Boolean isEntityInRect(Entity e, java.awt.Point p, Dimension d) {
        double z = _editor.canvas.getZoom();
        return e.x * z >= p.getX() && e.x * z <= p.getX() + d.width && e.y * z >= p.getY() && e.y * z <= p.getY() + d.height;
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Event handlers">

    /**
     * Mouse wheel moved event handler.
     * @param e
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        JScrollBar sb = _editor.canvasContainer.getVerticalScrollBar();
        if (!e.isControlDown()) {
            int newY = sb.getValue() + e.getUnitsToScroll();
            newY = Math.min(newY, sb.getMaximum() - sb.getVisibleAmount());
            newY = Math.max(newY, 0);
            sb.setValue(newY);
        } else {
            JScrollBar sb2 = _editor.canvasContainer.getHorizontalScrollBar();
            double mouseX = (double) (e.getX() - sb2.getValue()) / (double) sb2.getVisibleAmount();
            double mouseY = (double) (e.getY() - sb.getValue()) / (double) sb.getVisibleAmount();
            double oldZoom = _editor.canvas.getZoom();
            double newZoom = _editor.canvas.getZoom() * (1.0 - (double) e.getScrollAmount() / 100.0 * Math.signum(e.getUnitsToScroll()));
            double x1 = sb2.getValue() / oldZoom * newZoom;
            double y1 = sb.getValue() / oldZoom * newZoom;
            double o1 = sb2.getVisibleAmount() * (1 - 1 / newZoom * oldZoom) * mouseX;
            double o2 = sb.getVisibleAmount() * (1 - 1 / newZoom * oldZoom) * mouseY;
            _editor.canvas.setZoom(_editor.canvas.getZoom() * (1.0 - (double) e.getScrollAmount() / 100.0 * Math.signum(e.getUnitsToScroll())));
            _editor.canvas.revalidate();
            _editor.canvas.scrollRectToVisible(new Rectangle((int) (x1 + o1), (int) (y1 + o2), sb2.getVisibleAmount(), sb.getVisibleAmount()));
        }
    }

    /**
     * Determines whether a drag and drop operation is just started or not. If it is, return true.
     * @param e
     * @return whether a new drag and drop session was just eing created or not.
     */
    protected Boolean handleDragAndDropStart(MouseEvent e) {

        if(e.isShiftDown() || e.isAltDown() || e.isControlDown())
            return false;

        PetriNet net = _editor.getDocument() != null && _editor.getDocument().net != null ? _editor.getDocument().net : null;

        if(net != null) {
            Entity selected = null;

            Iterator<petrinetz.control.entities.Place> placeIt = net.places.values().iterator();

            while (placeIt.hasNext()) {
                Place actPlace = placeIt.next();

                if(isEntityAtPoint(actPlace, e.getPoint()))
                    selected = actPlace;
            }

            Iterator<Transition> transIt = net.transitions.values().iterator();

            while (transIt.hasNext()) {
                Transition actTransition = transIt.next();

                if(isEntityAtPoint(actTransition, e.getPoint()))
                    selected = actTransition;
            }

            if(selected != null) {
                if(!selected.selected)
                    _editor.setSelection(selected);

                _editor.dragAndDropBegin = e.getPoint();
                _editor.dragAndDropDimension = new Dimension();

                _editor.canvas.calculateMinSize();
                _editor.canvas.repaint();

                return true;
            }
        }

        return false;
    }

    /**
     * Determines that a drag and drop operation is still being executed or not.
     * @param e
     * @return true if it is.
     */
    protected Boolean handleDragAndDrop(MouseEvent e) {

        if(_editor.isBeingDraggedAndDropped()) {

            _editor.dragAndDropDimension = new Dimension(
                    (int)((e.getPoint().x - _editor.dragAndDropBegin.x) / _editor.canvas.getZoom()),
                    (int)((e.getPoint().y - _editor.dragAndDropBegin.y) / _editor.canvas.getZoom()));
            _editor.canvas.calculateMinSize();
            _editor.canvas.repaint();

            return true;
        }

        return false;
    }

    /**
     * Handles the end of the drag and drop operation.
     * @param e
     */
    protected Boolean handleDragAndDropEnd(MouseEvent e) {

        if(_editor.isBeingDraggedAndDropped()) {
            Dimension dnd = _editor.dragAndDropDimension;

            _editor.dragAndDropBegin = null;
            _editor.dragAndDropDimension = null;

            Iterator<Entity> it = _editor.getSelection().iterator();

            while(it.hasNext()) {
                Entity ent = it.next();

                ent.x += dnd.width;
                ent.y += dnd.height;
            }

            _editor.canvas.calculateMinSize();
            _editor.canvas.repaint();

            return true;
        }

        return false;
    }

    /**
     * Mouse release handler.
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        _editor.canvas.requestFocus();
        _editor.canvas.repaint();
    }

    // </editor-fold>

}
