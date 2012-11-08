package petrinetz.control.entities;

import petrinetz.util.CompareUtil;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class Place extends Entity implements Cloneable {

    // <editor-fold defaultstate="opened" desc="Properties">

    public int weight;

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructors">

    public Place(PetriNet net) {
        this._net = net;
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Overridden methods">

    @Override
    public boolean equals(Object other) {
        return (other instanceof Place)
                && super.equals(other)
                && CompareUtil.compare(((Place)other).weight, weight);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (new Integer(this.weight).hashCode());
        return hash;
    }

    @Override
    protected Object clone()
    {
        Place retVal = new Place(_net);

        retVal.label = label;
        retVal.selected = selected;
        retVal.sign = sign;
        retVal.x = x;
        retVal.y = y;
        retVal.weight = weight;

        return retVal;
    }

    // </editor-fold>

}
