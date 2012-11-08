package petrinetz.view;

import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import petrinetz.Petrinetz;

/**
 * The error dialog class.
 * @author PIAPAAI.ELTE
 */
public class ErrorDialog extends JDialog {

    // <editor-fold defaultstate="opened" desc="Properties">

    private JButton errorCloseButton;
    private JLabel errorMessageLabel;

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructor">

    /**
     * Constructor.
     * @param owner the owner of the dialog.
     */
    public ErrorDialog(JFrame owner) {
        super(owner);

        initializeView();
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Internal functions">

    private void initializeView() {
        ApplicationContext c = Application.getInstance(Petrinetz.class).getContext();
        ActionMap actionMap = c.getActionMap(ErrorDialog.class, this);
        ResourceMap map = c.getResourceMap(ErrorDialog.class);

        setModal(true);
        setResizable(false);

        errorMessageLabel = new JLabel();
        add(errorMessageLabel);

        errorCloseButton = new JButton();
        errorCloseButton.setAction(actionMap.get("closeErrorDialogAction"));
        errorCloseButton.setText(map.getString("ErrorDialog.JButton.Close.Text"));
        add(errorCloseButton);

        GroupLayout layout = new GroupLayout(getContentPane());
        setLayout(layout);
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGap(5)
            .addComponent(errorMessageLabel)
            .addComponent(errorCloseButton)
            .addGap(5)
        );
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGap(5)
            .addGroup(layout.createParallelGroup()
                .addComponent(errorMessageLabel, GroupLayout.Alignment.LEADING)
                .addComponent(errorCloseButton, GroupLayout.Alignment.TRAILING)
            )
            .addGap(5)
        );

        getRootPane().setDefaultButton(errorCloseButton);
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Misc functions">

    /**
     * Show the dialog with the given parameters.
     * @param message
     * @param title can be "Error" or "Warning", or anything else
     */
    public void showError(String message, String title) {
        ApplicationContext c = Application.getInstance(Petrinetz.class).getContext();
        ResourceMap map = c.getResourceMap(ErrorDialog.class);

        setTitle(map.getString("ErrorDialog.JDialog." + title + ".Title"));
        errorMessageLabel.setText(map.getString("ErrorDialog.JLabel.Message." + message + ".Text"));
        petrinetz.Petrinetz.getApplication().show(this);
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Actions">

    @Action
    public void closeErrorDialogAction() {
        dispose();
    }

    // </editor-fold>

}
