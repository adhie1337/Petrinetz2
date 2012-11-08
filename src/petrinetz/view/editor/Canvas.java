package petrinetz.view.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.Iterator;
import petrinetz.control.entities.Edge;
import petrinetz.control.entities.PetriNet;
import petrinetz.control.entities.Place;
import petrinetz.control.entities.Transition;
import javax.swing.JPanel;
import petrinetz.control.entities.Entity;

/**
 * The canvas class. Handles the drawing of the Petri net being currently edited.
 * @author PIAPAAI.ELTE
 */
public class Canvas extends JPanel {

    // <editor-fold defaultstate="opened" desc="Properties">
    private DocumentEditor _editor;
    private PetriNet _net;

    /**
     * Petri net setter.
     * @param value
     */
    public void setNet(PetriNet value) {
        _net = value;

        calculateMinSize();

        repaint();
    }

    /**
     * Petri net getter.
     */
    public PetriNet getNet() {
        return _net;
    }

    private Double _zoom = 1.0;

    /**
     * Zoom setter
     * @param z
     */
    public void setZoom(double z) {
        _zoom = Math.max(z, 0.01);
        calculateMinSize();
        repaint();
    }

    /**
     * Zoom getter
     */
    public double getZoom() {
        return _zoom;
    }

    public static final Integer PLACE_RADIUS = 20;
    public static final Integer TRANSITION_WIDTH = 32;
    private final int ARR_SIZE = 8;

    public Place placeToAdd = null;
    public Transition transitionToAdd = null;

    public Entity edgeToAddStart;
    public Entity edgeToAddFinish;
    public java.awt.Point edgeToAddEnd;

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructor">

    /**
     * Constructor.
     * @param editor the editor to draw the Petri net by.
     */
    public Canvas(DocumentEditor editor) {
        _editor = editor;

        /*
        JPopupMenu menu = new JPopupMenu();
        menu.add(new JMenuItem("lol"));

        setComponentPopupMenu(menu);*/
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Overridden methods">
    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());

