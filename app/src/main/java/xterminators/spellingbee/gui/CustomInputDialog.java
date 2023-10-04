package xterminators.spellingbee.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomInputDialog extends JDialog {
    private JTextField tbWord;
    private JTextField tbRequiredLetter;
    private JButton okButton;
    private JButton cancelButton;

    private String baseWord;
    private String requiredLetter;
    private Boolean isCanceled;

    public CustomInputDialog(Frame parent) {
        super(parent, "Get Starting Word", true);
        setLayout(new GridLayout(3, 2));

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
                baseWord = null;
                requiredLetter = null;
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

        add(new JLabel("Base Word:"));
        add(tbWord);
        add(new JLabel("Required Letter:"));
        add(tbRequiredLetter);
        add(okButton);
        add(cancelButton);

        pack();
        setLocationRelativeTo(parent);
    }

    private void setPropertiesAndClose() {
        isCanceled = false;
        baseWord = tbWord.getText();
        requiredLetter = tbRequiredLetter.getText();
        dispose();
    }

    public Boolean isCanceled() {
        return isCanceled;
    }

    public String getBaseWord() {
        return baseWord;
    }

    public String getRequiredLetter() {
        return requiredLetter;
    }
}