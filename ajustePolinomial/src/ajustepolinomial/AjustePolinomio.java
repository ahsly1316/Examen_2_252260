
package ajustepolinomial;

/**
 * Clase que realiza el ajuste polinomial de grado n a un conjunto de puntos.
 * Utiliza el método de mínimos cuadrados.
 * @author chant
 */
public class AjustePolinomio {
 private double[] coeficientes;

    /**
     * Constructor que calcula los coeficientes del polinomio ajustado.
     * @param x Valores de las abscisas.
     * @param y Valores de las ordenadas.
     * @param grado Grado del polinomio a ajustar.
     */
    public AjustePolinomio(double[] x, double[] y, int grado) {
        int n = x.length;
        int m = grado + 1;

        if (grado >= n) {
            throw new IllegalArgumentException("El grado debe ser menor que el número de puntos.");
        }

        double[][] A = new double[m][m];
        double[] b = new double[m];

        // Crear matriz normal y vector b
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                double suma = 0;
                for (int k = 0; k < n; k++) {
                    suma += Math.pow(x[k], i + j);
                }
                A[i][j] = suma;
            }
            double sumaB = 0;
            for (int k = 0; k < n; k++) {
                sumaB += y[k] * Math.pow(x[k], i);
            }
            b[i] = sumaB;
        }

        // Resolver sistema usando Gauss-Jordan
        coeficientes = gaussJordan(A, b);
    }

    public double[] getCoeficientes() {
        return coeficientes;
    }

    public double evaluar(double x) {
        double resultado = 0;
        for (int i = 0; i < coeficientes.length; i++) {
            resultado += coeficientes[i] * Math.pow(x, i);
        }
        return resultado;
    }

    // Método interno de Gauss-Jordan
    private double[] gaussJordan(double[][] A, double[] b) {
        int n = b.length;
        double[][] M = new double[n][n+1];

        // Construir matriz aumentada
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
            M[i][n] = b[i];
        }

        // Eliminación Gauss-Jordan
        for (int i = 0; i < n; i++) {
            // Hacer pivote 1
            double pivote = M[i][i];
            if (Math.abs(pivote) < 1e-10) throw new ArithmeticException("Sistema singular.");
            for (int j = i; j <= n; j++) M[i][j] /= pivote;

            // Eliminar columnas
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = M[k][i];
                    for (int j = i; j <= n; j++) M[k][j] -= factor * M[i][j];
                }
            }
        }

        // Extraer solución
        double[] x = new double[n];
        for (int i = 0; i < n; i++) x[i] = M[i][n];
        return x;
    }
}