        if (_net == null) {
            return;
        }

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D) g).setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

        Iterator<Place> placeIt = _net.places.values().iterator();

        while (placeIt.hasNext()) {
            drawPlace(placeIt.next(), g.create());
        }

        Iterator<Transition> transIt = _net.transitions.values().iterator();

        while (transIt.hasNext()) {
            drawTransition(transIt.next(), g.create());
        }

        Iterator<Edge> edgeIt = _net.edges.iterator();

        ((Graphics2D) g).setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

        while (edgeIt.hasNext()) {
            drawEdge(edgeIt.next(), g.create());
        }

        ((Graphics2D) g).setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

        if(_editor.isBeingSelected() && _editor.getEditorMode() == DocumentEditor.EditorMode.Selection) {
            java.awt.Point a = new java.awt.Point(
                    Math.min( _editor.selectionBegin.x, _editor.selectionEnd.x),
                    Math.min( _editor.selectionBegin.y, _editor.selectionEnd.y));
            Dimension b = new Dimension(
                    Math.max(_editor.selectionBegin.x, _editor.selectionEnd.x) - a.x,
                    Math.max(_editor.selectionBegin.y, _editor.selectionEnd.y) - a.y);
            g.setColor(new Color((float)0.0, (float)0.0, (float)1.0, (float)0.1));
            g.fillRect(a.x,
                    a.y,
                    b.width,
                    b.height);
            g.setColor(new Color((float)0.0, (float)0.0, (float)1.0, (float)0.9));
            g.drawRect(a.x,
                    a.y,
                    b.width,
                    b.height);
        }
        else if(_editor.getEditorMode() == DocumentEditor.EditorMode.AddPlace
                && placeToAdd != null) {
            g.setColor(new Color((float)0.0, (float)0.0, (float)1.0, (float)0.1));

            drawPlace(placeToAdd, g.create());
        }
        else if(_editor.getEditorMode() == DocumentEditor.EditorMode.AddTransition
                && transitionToAdd != null) {
            g.setColor(new Color((float)0.0, (float)0.0, (float)1.0, (float)0.1));

            drawTransition(transitionToAdd, g.create());
        }
        else if(_editor.getEditorMode() == DocumentEditor.EditorMode.AddEdge
                && edgeToAddStart != null) {
            Color c = new Color((float)1.0, (float)0.0, (float)0.0, (float)1.0);

            if(edgeToAddFinish != null)
            {
                if(!edgeToAddFinish.getClass().equals(edgeToAddStart.getClass()))
                {
                    c = new Color((float)0.0, (float)0.0, (float)1.0, (float)1.0);
                }

                Edge edgeToDraw = new Edge(_net);
                edgeToDraw.from = edgeToAddStart;
                edgeToDraw.to = edgeToAddFinish;

                drawEdge(edgeToDraw, g, c);
            }
            else if(edgeToAddEnd != null)
            {
                java.awt.Point start = null;

                if(edgeToAddStart instanceof Place)
                {
                    start = getCorrectedPointFromPlace(edgeToAddStart.x, edgeToAddStart.y, edgeToAddEnd.x, edgeToAddEnd.y, PLACE_RADIUS - 1);
                }
                else
                {
                    start = getCorrectedPointFromTransition(edgeToAddStart.x, edgeToAddStart.y, edgeToAddEnd.x, edgeToAddEnd.y, PLACE_RADIUS - 1);
                }

                g.setColor(c);
                drawArrow((Graphics2D)g, start.x, start.y, edgeToAddEnd.x, edgeToAddEnd.y);
            }
        }
    }

    private void drawPlace(Place p, Graphics g) {
        java.awt.Point pos = getCorrectedPosition(p);
        g.setColor(getColorForEntity(p, false));
        g.fillOval((int) ((pos.x - PLACE_RADIUS) * _zoom),
                (int) ((pos.y - PLACE_RADIUS) * _zoom),
                (int) (2 * PLACE_RADIUS * _zoom),
                (int) (2 * PLACE_RADIUS * _zoom));
        g.setColor(getColorForEntity(p, true));
        g.drawOval((int) ((pos.x - PLACE_RADIUS) * _zoom),
                (int) ((pos.y - PLACE_RADIUS) * _zoom),
                (int) (2 * PLACE_RADIUS * _zoom),
                (int) (2 * PLACE_RADIUS * _zoom));

        if(p.label != null && !p.label.trim().equals("")) {
            Graphics g2 = g.create();
            g2.setFont(g2.getFont().deriveFont((float)(PLACE_RADIUS * 2 / 3 * _zoom)));

            FontMetrics m = g2.getFontMetrics();

            g2.drawString(p.label,
                    (int)(pos.x * _zoom - m.stringWidth(p.label) / 2),
                    (int)((pos.y - PLACE_RADIUS) * _zoom - g2.getFont().getSize2D() / 2));
        }

        if(p.weight > 0) {
            String lbl = NumberFormat.getIntegerInstance().format(p.weight);
            Graphics g2 = g.create();
            g2.setFont(g2.getFont().deriveFont((float)(PLACE_RADIUS * 2 / 3 * _zoom)));

            FontMetrics m = g2.getFontMetrics();

            g2.drawString(lbl,
                    (int)(pos.x * _zoom - m.stringWidth(lbl) / 2),
                    (int)(pos.y * _zoom + g2.getFont().getSize2D() / 2));
        }
    }

    private void drawTransition(Transition t, Graphics g) {
        java.awt.Point pos = getCorrectedPosition(t);

        g.setColor(getColorForEntity(t, false));
        g.fillRect((int) ((pos.x - TRANSITION_WIDTH / 2) * _zoom),
                (int) ((pos.y - TRANSITION_WIDTH / 2) * _zoom),
                (int) (TRANSITION_WIDTH * _zoom),
                (int) (TRANSITION_WIDTH * _zoom));
        g.setColor(getColorForEntity(t, true));
        g.drawRect((int) ((pos.x - TRANSITION_WIDTH / 2) * _zoom),
                (int) ((pos.y - TRANSITION_WIDTH / 2) * _zoom),
                (int) (TRANSITION_WIDTH * _zoom),
                (int) (TRANSITION_WIDTH * _zoom));

        if(t.label != null && !t.label.trim().equals("")) {
            Graphics g2 = g.create();
            g2.setFont(g2.getFont().deriveFont((float)(TRANSITION_WIDTH / 3 * _zoom)));

            FontMetrics m = g2.getFontMetrics();
            int i = 0;

            while(i < t.label.length()
                    && m.stringWidth(t.label.substring(0, t.label.length() - i)) >= TRANSITION_WIDTH * _zoom - 4)
                ++i;

            g2.drawString(t.label.substring(0, t.label.length() - i),
                    (int)(pos.x * _zoom - m.stringWidth(t.label.substring(0, t.label.length() - i)) / 2),
                    (int)(pos.y * _zoom + g2.getFont().getSize2D() / 2));
        }
    }

    private void drawEdge(Edge e, Graphics g)
    {
        drawEdge(e, g, null);
    }

    private void drawEdge(Edge e, Graphics g, Color c) {

        if(!shouldBeDrawn(e)) return;

        if(c == null)
        {
            g.setColor(getColorForEntity(e, false));
        }
        else
        {
            g.setColor(c);
        }

        java.awt.Point fromPos = getCorrectedPosition(e.from);
        java.awt.Point toPos = getCorrectedPosition(e.to);
        int fromX = 0;
        int fromY = 0;
        int toX = 0;
        int toY = 0;

        if (e.from instanceof Place) {
            java.awt.Point fromPoint = getCorrectedPointFromPlace(fromPos.x, fromPos.y, toPos.x, toPos.y, PLACE_RADIUS - 1);
            fromX = fromPoint.x;
            fromY = fromPoint.y;
        } else {
            java.awt.Point fromPoint = getCorrectedPointFromTransition(fromPos.x, fromPos.y, toPos.x, toPos.y, TRANSITION_WIDTH / 2 - 1);
            fromX = fromPoint.x;
            fromY = fromPoint.y;
        }

        if (e.to instanceof Place) {
            java.awt.Point toPoint = getCorrectedPointFromPlace(toPos.x, toPos.y, fromPos.x, fromPos.y, PLACE_RADIUS + 1);
            toX = toPoint.x;
            toY = toPoint.y;
        } else {
            java.awt.Point toPoint = getCorrectedPointFromTransition(toPos.x, toPos.y, fromPos.x, fromPos.y, TRANSITION_WIDTH / 2 + 1);
            toX = toPoint.x;
            toY = toPoint.y;
        }

        drawArrow((Graphics2D)g.create(),
                fromX,
                fromY,
                toX,
                toY);

        if(e.weight > 1) {
            Graphics g2 = g.create();
            g2.setFont(g2.getFont().deriveFont((float)(TRANSITION_WIDTH / 2 * _zoom)));
            String weightString = NumberFormat.getInstance().format(e.weight);

            float ratio = (float)Math.abs(fromX - toX) / ((float)Math.abs(fromY - toY) + (float)0.1);
            float flip = fromX < toX && fromY < toY || fromX > toX && fromY > toY ? (float)-1.0 : (float)1.0;

            g2.drawString(weightString,
                    (int)((fromX + toX) / 2 - TRANSITION_WIDTH / 4 * _zoom + (1 / ratio > 1.5 ? 1 : 1 / ratio) * TRANSITION_WIDTH / 3 * _zoom),
                    (int)((fromY + toY) / 2 + flip * TRANSITION_WIDTH / 4 * _zoom + (ratio > 1.5 ? 1 : ratio) * TRANSITION_WIDTH / 3 * _zoom));
        }
    }

    void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        g.translate(x1, y1);
        g.rotate(angle);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, (int) len, 0);
        g.fillPolygon(new int[] {len, (int)(len - ARR_SIZE * _zoom), (int)(len - ARR_SIZE * _zoom), len},
                      new int[] {0, (int)(-ARR_SIZE * _zoom), (int)(ARR_SIZE * _zoom), 0}, 4);
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Misc functions">

    private java.awt.Point getCorrectedPointFromPlace(double a, double b, double c, double d, double radius) {
        int fromX;
        int fromY;

        double sgn = 1.0;

        if(Math.abs(b - d) > 0.5) {
            sgn = Math.signum(c - a);
            fromX = (int) (_zoom * (a + radius / Math.sqrt(1 + Math.pow(b - d, 2) / Math.pow(a - c, 2)) * sgn));
        }else {
            fromX = (int) (_zoom * (a + Math.signum(c - a) * radius));
        }
        if(Math.abs(a - c) > 0.5) {
            sgn = Math.signum(d - b);
            fromY = (int) (_zoom * (b + radius / Math.sqrt(1 + Math.pow(a - c, 2) / Math.pow(b - d, 2)) * sgn));
        }else {
            fromY = (int) (_zoom * (b - Math.signum(b - d) * radius));
        }

        return new java.awt.Point(fromX, fromY);
    }

    private java.awt.Point getCorrectedPointFromTransition(double a, double b, double c, double d, double radius) {
        int fromX;
        int fromY;

        double sgn = 1.0;

        if(Math.abs(b - d) <= Math.abs(c - a))
        {
            fromX = (int) (_zoom * (a + radius * Math.signum(c - a)));
            fromY = (int) (_zoom * (b - radius * (b - d) / Math.abs(c - a)));
        }
        else
        {
            fromX = (int) (_zoom * (a + radius * (c - a) / Math.abs(b - d)));
            fromY = (int) (_zoom * (b - radius * Math.signum(b - d)));
        }

        return new java.awt.Point(fromX, fromY);
    }

    /**
     * Recalculates the minimum size required by the places and transitions of the Petri net and the zoom value.
     * Also sets the minimum size so the scroll bars can be set.
     */
    public void calculateMinSize() {
        Dimension minSize = new Dimension();

        Iterator<Place> placeIt = _net.places.values().iterator();
        Iterator<Transition> transIt = _net.transitions.values().iterator();

        double translateX = 0.0;
        double translateY = 0.0;

        while (placeIt.hasNext()) {
            Entity e = placeIt.next();

            if(e.x - PLACE_RADIUS - 5 < translateX)
            {
                translateX = e.x - PLACE_RADIUS - 5;
            }
            if(e.y - PLACE_RADIUS - 5 < translateY)
            {
                translateY = e.y - PLACE_RADIUS - 5;
            }

            minSize.width = (int)Math.max(minSize.width, (e.x + PLACE_RADIUS + 5) * _zoom);
            minSize.height = (int)Math.max(minSize.height, (e.y + PLACE_RADIUS + 5) * _zoom);
        }

        while (transIt.hasNext()) {
            Entity e = transIt.next();

            if(e.x - TRANSITION_WIDTH / 2 - 5 < translateX)
            {
                translateX = e.x - TRANSITION_WIDTH / 2 - 5;
            }
            if(e.y - TRANSITION_WIDTH / 2 - 5 < translateY)
            {
                translateY = e.y - TRANSITION_WIDTH / 2 - 5;
            }

            minSize.width = (int)Math.max(minSize.width, (e.x + TRANSITION_WIDTH / 2 + 5) * _zoom);
            minSize.height = (int)Math.max(minSize.height, (e.y + TRANSITION_WIDTH / 2 + 5) * _zoom);
        }

        if(translateX < 0.0)
        {
            translateX = -translateX;
        }
        if(translateY < 0.0)
        {
            translateY = -translateY;
        }

        _net.translate(translateX, translateY);

        minSize.width  += translateX;
        minSize.height += translateY;

        setPreferredSize(minSize);
        getParent().revalidate();
    }

    private java.awt.Point getCorrectedPosition(Entity e) {
        java.awt.Point p = new java.awt.Point((int)e.x, (int)e.y);

        if(e.selected && _editor.isBeingDraggedAndDropped()) {
            p.x += _editor.dragAndDropDimension.width;
            p.y += _editor.dragAndDropDimension.height;
        }

        return p;
    }

    /**
     * Gets the color of the entity given as the first parameter.
     * @param value The entity value.
     * @param lineColor A boolean that means that we are asking the color to fill the entity with, or not.
     * @return The color.
     */
    public Color getColorForEntity(Object value, Boolean lineColor) {

        if(value instanceof Edge) {
            if(_editor.getEditorMode() == DocumentEditor.EditorMode.Simulation)
            {
                return Color.BLACK;
            }
            else return ((Edge)value).isSelected() ? Color.BLUE : Color.BLACK;
        }
        else if(value instanceof Place) {
            if(!lineColor)
                return Color.WHITE;

            if(_editor.getEditorMode() == DocumentEditor.EditorMode.Simulation)
            {
                return Color.BLACK;
            }
            else return ((Place)value).selected ? Color.BLUE : Color.BLACK;
        }
        else if(value instanceof Transition) {

            if(!lineColor)
            {
                if(_editor.getEditorMode() == DocumentEditor.EditorMode.Simulation)
                {
                    Iterator<Edge> it = _editor.getDocument().net.edges.iterator();
                    boolean allowed = true;

                    while(it.hasNext() && allowed) {
                        Edge edge = it.next();
                        if(edge.to.equals(value) && ((Place)edge.from).weight < edge.weight)
                            allowed = false;
                    }

                    return allowed ? Color.RED : Color.WHITE;
                }

                return Color.WHITE;
            }
            else
            {
                if(_editor.getEditorMode() == DocumentEditor.EditorMode.Simulation)
                {
                    return Color.BLACK;
                }
                else
                {
                    return ((Transition)value).selected ? Color.BLUE : Color.BLACK;
                }
            }
        }

        return Color.WHITE;
    }

    private boolean shouldBeDrawn(Edge e) {
        double z = getZoom();
        java.awt.Point from = getCorrectedPosition(e.from);
        java.awt.Point to = getCorrectedPosition(e.to);
        if(Math.sqrt(Math.pow(from.x * z - to.x * z, 2) + Math.pow(from.y * z - to.y * z, 2)) < Math.max(TRANSITION_WIDTH, PLACE_RADIUS * 2) * z)
            return false;

        return true;
    }

    // </editor-fold>
}
