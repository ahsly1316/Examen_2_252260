/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ajustepolinomial;
/**
 * Clase auxiliar con métodos matemáticos para resolver sistemas de ecuaciones.
 */
/**
 *
 * @author chant
 */
public class Utilidades {
        /**
     * Resuelve un sistema lineal A*x = b usando eliminación de Gauss.
     * @param A Matriz cuadrada de coeficientes.
     * @param b Vector del lado derecho.
     * @return Vector solución x.
     */
    public static double[] resolverSistema(double[][] A, double[] b) {
        int n = b.length;

        // Copiamos las matrices para no modificar los originales
        double[][] M = new double[n][n];
        double[] N = new double[n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            N[i] = b[i];
        }

        // Eliminación hacia adelante
        for (int k = 0; k < n - 1; k++) {
            for (int i = k + 1; i < n; i++) {
                double factor = M[i][k] / M[k][k];
                for (int j = k; j < n; j++) {
                    M[i][j] -= factor * M[k][j];
                }
                N[i] -= factor * N[k];
            }
        }

        // Sustitución hacia atrás
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double suma = N[i];
            for (int j = i + 1; j < n; j++) {
                suma -= M[i][j] * x[j];
            }
            x[i] = suma / M[i][i];
        }

        return x;
    }
}
