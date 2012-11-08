package petrinetz.control;

import java.io.BufferedReader;
import petrinetz.Petrinetz;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The class that handles file manipulation commands, like read or write.
 * @author PIAPAAI.ELTE
 */
class FileController {

    // <editor-fold defaultstate="opened" desc="Properties">
    
    private JFileChooser _fileChooser;

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Methods">

    /**
     * Gets a file chooser window to choose a file.
     * @return reference to the (only) file chooser instance
     */
    protected JFileChooser getFileChooser()
    {
        if (_fileChooser == null) {
            _fileChooser = new JFileChooser();
        }

        return _fileChooser;
    }

    /**
     * Pops up a file chooser for the user to select a file to read or write. When the user selected a file, it gives back the path to the file. If the user cancelled the peration, gives an empty string.
     * @param open whether the application shouls show the "Select file for open" dialog or not.
     * @return the path to the selected file
     * @throws PNException when an error occured while selecting the file (like file not readable)
     */
    public String selectFile(Boolean open)
            throws PNException
    {
        JFileChooser chooser = this.getFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("NDR files", "ndr"));

        int result = 0;

        if(open)
        {
            result = chooser.showOpenDialog(Petrinetz.getApplication().getMainFrame());
        }
        else
        {
            result = chooser.showSaveDialog(Petrinetz.getApplication().getMainFrame());
        }

        if(result == JFileChooser.APPROVE_OPTION) {
            String retVal = chooser.getSelectedFile().getAbsolutePath().trim();

            if(!retVal.toLowerCase().endsWith(".ndr")) {
                retVal += ".ndr";
            }

            return retVal;
        }
        else if(result == JFileChooser.ERROR_OPTION) {
            throw new PNException("FileNotReadable", "Error");
        }

        return "";
    }

    /**
     * The method that actually handles the file reading.
     * @param path the path to the file to read.
     * @return the contents of the file
     * @throws PNException when the file doesn't exist or does, but is not readable.
     */
    public String readFile(String path)
            throws PNException
    {
        BufferedReader reader = null;
        String contents = null;

        try
        {
            reader = new BufferedReader(new FileReader(new File(path)));

            StringBuilder contentBuffer = new StringBuilder();
            String line = reader.readLine();

            while(line != null)
            {
                contentBuffer.append(line);
                contentBuffer.append("\n");
                line = reader.readLine();
            }

            contents = contentBuffer.toString();
        }
        catch(FileNotFoundException e)
        {
            throw new PNException("FileNotFound", "Error");
        }
        catch(IOException e)
        {
            throw new PNException("FileNotReadable", "Error");
        }
        finally
        {
            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch(IOException e)
                {
                    throw new PNException("FileNotReadable", "Error");
                }
            }

            return contents;
        }

    }

    /**
     * The method that handles file writing.
     * @param path the path to the file to write the contetns to
     * @param content the contents to write to the file
     * @throws PNException when file is not writable
     */
    public void writeFile(String path, String content)
            throws PNException
    {
        PrintWriter pw;

        try
        {
            pw = new PrintWriter(new FileWriter(path));
            pw.append(content);
            pw.close();
        }
        catch(IOException e)
        {
            throw new PNException("FileNotWritable", "Error");
        }

        pw.close();
    }

    // </editor-fold>

}
