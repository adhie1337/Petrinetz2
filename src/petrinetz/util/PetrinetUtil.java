package petrinetz.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.swing.text.NumberFormatter;
import petrinetz.Petrinetz;
import petrinetz.control.PNException;
import petrinetz.control.entities.Edge;
import petrinetz.control.entities.PetriNet;
import petrinetz.control.entities.Place;
import petrinetz.control.entities.Transition;
import petrinetz.view.editor.Canvas;

/**
 * A utility class for manipulating Petri nets, including conversion from and to String, and managing clipboard operations.
 * @author PIAPAAI.ELTE
 */
public class PetrinetUtil {

    // <editor-fold defaultstate="closed" desc="String to Petrinet">

    /**
     * Converts ndr file contents to Petri net objects.
     * @param value the file contents as String
     * @return a Petri net object.
     * @throws PNException the file format is incorrect or the file cannot be opened
     */
    public static PetriNet fromString(String value)
            throws PNException
    {
        PetriNet retVal = new PetriNet();

        BufferedReader stringReader = new BufferedReader(new StringReader(value));
        String line = null;
        Boolean error = false;
        Boolean end = false;

        do{
            try {
                line = stringReader.readLine();
                end = line == null || line.trim().equals("");

                if(!error && !end) {
                    retVal = parseOneLine(line, retVal);
                }
            }
            catch(IOException e) {
                error = true;
            }

        }while(!error && !end);

        if(error)
        {
            throw new PNException("IncorrectFileFormat", "Error");
        }

        return retVal;
    }

    private static PetriNet parseOneLine(String line, PetriNet net)
            throws IOException
    {
        Boolean endLine = false;
        String word = null;
        List<String> words = new ArrayList<String>();
        int chr = 0;

        BufferedReader lineReader = new BufferedReader(new StringReader(line));

        do {
            word = "";
            do {
                chr = lineReader.read();

                if(chr == -1) {
                    endLine = true;
                }
                else if(chr != (int)(' ')) {
                    word += (char)chr;
                }
            }while(chr != (int)(' ') && !endLine);

            words.add(word);
        }while(!endLine);

        if(words.get(0).equals("p")) {
            Place p = parseOnePlace(words, net);
            net.places.put(p.sign, p);
        }
        else if(words.get(0).equals("t")) {
            Transition t = parseOneTransition(words, net);
            net.transitions.put(t.sign, t);
        }
        else if(words.get(0).equals("e")) {
            net.edges.add(parseOneEdge(words, net));
        }
        else if(words.get(0).equals("h")) {
            net.name = words.get(1);
        }

        return net;
    }

    private static Place parseOnePlace(List<String> line, PetriNet net) {
        Place retVal = null;
        Integer i = 0;

        if(!line.get(i).equals("p")) {
            // Error?
        }

        ++i;

        retVal = new Place(net);
        retVal.x = Double.parseDouble(line.get(i));
        ++i;
        retVal.y = Double.parseDouble(line.get(i));
        ++i;
        retVal.sign = line.get(i);
        ++i;
        retVal.weight = Integer.parseInt(line.get(i));
        ++i;

        if(line.get(i).equals("n")) {
            if(line.get(line.size() - 1).equals("ne")) {
                ++i;
                String label = "";
                String word = "";
                do {
                    word = line.get(i);
                    ++i;

                    if(word.charAt(0) == '{') {
                        word = word.substring(1);
                    }

                    if(word.charAt(word.length() - 1) == '}') {
                        word = word.substring(0, word.length() - 1);
                    }

                    word = word.replace("\\{", "{");
                    word = word.replace("\\}", "}");

                    if(i < line.size()) {
                        label += " " + word;
                    }
                }while(i != line.size());

                retVal.label = label;
            }
        }
        else {
            // Error?
        }

        return retVal;
    }

    private static Transition parseOneTransition(List<String> line, PetriNet net) {
        Transition retVal = null;
        Integer i = 0;
        String label = "";

        if(!line.get(i).equals("t")) {
            // Error?
        }

        ++i;

        retVal = new Transition(net);
        retVal.x = Double.parseDouble(line.get(i));
        ++i;
        retVal.y = Double.parseDouble(line.get(i));
        ++i;
        retVal.sign = line.get(i);
        ++i;

        if(line.get(i).equals("w")) {
            retVal.from = -1;
        }
        else {
            retVal.from = Integer.parseInt(line.get(i));

            if(retVal.from < 0) {
                retVal.from = -retVal.from - 1;
                retVal.leftOpen = true;
            }
        }

        ++i;

        if(line.get(i).equals("w")) {
            retVal.to = -1;
        }
        else {
            retVal.to = Integer.parseInt(line.get(i));

            if(retVal.to < 0) {
                retVal.to = -retVal.to - 1;
                retVal.rightOpen = true;
            }
        }

        ++i;

        if(line.get(i).equals("n")) {
        }
        else
            for(int j = i; j < line.size() - 1; ++j)
            {
                label = label + " " + line.get(j).replace("\\{", "{").replace("\\}", "}");
            }

        retVal.label = PetrinetUtil.trimString(label).replace("\\{", "{").replace("\\}", "}");
        return retVal;
    }

