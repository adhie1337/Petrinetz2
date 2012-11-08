package petrinetz.view.editor;

import java.text.NumberFormat;
import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import petrinetz.Petrinetz;
import petrinetz.control.ErrorController;
import petrinetz.control.entities.Edge;

/**
 * The edge properties setter dialog. Very simple dialog, with 1 text field.
 * @author PIAPAAI.ELTE
 */
public class EdgePropertiesDialog extends JDialog{

    // <editor-fold defaultstate="opened" desc="Properties">

    private JButton okButton;
    private JButton cancelButton;

    private JLabel weightLabel;
    private JTextField weightField;

    private Edge _edge;

    public Edge getEdge() {
        return _edge;
    }

    public void setEdge(Edge e) {
        _edge = e;

        if(_edge != null) {
            weightField.setText(NumberFormat.getIntegerInstance().format(_edge.weight));
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructor">

    public EdgePropertiesDialog(java.awt.Frame owner) {
        super(owner);

        initializeView();
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Internal functions">

    private void initializeView() {
        ApplicationContext c = Application.getInstance(Petrinetz.class).getContext();
        ActionMap actionMap = c.getActionMap(EdgePropertiesDialog.class, this);
        ResourceMap rm = c.getResourceMap(EdgePropertiesDialog.class);

        setModal(true);
        setResizable(false);
        setTitle(rm.getString("EdgePropertiesDialog.Title"));

        weightLabel = new JLabel(rm.getString("EdgePropertiesDialog.JLabel.weight.Text"));
        weightField = new JFormattedTextField(NumberFormat.getIntegerInstance());

        okButton = new JButton(actionMap.get("commitChangesAction"));
        okButton.setText(rm.getString("EdgePropertiesDialog.JButton.ok.Text"));

        cancelButton = new JButton(actionMap.get("closeDialogAction"));
        cancelButton.setText(rm.getString("EdgePropertiesDialog.JButton.cancel.Text"));

        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        getContentPane().setLayout(layout);
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(weightLabel)
                .addComponent(weightField)
            )
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(okButton)
                .addComponent(cancelButton)
            )
        );
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(weightLabel)
                    .addComponent(weightField, 75, 75, 75)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(okButton)
                    .addComponent(cancelButton)
                )
            )
        );

        getRootPane().setDefaultButton(okButton);
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Actions">

    @Action
    public void closeDialogAction() {
        dispose();
    }

    @Action
    public void commitChangesAction() {
        try {
            _edge.weight = Integer.parseInt(weightField.getText());
        } catch(Exception e) {
            ErrorController.showError("Invalid weight format!", "Error");
        }

        dispose();

        Petrinetz.getApplication().getMainFrame().repaint();
    }


    // </editor-fold>

}
