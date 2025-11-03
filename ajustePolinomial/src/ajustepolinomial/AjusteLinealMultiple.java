/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ajustepolinomial;

/**
 * Clase que realiza el ajuste lineal múltiple con el método de mínimos cuadrados.
 * @author chant
 */
public class AjusteLinealMultiple {
        private double[] coeficientes;

    /**
     * Constructor que calcula los coeficientes de la función lineal ajustada.
     * @param X Matriz de variables independientes (m filas, n columnas)
     * @param y Vector de valores dependientes
     */
    public AjusteLinealMultiple(double[][] X, double[] y) {
        int m = X.length;     // número de puntos
        int n = X[0].length;  // número de variables

        double[][] XT = new double[n + 1][m];
        double[][] Xmod = new double[m][n + 1];

        // Construimos matriz X con columna de 1s (para el término independiente)
        for (int i = 0; i < m; i++) {
            Xmod[i][0] = 1.0;
            for (int j = 0; j < n; j++) {
                Xmod[i][j + 1] = X[i][j];
            }
        }

        // Transpuesta de X
        for (int i = 0; i < n + 1; i++) {
            for (int j = 0; j < m; j++) {
                XT[i][j] = Xmod[j][i];
            }
        }

        // XT * X
        double[][] XTX = new double[n + 1][n + 1];
        for (int i = 0; i < n + 1; i++) {
            for (int j = 0; j < n + 1; j++) {
                double suma = 0;
                for (int k = 0; k < m; k++) {
                    suma += XT[i][k] * Xmod[k][j];
                }
                XTX[i][j] = suma;
            }
        }

        // XT * y
        double[] XTy = new double[n + 1];
        for (int i = 0; i < n + 1; i++) {
            double suma = 0;
            for (int k = 0; k < m; k++) {
                suma += XT[i][k] * y[k];
            }
            XTy[i] = suma;
        }

        // Resuelve el sistema (XT * X) * b = XT * y
        coeficientes = Utilidades.resolverSistema(XTX, XTy);
    }

    /**
     * Devuelve los coeficientes del modelo lineal ajustado.
     * @return Vector de coeficientes b0, b1, ..., bn
     */
    public double[] getCoeficientes() {
        return coeficientes;
    }

    /**
     * Evalúa la función ajustada para un conjunto de variables.
     * @param x Vector con los valores de las variables independientes.
     * @return Valor estimado de y.
     */
    public double evaluar(double[] x) {
        double resultado = coeficientes[0];
        for (int i = 0; i < x.length; i++) {
            resultado += coeficientes[i + 1] * x[i];
        }
        return resultado;
    }
}