    private static String trimString(String value) {
        int begin = 0;
        int end = value.length();

        for(int i = 0; i < value.length(); ++i) {
            String at = value.substring(i, i+1);

            if((begin >= i - 1) && (at.equals(" ") || at.equals("{")))
            {
                ++begin;
            }
        }

        for(int i = value.length() - 1; i > 0; --i) {
            String at = value.substring(i, i+1);

            if((begin >= i - 1) && (at.equals(" ") || at.equals("}")))
            {
                --end;
            }
        }

        return value.substring(begin, end);
    }

    private static Edge parseOneEdge(List<String> line, PetriNet net) {
        Edge retVal = null;
        Integer i = 0;

        if(!line.get(i).equals("e")) {
            // Error?
        }

        ++i;
        retVal = new Edge(net);
        retVal.from = net.places.containsKey(line.get(i)) ? net.places.get(line.get(i)) : net.transitions.get(line.get(i));
        ++i;

        if(line.size() == 9) {
            retVal.fromAngle = Double.parseDouble(line.get(i));
            ++i;
            retVal.fromWeight = Double.parseDouble(line.get(i));
            ++i;
        }

        retVal.to = net.places.containsKey(line.get(i)) ? net.places.get(line.get(i)) : net.transitions.get(line.get(i));
        ++i;

        if(line.size() == 9) {
            retVal.toAngle = Double.parseDouble(line.get(i));
            ++i;
            retVal.toWeight = Double.parseDouble(line.get(i));
            ++i;
        }

        retVal.weight = Integer.parseInt(line.get(i));
        ++i;

        if(line.get(i).equals("n")) {
        }
        else {
            // Error?
        }

        return retVal;
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Petrinet to String">

    /**
     * A conversion function that convert a Petri net object to an ndr file contetn string.
     * @param value the Petri net object
     * @return the net formatted as string
     */
    public static String fromPetriNet(PetriNet value) {
        String retVal = "";

        Iterator<String> it = value.places.keySet().iterator();

        while(it.hasNext())
        {
            retVal += toString(value.places.get(it.next())) + "\n";
        }

        it = value.transitions.keySet().iterator();

        while(it.hasNext())
        {
            retVal += toString(value.transitions.get(it.next())) + "\n";
        }

        Iterator<Edge> ite = value.edges.iterator();

        while(ite.hasNext())
        {
            retVal += toString(ite.next()) + "\n";
        }

        retVal += "h " + value.name;

        return retVal;
    }

    private static String toString(Place p) {
        String lbl = (p.label == null || p.label.trim().equals("") ? "" : p.label.trim().replace("{", "\\{").replace("}", "\\}"));

        if(lbl.indexOf(" ") != -1)
        {
            lbl = "{" + lbl + "}";
        }

        if(!lbl.equals(""))
        {
            lbl = " " + lbl + " ne";
        }

        return "p " + f(p.x) + " " + f(p.y) + " " + p.sign + " " + p.weight + " n" + lbl;
    }

    private static String toString(Transition t) {
        String from = new Integer(t.from).equals(-1) ? "w" : ("" + ((t.from + (t.leftOpen ? 1 : 0)) * (t.leftOpen ? -1 : 1)));

        String to = new Integer(t.to).equals(-1) ? "w" : ("" + ((t.to + (t.rightOpen ? 1 : 0)) * (t.rightOpen ? -1 : 1)));

        String lbl = (t.label == null || t.label.trim().equals("") ? "" : t.label.trim().replace("{", "\\{").replace("}", "\\}"));

        if(lbl.indexOf(" ") != -1 || lbl.indexOf("}") != -1 || lbl.indexOf("{") != -1)
            lbl = "{" + lbl + "}";

        if(!lbl.equals(""))
            lbl = " " + lbl + " ne";
        else
            lbl = "n";

        return "t " + f(t.x) + " " + f(t.y) + " " + t.sign + " " + from + " " + to + " " + lbl;
    }

    private static String toString(Edge e) {
        String fromProps = "";
        String toProps = "";

        if(!new Double(e.fromAngle).equals(-1.0)) {
            fromProps = " " + f(e.fromAngle) + " " + e.fromWeight;
        }
        if(!new Double(e.toAngle).equals(-1.0)) {
            toProps = " " + f(e.toAngle) + " " + e.toWeight;
        }

        return "e " + e.from.sign + fromProps + " " + e.to.sign + toProps + " " + e.weight + " n";
    }

    private static String f(double value) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(8);
        format.setMinimumFractionDigits(1);
        format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));

        NumberFormatter formatter = new NumberFormatter(format);
        
        try
        {
            return formatter.valueToString(value).replace(",", "");
        }
        catch(ParseException e)
        {
            return "";
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Clipboard operations">

    /**
     * Gets the copied Petri net from the clipboard.
     * @return the Petri net instance
     */
    public static PetriNet fromClipboard() {
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipBoardContents = c.getContents(Petrinetz.getInstance());
        PetriNet retVal = null;

        try {
            retVal = fromString(clipBoardContents.getTransferData(DataFlavor.stringFlavor).toString());
            toClipBoard(fromString(clipBoardContents.getTransferData(DataFlavor.stringFlavor).toString()));
        }
        catch(Exception e) {
            return null;
        }

        return retVal;
    }

    /**
     * Copies the given Petri net to the clipboard.
     * @param net the net to copy.
     */
    public static void toClipBoard(PetriNet net) {
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();

        net.translate(Canvas.TRANSITION_WIDTH, Canvas.TRANSITION_WIDTH);
        net.name = "Clipboard contents";

        c.setContents(new StringSelection(fromPetriNet(net)), (Petrinetz)Petrinetz.getInstance());
    }

    // </editor-fold>

}
