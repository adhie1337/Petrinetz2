package petrinetz.control.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import petrinetz.util.CompareUtil;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class PetriNet {

    // <editor-fold defaultstate="opened" desc="Properties">

    public String name;

    public HashMap<String, Place> places;
    public HashMap<String, Transition> transitions;

    public List<Edge> edges;

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Misc functions">

    public Place addPlace(Place value)
    {
        if(value.sign == null
                || value.sign.equals("")
                || places.containsKey(value.sign)
                    && !places.get(value.sign).equals(value))
        {
            Iterator<String> it = places.keySet().iterator();
            int i = 0;

            while(it.hasNext()) {
                String act = it.next();

                if(act.matches("^p[0-9]+$")) {
                    try {
                        i = Math.max(i, Integer.parseInt(act.substring(1)));
                    }
                    catch(NumberFormatException e)
                    {
                        // never happens
                        //throw new PNException("InvalidNameFormat", "Error");
                    }
                }
            }

            ++i;
            value.sign = "p" + i;
        }
        else if(places.containsKey(value.sign)
                    && places.get(value.sign).equals(value)) {
            return places.get(value.sign);
        }

        value._net = this;
        places.put(value.sign, value);
        return value;
    }

    public Transition addTransition(Transition value)
    {
        if(value.sign == null
                || value.sign.equals("")
                || transitions.containsKey(value.sign)
                    && !transitions.get(value.sign).equals(value))
        {
            Iterator<String> it = transitions.keySet().iterator();
            int i = 0;

            while(it.hasNext()) {
                String act = it.next();

                if(act.matches("^t[0-9]+$")) {
                    try {
                    i = Math.max(i, Integer.parseInt(act.substring(1)));
                    } catch(NumberFormatException e)
                    {
                        // Never happens
                        //throw new PNException("InvalidNameFormat", "Error");
                    }
                }
            }

            ++i;
            value.sign = "t" + i;
        }
        else if(transitions.containsKey(value.sign)
                    && transitions.get(value.sign).equals(value)) {
            return transitions.get(value.sign);
        }

        value._net = this;
        transitions.put(value.sign, value);
        return value;
    }

    public void addAll(PetriNet net) {
        HashMap<String, String> signMap = new HashMap<String, String>();

        Iterator<Place> placeIt = net.places.values().iterator();

        while(placeIt.hasNext()) {
            Place p = placeIt.next();
            signMap.put(p.sign, addPlace(p).sign);
        }

        Iterator<Transition> transIt = net.transitions.values().iterator();

        while(transIt.hasNext()) {
            Transition t = transIt.next();
            signMap.put(t.sign, addTransition(t).sign);
        }

        Iterator<Edge> edgeIt = net.edges.iterator();


        while(edgeIt.hasNext()) {
            Edge e = edgeIt.next();

            if(e.from instanceof Place)
            {
                if(signMap.containsKey(e.from.sign)) {
                    e.from = places.get(signMap.get(e.from.sign));
                }
                if(signMap.containsKey(e.to.sign)) {
                    e.to = transitions.get(signMap.get(e.to.sign));
                }
            }
            else
            {
                if(signMap.containsKey(e.from.sign)) {
                    e.from = transitions.get(signMap.get(e.from.sign));
                }
                if(signMap.containsKey(e.to.sign)) {
                    e.to = places.get(signMap.get(e.to.sign));
                }
            }

            edges.add(e);
        }
    }

    public PetriNet getSelection() {
        return getSelection(false);
    }

    public PetriNet getSelection(Boolean delete) {
        PetriNet retVal = new PetriNet();

        Iterator<Place> placeIt = places.values().iterator();

        while(placeIt.hasNext()) {
            Place next = placeIt.next();

            if(next.selected) {
                retVal.addPlace((Place)next.clone());
            }
        }

        Iterator<Transition> transIt = transitions.values().iterator();

        while(transIt.hasNext()) {
            Transition next = transIt.next();

            if(next.selected) {
                retVal.addTransition((Transition)next.clone());
            }
        }

        Iterator<Edge> edgeIt = edges.iterator();

        while(edgeIt.hasNext()) {
            Edge next = edgeIt.next();

            if(next.isSelected()) {
                Edge e = new Edge(retVal);

                if(next.from instanceof Transition)
                {
                    e.from = retVal.transitions.get(next.from.sign);
                    e.to = retVal.places.get(next.to.sign);
                }
                else if(next.from instanceof Place)
                {
                    e.from = retVal.places.get(next.from.sign);
                    e.to = retVal.transitions.get(next.to.sign);
                }

                if(e.from != null && e.to != null)
                    retVal.edges.add(e);
            }
        }

        if(delete) {
            for(int i = 0; i < edges.size(); ++i) {
                if(edges.get(i).isSelected()) {
                    edges.remove(i);
                    --i;
                }
            }

            transIt = retVal.transitions.values().iterator();

            while(transIt.hasNext()) {
                Transition next = transIt.next();
                transitions.remove(next.sign);
            }

            placeIt = retVal.places.values().iterator();

            while(placeIt.hasNext()) {
                Place next = placeIt.next();
                places.remove(next.sign);
            }
        }

        return retVal;
    }

    public void translate(double translateX, double translateY) {

        Iterator<Place> placeIt = places.values().iterator();

        while (placeIt.hasNext()) {
            Entity e = placeIt.next();
            e.x += translateX;
            e.y += translateY;
        }

        Iterator<Transition> transIt = transitions.values().iterator();

        while (transIt.hasNext()) {
            Entity e = transIt.next();
            e.x += translateX;
            e.y += translateY;
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructors">

    public PetriNet()
    {
        places = new HashMap<String, Place>();
        transitions = new HashMap<String, Transition>();
        edges = new ArrayList<Edge>();
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Overridden methods">

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof PetriNet)) {
            return false;
        }

        return CompareUtil.compare(name, ((PetriNet)other).name)
                && CompareUtil.compare(places, ((PetriNet)other).places)
                && CompareUtil.compare(transitions,((PetriNet)other).transitions)
                && CompareUtil.compare(edges, ((PetriNet)other).edges);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.places != null ? this.places.hashCode() : 0);
        hash = 67 * hash + (this.transitions != null ? this.transitions.hashCode() : 0);
        hash = 67 * hash + (this.edges != null ? this.edges.hashCode() : 0);
        return hash;
    }

    // </editor-fold>

}
