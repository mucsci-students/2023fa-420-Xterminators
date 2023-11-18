package xterminators.spellingbee.gui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class CustomInputDialog extends JDialog {
    private JTextField tbWord;
    private JTextField tbRequiredLetter;
    private JButton okButton;
    private JButton cancelButton;

    private String field1;
    private String field2;
    private Boolean isCanceled;

    public CustomInputDialog(Frame parent, String header, String field1Label, String field2Label) {
        super(parent, header, true);
        
        if (field2.isEmpty()) {
            setLayout(new GridLayout(2, 2));
        } else {
            setLayout(new GridLayout(3, 2));
        }

        isCanceled = true;
        tbWord = new JTextField();
        tbRequiredLetter = new JTextField();
        okButton = new JButton("OK");
        okButton.setBackground(Color.white);
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.white);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPropertiesAndClose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field1 = null;
                field2 = null;
                dispose();
            }
        });

        tbRequiredLetter.addActionListener(new ActionListener() {
            @Override 
            public void actionPerformed(ActionEvent e) {
                String text = tbRequiredLetter.getText();
                if (text != null && !text.isEmpty()) {
                    setPropertiesAndClose();
                }
            }
        });

        add(new JLabel(field1Label + ":"));
        add(tbWord);
        if (!field2.isEmpty()) {
            add(new JLabel(field2Label + ":"));
            add(tbRequiredLetter);
        }
        add(okButton);
        add(cancelButton);

        pack();
        setLocationRelativeTo(parent);
    }

    private void setPropertiesAndClose() {
        isCanceled = false;
        field1 = tbWord.getText();
        field2 = tbRequiredLetter.getText();
        dispose();
    }

    public Boolean isCanceled() {
        return isCanceled;
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }
}