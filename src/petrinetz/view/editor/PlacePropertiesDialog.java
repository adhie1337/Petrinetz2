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
import petrinetz.control.entities.Place;

/**
 * A dialog to set the selected place's settings
 * @author PIAPAAI.ELTE
 */
public class PlacePropertiesDialog extends JDialog{

    // <editor-fold defaultstate="opened" desc="Properties">

    private JButton okButton;
    private JButton cancelButton;

    private JLabel nameLabel;
    private JTextField nameField;

    private JLabel labelLabel;
    private JTextField labelField;

    private JLabel weightLabel;
    private JTextField weightField;

    private Place _place;

    public Place getPlace() {
        return _place;
    }

    public void setPlace(Place t) {
        _place = t;

        if(_place != null) {
            nameField.setText(_place.sign);
            labelField.setText(_place.label);
            weightField.setText(NumberFormat.getIntegerInstance().format(_place.weight));
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Constructor">

    public PlacePropertiesDialog(java.awt.Frame owner) {
        super(owner);

        initializeView();
    }

    // </editor-fold>

    // <editor-fold defaultstate="opened" desc="Internal functions">

    private void initializeView() {
        ApplicationContext c = Application.getInstance(Petrinetz.class).getContext();
        ActionMap actionMap = c.getActionMap(PlacePropertiesDialog.class, this);
        ResourceMap rm = c.getResourceMap(PlacePropertiesDialog.class);

        setModal(true);
        setResizable(false);
        setTitle(rm.getString("PlacePropertiesDialog.Title"));

        nameLabel = new JLabel(rm.getString("PlacePropertiesDialog.JLabel.name.Text"));
        labelLabel = new JLabel(rm.getString("PlacePropertiesDialog.JLabel.label.Text"));
        weightLabel = new JLabel(rm.getString("PlacePropertiesDialog.JLabel.weight.Text"));

        nameField = new JTextField();
        nameField.setSize(10, nameField.getSize().height);
        nameField.setEnabled(false);

        labelField = new JTextField();
        labelField.setSize(10, nameField.getSize().height);

        weightField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        weightField.setSize(10, nameField.getSize().height);

        okButton = new JButton(actionMap.get("commitChangesAction"));
        okButton.setText(rm.getString("PlacePropertiesDialog.JButton.ok.Text"));

        cancelButton = new JButton(actionMap.get("closeDialogAction"));
        cancelButton.setText(rm.getString("PlacePropertiesDialog.JButton.cancel.Text"));

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
                    .addComponent(weightLabel)
                    .addComponent(weightField)
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
        _place.sign = nameField.getText();
        _place.label = labelField.getText();

        try {
            _place.weight = Integer.parseInt(weightField.getText());
        } catch(Exception e) {
            ErrorController.showError("Invalid weight format!", "Error");
        }

        dispose();

        Petrinetz.getApplication().getMainFrame().repaint();
    }


    // </editor-fold>

}
