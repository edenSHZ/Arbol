import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class NodoArbol {
    String valor;
    NodoArbol izquierda, derecha;

    public NodoArbol(String valor) {
        this.valor = valor;
        izquierda = derecha = null;
    }

    public NodoArbol(NodoArbol izquierda, String valor, NodoArbol derecha) {
        this.valor = valor;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }
}

class Pila {
    private Stack<Object> pila;

    public Pila() {
        pila = new Stack<>();
    }

    public void push(Object item) {
        pila.push(item);
    }

    public Object pop() {
        return pila.pop();
    }

    public Object top() {
        return pila.peek();
    }

    public boolean estaVacia() {
        return pila.isEmpty();
    }
}

public class ArbolExpresiones extends JFrame {
    Pila pOperandos;
    Pila pOperadores;
    final String blanco;
    final String operadores;

    private JTextField expressionField;
    private JTextArea outputArea;

    public ArbolExpresiones() {
        pOperandos = new Pila();
        pOperadores = new Pila();
        blanco = " \t";
        operadores = ")+-*/%^(";

        setTitle("Árbol de Expresiones");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        expressionField = new JTextField();
        panel.add(expressionField, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton buildButton = new JButton("Construir Árbol");
        buildButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                construirYMostrarArbol();
            }
        });
        buttonPanel.add(buildButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(panel);
    }

    private void construirYMostrarArbol() {
        String expresion = expressionField.getText();
        NodoArbol raiz = construirArbol(expresion);

        StringBuilder output = new StringBuilder();
        output.append("El árbol en inorden es: ");
        imprime(raiz, output);
        output.append("\nEl árbol en postorden es: ");
        imprimePos(raiz, output);
        output.append("\nEl árbol en preorden es: ");
        imprimePre(raiz, output);
        output.append("\n\n");

        outputArea.setText(output.toString());
    }

    public NodoArbol construirArbol(String expresion) {
        StringTokenizer tokenizer;
        String token;
        NodoArbol raiz = null;

        tokenizer = new StringTokenizer(expresion, blanco + operadores, true);
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (blanco.indexOf(token) >= 0) {
                // Es un espacio en blanco, se ignora
            } else if (operadores.indexOf(token) < 0) {
                // Es operando y lo guarda como nodo del arbol
                pOperandos.push(new NodoArbol(token));
            } else if (token.equals(")")) {
                // Saca elementos hasta encontrar (
                while (!pOperadores.estaVacia() && !pOperadores.top().equals("(")) {
                    guardarSubArbol();
                }
                pOperadores.pop();  // Saca el parentesis izquierdo
            } else {
                if (!token.equals("(") && !pOperadores.estaVacia()) {
                    // operador diferente de cualquier parentesis
                    String op = (String) pOperadores.top();
                    while (!op.equals("(") && !pOperadores.estaVacia()
                            && operadores.indexOf(op) >= operadores.indexOf(token)) {
                        guardarSubArbol();
                        if (!pOperadores.estaVacia()) {
                            op = (String) pOperadores.top();
                        }
                    }
                }
                pOperadores.push(token);  // Guarda el operador
            }
        }
        // Sacar todo lo que queda
        raiz = (NodoArbol) pOperandos.top();
        while (!pOperadores.estaVacia()) {
            if (pOperadores.top().equals("(")) {
                pOperadores.pop();
            } else {
                guardarSubArbol();
                raiz = (NodoArbol) pOperandos.top();
            }
        }
        return raiz;
    }

    private void guardarSubArbol() {
        NodoArbol op2 = (NodoArbol) pOperandos.pop();
        NodoArbol op1 = (NodoArbol) pOperandos.pop();
        pOperandos.push(new NodoArbol(op1, (String) pOperadores.pop(), op2));
    }

    public void imprime(NodoArbol n, StringBuilder sb) {
        if (n != null) {
            imprime(n.izquierda, sb);
            sb.append(n.valor).append(" ");
            imprime(n.derecha, sb);
        }
    }

    public void imprimePos(NodoArbol n, StringBuilder sb) {
        if (n != null) {
            imprimePos(n.izquierda, sb);
            imprimePos(n.derecha, sb);
            sb.append(n.valor).append(" ");
        }
    }

    public void imprimePre(NodoArbol n, StringBuilder sb) {
        if (n != null) {
            sb.append(n.valor).append(" ");
            imprimePre(n.izquierda, sb);
            imprimePre(n.derecha, sb);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ArbolExpresiones().setVisible(true);
            }
        });
    }
}
