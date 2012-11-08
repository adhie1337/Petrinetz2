package petrinetz.view.editor;

import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import petrinetz.Petrinetz;
import petrinetz.control.entities.Transition;

/**
 * A dialog class that can edit transition properties.
 * @author PIAPAAI.ELTE
 */
public class TransitionPropertiesDialog extends JDialog{

    // <editor-fold defaultstate="opened" desc="Properties">

    private JButton okButton;
    private JButton cancelButton;

    private JLabel nameLabel;
    private JTextField nameField;

    private JLabel labelLabel;
    private JTextField labelField;

    private Transition _transition;

    public Transition getTransition() {
        return _transition;
    }

    public void setTransition(Transition t) {
        _transition = t;

        if(_transition != null) {
            nameField.setText(_transition.sign);
            labelField.setText(_transition.label);
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructor">

    public TransitionPropertiesDialog(java.awt.Frame owner) {
        super(owner);

        initializeView();
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Internal functions">

    private void initializeView() {
        ApplicationContext c = Application.getInstance(Petrinetz.class).getContext();
        ActionMap actionMap = c.getActionMap(TransitionPropertiesDialog.class, this);
        ResourceMap rm = c.getResourceMap(TransitionPropertiesDialog.class);

        setModal(true);
        setResizable(false);
        setTitle(rm.getString("TransitionPropertiesDialog.Title"));

        nameLabel = new JLabel(rm.getString("TransitionPropertiesDialog.JLabel.name.Text"));

        labelLabel = new JLabel(rm.getString("TransitionPropertiesDialog.JLabel.label.Text"));

        nameField = new JTextField();
        nameField.setSize(10, nameField.getSize().height);
        nameField.setEnabled(false);

        labelField = new JTextField();
        labelField.setSize(10, nameField.getSize().height);

        okButton = new JButton(actionMap.get("commitChangesAction"));
        okButton.setText(rm.getString("TransitionPropertiesDialog.JButton.ok.Text"));

        cancelButton = new JButton(actionMap.get("closeDialogAction"));
        cancelButton.setText(rm.getString("TransitionPropertiesDialog.JButton.cancel.Text"));

        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        getContentPane().setLayout(layout);
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(nameLabel)
                    .addComponent(nameField)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(labelLabel)
                    .addComponent(labelField)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(okButton)
                    .addComponent(cancelButton)
                )
            )
        );
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(nameLabel)
                    .addComponent(nameField, 75, 75, 75)
                )
                .addGroup(layout.createSequentialGroup()
                    .addComponent(labelLabel)
                    .addComponent(labelField, 75, 75, 75)
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
        _transition.sign = nameField.getText();
        _transition.label = labelField.getText();
        dispose();

        Petrinetz.getApplication().getMainFrame().repaint();
    }


    // </editor-fold>

}
