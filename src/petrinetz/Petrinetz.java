package petrinetz;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import petrinetz.control.DocumentController;
import petrinetz.view.MainFrame;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class Petrinetz extends SingleFrameApplication implements ClipboardOwner {

    /**
     * The entry point of the application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(Petrinetz.class, args);
    }

    /**
     * The function called to initialize view.
     */
    @Override
    protected void startup() {
        show(new MainFrame(this));

        DocumentController.getInstance().newFileAction();
    }

    /**
     * The application instance getter.
     */
    public static Petrinetz getApplication() {
        return Application.getInstance(Petrinetz.class);
    }

    /**
     * lostOwnership listener
     * @param clipboard
     * @param contents
     */
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
