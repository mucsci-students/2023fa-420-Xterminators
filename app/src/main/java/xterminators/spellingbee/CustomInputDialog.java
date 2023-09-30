package xterminators.spellingbee;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class CustomInputDialog extends JDialog {
    private JTextField tbWord;
    private JTextField tbRequiredLetter;
    private JButton okButton;
    private JButton cancelButton;

    private String baseWord;
    private String requiredLetter;

    public CustomInputDialog(Frame parent) {
        super(parent, "Custom Input Dialog", true);
        setLayout(new GridLayout(3, 2));

        tbWord = new JTextField();
        tbRequiredLetter = new JTextField();
        okButton = new JButton("OK");
        okButton.setBackground(Color.white);
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.white);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                baseWord = tbWord.getText();
                requiredLetter = tbRequiredLetter.getText();
                dispose();
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

        add(new JLabel("Base Word:"));
        add(tbWord);
        add(new JLabel("Required Letter:"));
        add(tbRequiredLetter);
        add(okButton);
        add(cancelButton);

        pack();
        setLocationRelativeTo(parent);
    }

    public String getBaseWord() {
        return baseWord;
    }

    public String getRequiredLetter() {
        return requiredLetter;
    }
}