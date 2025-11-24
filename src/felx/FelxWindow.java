/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package felx;

/**
 *
 * @author ashley
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class FelxWindow extends JFrame {
    
    private final FelxCmd cmd;
    private final JTextArea consola;
    private final JScrollPane scroll;

    private String promptActual = "";

    public FelxWindow() {
        super("FEL.X - CatOS Terminal");

        cmd = new FelxCmd();
        consola = crearConsola();
        scroll = new JScrollPane(consola);

        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);

        // Banner de bienvenida estilo CatOS
        imprimir("CatOS FEL.X Terminal\n");
        imprimir("(c) CatOS Project. All rights reserved.\n\n");
        mostrarPrompt();
    }

    private JTextArea crearConsola() {
        JTextArea txt = new JTextArea();
        txt.setEditable(true);
        txt.setFont(new Font("Consolas", Font.PLAIN, 16));
        txt.setBackground(Color.BLACK);
        txt.setForeground(Color.WHITE);
        txt.setMargin(new Insets(5, 5, 5, 5));

        txt.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                int caretPos = consola.getCaretPosition();
                int limite = consola.getText().lastIndexOf(promptActual) + promptActual.length();

                // No dejar borrar antes del prompt
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && caretPos <= limite) {
                    e.consume();
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_DELETE && caretPos < consola.getText().length()
                        && caretPos < limite) {
                    e.consume();
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_LEFT && caretPos <= limite) {
                    e.consume();
                    consola.setCaretPosition(consola.getText().length());
                    return;
                }

                // ENTER: ejecutar comando
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();

                    String texto = consola.getText();
                    int posPrompt = texto.lastIndexOf(promptActual);

                    String comando = texto.substring(posPrompt + promptActual.length()).trim();

                    procesarEntrada(comando);
                    return;
                }

                // Si intenta mover el caret antes del prompt, lo mandamos al final
                if (caretPos < limite) {
                    consola.setCaretPosition(consola.getText().length());
                }
            }
        });

        return txt;
    }

    private void imprimir(String texto) {
        consola.append(texto);
        consola.setCaretPosition(consola.getDocument().getLength());
    }

    private void mostrarPrompt() {
        promptActual = cmd.getPrompt();
        imprimir(promptActual);
    }

    private void procesarEntrada(String comando) {
        imprimir("\n");

        if (!comando.isBlank()) {
            String salida = cmd.procesarComando(comando);
            if (salida != null) {
                imprimir(salida);
            }
        }

        mostrarPrompt();
    }
    
}
