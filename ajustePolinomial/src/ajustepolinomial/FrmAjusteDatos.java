package ajustepolinomial;


import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author chant
 */
public class FrmAjusteDatos extends javax.swing.JFrame {
private DefaultTableModel modeloTabla;
private DecimalFormat formato = new DecimalFormat("0.000000");
    /**
     * Creates new form FrmAjusteDatos
     */
    public FrmAjusteDatos() {
        initComponents();
        setLocationRelativeTo(null);
        txtGrado.setEnabled(true);
            
    modeloTabla = new DefaultTableModel();
    tblPuntos.setModel(modeloTabla);
    }
    


private void limpiarCampos() {
    modeloTabla.setRowCount(0);
    txtResultado.setText("");
    txtNumPuntos.setText("");
    txtGrado.setText("");
}

private void calcularAjuste() {
    try {
        int n = modeloTabla.getRowCount();

        if (n == 0) {
            JOptionPane.showMessageDialog(this, "Debe generar la tabla e ingresar los puntos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipo = cmbTipoAjuste.getSelectedItem().toString();
        DecimalFormat formato = new DecimalFormat("0.000000");
        DecimalFormat formatoR2 = new DecimalFormat("0.0000");
        
        StringBuilder resultadoCompleto = new StringBuilder();
        StringBuilder pasos = new StringBuilder();

        if (tipo.equals("Lineal")) {
            resultadoCompleto.append("AJUSTE LINEAL SIMPLE\n");
            resultadoCompleto.append("====================\n\n");
            
            pasos.append("=== INICIO DEL CÁLCULO DE AJUSTE LINEAL SIMPLE ===\n");
            pasos.append("Ecuación: y = mx + b\n");
            pasos.append("Número de puntos: ").append(n).append("\n");

            // Leer datos
            double[] x = new double[n];
            double[] y = new double[n];
            pasos.append("\n--- PASO 1: Datos de entrada ---\n");
            for (int i = 0; i < n; i++) {
                x[i] = Double.parseDouble(modeloTabla.getValueAt(i, 0).toString());
                y[i] = Double.parseDouble(modeloTabla.getValueAt(i, 1).toString());
                pasos.append(String.format("Punto %d: (%.4f, %.4f)\n", i+1, x[i], y[i]));
            }

            // Calcular sumatorias
            pasos.append("\n--- PASO 2: Calcular sumatorias ---\n");
            double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
            for (int i = 0; i < n; i++) {
                sumX += x[i];
                sumY += y[i];
                sumXY += x[i] * y[i];
                sumX2 += x[i] * x[i];
            }
            pasos.append(String.format("Σx = %.4f\n", sumX));
            pasos.append(String.format("Σy = %.4f\n", sumY));
            pasos.append(String.format("Σxy = %.4f\n", sumXY));
            pasos.append(String.format("Σx² = %.4f\n", sumX2));

            // Calcular coeficientes
            pasos.append("\n--- PASO 3: Calcular coeficientes ---\n");
            double pendiente = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
            double intercepto = (sumY - pendiente * sumX) / n;
            
            pasos.append("Fórmula para pendiente (m):\n");
            pasos.append(String.format("m = (n·Σxy - Σx·Σy) / (n·Σx² - (Σx)²)\n"));
            pasos.append(String.format("m = (%d·%.4f - %.4f·%.4f) / (%d·%.4f - %.4f²)\n", 
                n, sumXY, sumX, sumY, n, sumX2, sumX));
            pasos.append(String.format("m = %.6f\n", pendiente));

            pasos.append("\nFórmula para intercepto (b):\n");
            pasos.append(String.format("b = (Σy - m·Σx) / n\n"));
            pasos.append(String.format("b = (%.4f - %.4f·%.4f) / %d\n", sumY, pendiente, sumX, n));
            pasos.append(String.format("b = %.6f\n", intercepto));

            // Resultado principal
            resultadoCompleto.append("Ecuación ajustada:\n");
            resultadoCompleto.append(String.format("y = %.6fx + %.6f\n\n", pendiente, intercepto));

            // Calcular R²
            pasos.append("\n--- PASO 4: Calcular R² ---\n");
            double mediaY = sumY / n;
            double sst = 0, sse = 0;
            
            for (int i = 0; i < n; i++) {
                double yPred = pendiente * x[i] + intercepto;
                sst += Math.pow(y[i] - mediaY, 2);
                sse += Math.pow(y[i] - yPred, 2);
                pasos.append(String.format("Punto %d: y_real=%.4f, y_pred=%.4f, error=%.4f\n", 
                    i+1, y[i], yPred, y[i] - yPred));
            }
            
            double r2 = 1 - (sse / sst);
            pasos.append(String.format("\nSST = %.4f, SSE = %.4f\n", sst, sse));
            pasos.append(String.format("R² = 1 - (%.4f/%.4f) = %.4f\n", sse, sst, r2));

            resultadoCompleto.append("Estadísticos:\n");
            resultadoCompleto.append(String.format("R² = %s (%s%%)\n", 
                formatoR2.format(r2), formatoR2.format(r2 * 100)));

            pasos.append("\n=== CÁLCULO COMPLETADO ===\n");

        } else if (tipo.equals("Polinomial")) {
            resultadoCompleto.append("AJUSTE POLINOMIAL\n");
            resultadoCompleto.append("=================\n\n");
            
            // Validar grado
            if (txtGrado.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa un grado válido.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int grado = Integer.parseInt(txtGrado.getText().trim());
            
            if (grado >= n) {
                JOptionPane.showMessageDialog(this, "El grado debe ser menor que el número de puntos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            pasos.append("=== INICIO DEL CÁLCULO DE AJUSTE POLINOMIAL ===\n");
            pasos.append("Grado del polinomio: ").append(grado).append("\n");
            pasos.append("Número de puntos: ").append(n).append("\n");

            // Leer datos
            double[] x = new double[n];
            double[] y = new double[n];
            pasos.append("\n--- PASO 1: Datos de entrada ---\n");
            for (int i = 0; i < n; i++) {
                x[i] = Double.parseDouble(modeloTabla.getValueAt(i, 0).toString());
                y[i] = Double.parseDouble(modeloTabla.getValueAt(i, 1).toString());
                pasos.append(String.format("Punto %d: (%.4f, %.4f)\n", i+1, x[i], y[i]));
            }

            // Usar la clase existente para el cálculo
            AjustePolinomio ajuste = new AjustePolinomio(x, y, grado);
            double[] coef = ajuste.getCoeficientes();

            // Mostrar pasos del sistema de ecuaciones
            pasos.append("\n--- PASO 2: Sistema de ecuaciones normales ---\n");
            pasos.append("Se resuelve el sistema A·c = b donde:\n");
            pasos.append("A[i][j] = Σ(x^(i+j)), b[i] = Σ(y·x^i)\n");
            pasos.append("Usando eliminación Gauss-Jordan\n");

            // Mostrar coeficientes como pasos
            pasos.append("\n--- PASO 3: Coeficientes obtenidos ---\n");
            for (int i = 0; i < coef.length; i++) {
                if (i == 0) {
                    pasos.append(String.format("a%d (término independiente) = %.6f\n", i, coef[i]));
                } else {
                    pasos.append(String.format("a%d (coeficiente de x^%d) = %.6f\n", i, i, coef[i]));
                }
            }

            // Resultado principal
            resultadoCompleto.append("Ecuación ajustada:\n");
            resultadoCompleto.append("y = ");
            for (int i = 0; i < coef.length; i++) {
                if (i > 0) resultadoCompleto.append(" + ");
                resultadoCompleto.append(formato.format(coef[i]));
                if (i > 0) {
                    resultadoCompleto.append("*x");
                    if (i > 1) resultadoCompleto.append("^").append(i);
                }
            }
            resultadoCompleto.append("\n\n");

            // Calcular R² manualmente
            pasos.append("\n--- PASO 4: Calcular R² ---\n");
            double sumY = 0;
            for (double valor : y) sumY += valor;
            double mediaY = sumY / n;
            
            double sst = 0, sse = 0;
            for (int i = 0; i < n; i++) {
                double yPred = ajuste.evaluar(x[i]);
                sst += Math.pow(y[i] - mediaY, 2);
                sse += Math.pow(y[i] - yPred, 2);
                pasos.append(String.format("Punto %d: y_real=%.4f, y_pred=%.4f, error=%.4f\n", 
                    i+1, y[i], yPred, y[i] - yPred));
            }
            
            double r2 = 1 - (sse / sst);
            pasos.append(String.format("\nSST = %.4f, SSE = %.4f\n", sst, sse));
            pasos.append(String.format("R² = 1 - (%.4f/%.4f) = %.4f\n", sse, sst, r2));

            resultadoCompleto.append("Estadísticos:\n");
            resultadoCompleto.append(String.format("R² = %s (%s%%)\n", 
                formatoR2.format(r2), formatoR2.format(r2 * 100)));

            pasos.append("\n=== CÁLCULO COMPLETADO ===\n");

        } else { // Lineal Multiple
            resultadoCompleto.append("REGRESIÓN LINEAL MÚLTIPLE\n");
            resultadoCompleto.append("========================\n\n");
            
            int numVariables = modeloTabla.getColumnCount() - 1;
            pasos.append("=== INICIO DEL CÁLCULO DE REGRESIÓN LINEAL MÚLTIPLE ===\n");
            pasos.append("Número de puntos: ").append(n).append("\n");
            pasos.append("Número de variables independientes: ").append(numVariables).append("\n");

            // Leer datos
            double[][] X = new double[n][numVariables];
            double[] y = new double[n];
            
            pasos.append("\n--- PASO 1: Datos de entrada ---\n");
            for (int i = 0; i < n; i++) {
                StringBuilder punto = new StringBuilder("Punto ").append(i+1).append(": ");
                for (int j = 0; j < numVariables; j++) {
                    X[i][j] = Double.parseDouble(modeloTabla.getValueAt(i, j).toString());
                    punto.append(String.format("x%d=%.4f, ", j+1, X[i][j]));
                }
                y[i] = Double.parseDouble(modeloTabla.getValueAt(i, numVariables).toString());
                punto.append(String.format("y=%.4f", y[i]));
                pasos.append(punto.toString()).append("\n");
            }

            // Usar la clase existente
            AjusteLinealMultiple ajuste = new AjusteLinealMultiple(X, y);
            double[] coef = ajuste.getCoeficientes();

            // Mostrar pasos del método
            pasos.append("\n--- PASO 2: Método de mínimos cuadrados ---\n");
            pasos.append("Se resuelve el sistema (XᵀX)β = Xᵀy\n");
            pasos.append("Donde X es la matriz de datos aumentada con columna de 1s\n");
            pasos.append("Usando eliminación Gaussiana\n");

            pasos.append("\n--- PASO 3: Coeficientes obtenidos ---\n");
            for (int i = 0; i < coef.length; i++) {
                if (i == 0) {
                    pasos.append(String.format("β%d (intercepto) = %.6f\n", i, coef[i]));
                } else {
                    pasos.append(String.format("β%d (coeficiente de x%d) = %.6f\n", i, i, coef[i]));
                }
            }

            // Resultado principal
            resultadoCompleto.append("Ecuación ajustada:\n");
            resultadoCompleto.append("y = ").append(formato.format(coef[0]));
            for (int i = 1; i < coef.length; i++) {
                resultadoCompleto.append(" + ").append(formato.format(coef[i])).append("*x").append(i);
            }
            resultadoCompleto.append("\n\n");

            // Calcular R² manualmente
            pasos.append("\n--- PASO 4: Calcular R² ---\n");
            double sumY = 0;
            for (double valor : y) sumY += valor;
            double mediaY = sumY / n;
            
            double sst = 0, sse = 0;
            for (int i = 0; i < n; i++) {
                double yPred = ajuste.evaluar(X[i]);
                sst += Math.pow(y[i] - mediaY, 2);
                sse += Math.pow(y[i] - yPred, 2);
                pasos.append(String.format("Punto %d: y_real=%.4f, y_pred=%.4f, error=%.4f\n", 
                    i+1, y[i], yPred, y[i] - yPred));
            }
            
            double r2 = 1 - (sse / sst);
            pasos.append(String.format("\nSST = %.4f, SSE = %.4f\n", sst, sse));
            pasos.append(String.format("R² = 1 - (%.4f/%.4f) = %.4f\n", sse, sst, r2));

            resultadoCompleto.append("Estadísticos:\n");
            resultadoCompleto.append(String.format("R² = %s (%s%%)\n", 
                formatoR2.format(r2), formatoR2.format(r2 * 100)));

            pasos.append("\n=== CÁLCULO COMPLETADO ===\n");
        }

        // Combinar resultados y pasos
        resultadoCompleto.append("\n=== PASOS DETALLADOS ===\n");
        resultadoCompleto.append(pasos.toString());
        
        txtResultado.setText(resultadoCompleto.toString());

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Introduce solo números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error en los datos ingresados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

/**
 * Calcula el error estándar de la estimación.
 */
private double calcularErrorEstandar(RegresionLinealMultiple regresion, double[][] X, double[] y) {
    int n = y.length;
    int p = X[0].length; // número de variables independientes
    
    double sumaResiduos = 0;
    for (int i = 0; i < n; i++) {
        double prediccion = regresion.evaluar(X[i]);
        double residuo = y[i] - prediccion;
        sumaResiduos += residuo * residuo;
    }
    
    return Math.sqrt(sumaResiduos / (n - p - 1));
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cmbTipoAjuste = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtNumPuntos = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtGrado = new javax.swing.JTextField();
        btnGenerarTabla = new javax.swing.JButton();
        panelTabla = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPuntos = new javax.swing.JTable();
        btnAgregarFila = new javax.swing.JButton();
        btnEliminarFila = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnCalcular = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        panelResultado = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtResultado = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Regresión con Mínimos Cuadrados");

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 153, 153));
        jLabel1.setText("Seleccione tipo de ajuste:");

        cmbTipoAjuste.setBackground(new java.awt.Color(255, 153, 153));
        cmbTipoAjuste.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        cmbTipoAjuste.setForeground(new java.awt.Color(255, 153, 153));
        cmbTipoAjuste.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lineal", "Polinomial", "Lineal Multiple" }));
        cmbTipoAjuste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTipoAjusteActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 153, 153));
        jLabel2.setText("Número de puntos:");

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 153, 153));
        jLabel3.setText("Grado del polinomio:");

        btnGenerarTabla.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btnGenerarTabla.setForeground(new java.awt.Color(255, 153, 153));
        btnGenerarTabla.setText("Generar Tabla");
        btnGenerarTabla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarTablaActionPerformed(evt);
            }
        });

        tblPuntos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblPuntos);

        javax.swing.GroupLayout panelTablaLayout = new javax.swing.GroupLayout(panelTabla);
        panelTabla.setLayout(panelTablaLayout);
        panelTablaLayout.setHorizontalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        panelTablaLayout.setVerticalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnAgregarFila.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btnAgregarFila.setForeground(new java.awt.Color(255, 153, 153));
        btnAgregarFila.setText("Agregar Fila");
        btnAgregarFila.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarFilaActionPerformed(evt);
            }
        });

        btnEliminarFila.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btnEliminarFila.setForeground(new java.awt.Color(255, 153, 153));
        btnEliminarFila.setText("Eliminar Fila");
        btnEliminarFila.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarFilaActionPerformed(evt);
            }
        });

        btnCalcular.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btnCalcular.setForeground(new java.awt.Color(255, 153, 153));
        btnCalcular.setText("Calcular");
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        btnLimpiar.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 153, 153));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnSalir.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        btnSalir.setForeground(new java.awt.Color(255, 153, 153));
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 153, 153));
        jLabel4.setText("Resultado:");

        txtResultado.setColumns(20);
        txtResultado.setRows(5);
        jScrollPane2.setViewportView(txtResultado);

        javax.swing.GroupLayout panelResultadoLayout = new javax.swing.GroupLayout(panelResultado);
        panelResultado.setLayout(panelResultadoLayout);
        panelResultadoLayout.setHorizontalGroup(
            panelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadoLayout.createSequentialGroup()
                .addGap(230, 230, 230)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelResultadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        panelResultadoLayout.setVerticalGroup(
            panelResultadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(18, 18, 18)
                                        .addComponent(cmbTipoAjuste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtNumPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtGrado, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(223, 223, 223)
                                .addComponent(btnGenerarTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(panelTabla, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAgregarFila, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(btnEliminarFila, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(106, 106, 106))
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
            .addComponent(panelResultado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbTipoAjuste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNumPuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtGrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnGenerarTabla)
                .addGap(26, 26, 26)
                .addComponent(panelTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarFila)
                    .addComponent(btnEliminarFila))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCalcular)
                    .addComponent(btnLimpiar)
                    .addComponent(btnSalir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerarTablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarTablaActionPerformed
    try {
        int n = Integer.parseInt(txtNumPuntos.getText().trim());
        if (n <= 0) {
            JOptionPane.showMessageDialog(this, "Ingresa un número válido de puntos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloTabla = new DefaultTableModel();
        String tipo = cmbTipoAjuste.getSelectedItem().toString();

        if (tipo.equals("Polinomial")) {
            // Validar que se haya ingresado el grado
            if (txtGrado.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa el grado del polinomio primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int grado = Integer.parseInt(txtGrado.getText().trim());
            
            if (grado < 1) {
                JOptionPane.showMessageDialog(this, "El grado debe ser mayor o igual a 1.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Crear columnas para polinomial
            for (int i = 1; i <= grado; i++) {
                if (i == 1) {
                    modeloTabla.addColumn("X");
                } else {
                    modeloTabla.addColumn("X^" + i);
                }
            }
            modeloTabla.addColumn("Y");
            
        } else { // Lineal Multiple
            // Para regresión lineal múltiple, preguntar cuántas variables X
            String input = JOptionPane.showInputDialog(this, 
                "¿Cuántas variables independientes (X) deseas?", "Variables", JOptionPane.QUESTION_MESSAGE);
            
            if (input == null) return; // Usuario canceló
            
            int numVariables;
            try {
                numVariables = Integer.parseInt(input.trim());
                if (numVariables < 1) {
                    JOptionPane.showMessageDialog(this, "Debe haber al menos 1 variable independiente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número de variables no válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear columnas para lineal múltiple
            for (int i = 1; i <= numVariables; i++) {
                modeloTabla.addColumn("X" + i);
            }
            modeloTabla.addColumn("Y");
        }

        // Agregar filas vacías
        for (int i = 0; i < n; i++) {
            Object[] fila = new Object[modeloTabla.getColumnCount()];
            modeloTabla.addRow(fila);
        }

        tblPuntos.setModel(modeloTabla);

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Introduce solo números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnGenerarTablaActionPerformed

    private void btnAgregarFilaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarFilaActionPerformed
        modeloTabla.addRow(new Object[modeloTabla.getColumnCount()]);
    }//GEN-LAST:event_btnAgregarFilaActionPerformed

    private void btnEliminarFilaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarFilaActionPerformed
            int filaSeleccionada = tblPuntos.getSelectedRow();
    if (filaSeleccionada >= 0) {
        modeloTabla.removeRow(filaSeleccionada);
    } else {
        JOptionPane.showMessageDialog(this, "Seleccione una fila para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_btnEliminarFilaActionPerformed

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        calcularAjuste();
    }//GEN-LAST:event_btnCalcularActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
         System.exit(0);
    }//GEN-LAST:event_btnSalirActionPerformed

    private void cmbTipoAjusteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTipoAjusteActionPerformed
    String tipo = cmbTipoAjuste.getSelectedItem().toString();
    
    
    if (tipo.equals("Polinomial")) {
        txtGrado.setEnabled(true); 
        jLabel3.setEnabled(true);  
    } else {
        txtGrado.setText("");      
        txtGrado.setEnabled(false); 
        jLabel3.setEnabled(false);  
    }
    
    
    modeloTabla.setRowCount(0);
    txtResultado.setText("");
    }//GEN-LAST:event_cmbTipoAjusteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmAjusteDatos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmAjusteDatos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmAjusteDatos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmAjusteDatos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmAjusteDatos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarFila;
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnEliminarFila;
    private javax.swing.JButton btnGenerarTabla;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cmbTipoAjuste;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel panelResultado;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JTable tblPuntos;
    private javax.swing.JTextField txtGrado;
    private javax.swing.JTextField txtNumPuntos;
    private javax.swing.JTextArea txtResultado;
    // End of variables declaration//GEN-END:variables
}
