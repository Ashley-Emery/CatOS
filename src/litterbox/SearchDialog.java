/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package litterbox;

/**
 *
 * @author ashley
 */

import javax.swing.*;
import java.awt.*;

public class SearchDialog extends JDialog {

    private final JTextField txtQuery = new JTextField(25);
    private boolean accepted = false;

    public SearchDialog(Frame owner) {
        super(owner, "Search", true);
        buildUI();
    }

    private void buildUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel center = new JPanel();
        center.add(new JLabel("Name contains:"));
        center.add(txtQuery);
        panel.add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton btnSearch = new JButton("Search");
        JButton btnCancel = new JButton("Cancel");
        buttons.add(btnSearch);
        buttons.add(btnCancel);
        panel.add(buttons, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> {
            accepted = true;
            setVisible(false);
        });
        btnCancel.addActionListener(e -> {
            accepted = false;
            setVisible(false);
        });

        setContentPane(panel);
        pack();
        setLocationRelativeTo(getOwner());
    }

    public String showDialog() {
        txtQuery.setText("");
        accepted = false;
        setVisible(true);
        return accepted ? txtQuery.getText().trim() : null;
    }
}