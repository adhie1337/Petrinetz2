package petrinetz.control.entities;

import petrinetz.util.CompareUtil;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class Entity {

    // <editor-fold defaultstate="opened" desc="Properties">

    protected PetriNet _net;

    public double x;
    public double y;

    public String sign;
    public String label;

    public Boolean selected;

    public static int nextPlaceId = 1;
    public static int nextTransitionId = 1;

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructor">

    public Entity() {
        selected = false;

        if(getClass().equals(Place.class))
        {
            sign = "p" + nextPlaceId;
            ++nextPlaceId;
        }
        else if(getClass().equals(Transition.class))
        {
            sign = "t" + nextTransitionId;
            ++nextTransitionId;
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Overridden methods">

    @Override
    public boolean equals(Object other) {
        return (other instanceof Entity)
                && CompareUtil.compare(((Entity)other).x, x)
                && CompareUtil.compare(((Entity)other).y, y)
                && CompareUtil.compare(((Entity)other).sign, sign)
                && CompareUtil.compare(((Entity)other).label, label);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (new Double(this.x).hashCode());
        hash = 47 * hash + (new Double(this.y).hashCode());
        hash = 47 * hash + (this.sign != null ? this.sign.hashCode() : 0);
        hash = 47 * hash + (this.label != null ? this.label.hashCode() : 0);
        return hash;
    }

    // </editor-fold>

}
