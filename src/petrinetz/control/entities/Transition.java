package petrinetz.control.entities;

import petrinetz.util.CompareUtil;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class Transition extends Entity{
    
    // <editor-fold defaultstate="opened" desc="Properties">
    
    public int from;
    public int to;
    
    public Boolean leftOpen;
    public Boolean rightOpen;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="opened" desc="Constructors">
    
    public Transition(PetriNet net) {
        this._net = net;
        
        leftOpen = false;
        rightOpen = true;
        
        from = 0;
        to = -1;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="opened" desc="Overridden methods">
    
    @Override
    public boolean equals(Object other) {
        Boolean retVal = (other instanceof Transition)
                && super.equals(other)
                && CompareUtil.compare(((Transition)other).from, from)
                && CompareUtil.compare(((Transition)other).to, to)
                && CompareUtil.compare(((Transition)other).leftOpen, leftOpen)
                && CompareUtil.compare(((Transition)other).rightOpen, rightOpen);
        
        if(retVal) {
            return true;
        }
        
        return retVal;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (new Integer(this.from).hashCode());
        hash = 73 * hash + (new Integer(this.to).hashCode());
        hash = 73 * hash + (this.leftOpen != null ? this.leftOpen.hashCode() : 0);
        hash = 73 * hash + (this.rightOpen != null ? this.rightOpen.hashCode() : 0);
        return hash;
    }
    
    @Override
    protected Object clone() {
        Transition retVal = new Transition(_net);
        
        retVal.label = label;
        retVal.selected = selected;
        retVal.sign = sign;
        retVal.x = x;
        retVal.y = y;
        retVal.from = from;
        retVal.to = to;
        
        retVal.leftOpen = rightOpen;
        retVal.rightOpen = rightOpen;
        
        return retVal;
    }
    
    // </editor-fold>

}
