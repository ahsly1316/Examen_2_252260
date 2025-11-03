package ajustepolinomial;

/**
 * Clase que realiza regresión lineal múltiple con el método de mínimos cuadrados.
 * Maneja cualquier número de variables independientes.
 * @author chant
 */
public class RegresionLinealMultiple {
    private double[] coeficientes;
    private double rCuadrado;
    private boolean calculadoExitosamente;

    /**
     * Constructor que calcula los coeficientes de la regresión lineal múltiple.
     * @param X Matriz de variables independientes (m filas, n columnas)
     * @param y Vector de valores dependientes
     */
    public RegresionLinealMultiple(double[][] X, double[] y) {
        calculadoExitosamente = false;
        calcularCoeficientes(X, y);
    }

    /**
     * Calcula los coeficientes usando el método de mínimos cuadrados.
     */
    private void calcularCoeficientes(double[][] X, double[] y) {
        try {
            int m = X.length;     // número de puntos
            int n = X[0].length;  // número de variables independientes

            // Validaciones básicas
            if (m <= n) {
                throw new IllegalArgumentException("Número de puntos debe ser mayor que número de variables");
            }

            // Verificar que no haya NaN o infinitos en los datos
            if (!datosValidos(X, y)) {
                throw new IllegalArgumentException("Los datos contienen valores no válidos");
            }

            // Matriz aumentada con columna de 1s para el intercepto
            double[][] Xmod = new double[m][n + 1];
            for (int i = 0; i < m; i++) {
                Xmod[i][0] = 1.0; // intercepto
                for (int j = 0; j < n; j++) {
                    Xmod[i][j + 1] = X[i][j];
                }
            }

            // Calcular X^T * X
            double[][] XTX = new double[n + 1][n + 1];
            for (int i = 0; i < n + 1; i++) {
                for (int j = 0; j < n + 1; j++) {
                    double suma = 0;
                    for (int k = 0; k < m; k++) {
                        suma += Xmod[k][i] * Xmod[k][j];
                    }
                    XTX[i][j] = suma;
                }
            }

            // Calcular X^T * y
            double[] XTy = new double[n + 1];
            for (int i = 0; i < n + 1; i++) {
                double suma = 0;
                for (int k = 0; k < m; k++) {
                    suma += Xmod[k][i] * y[k];
                }
                XTy[i] = suma;
            }

            // Resolver el sistema (X^T * X) * b = X^T * y
            coeficientes = Utilidades.resolverSistema(XTX, XTy);
            
            // Calcular R²
            calcularRCuadrado(X, y);
            
            calculadoExitosamente = true;

        } catch (Exception e) {
            System.err.println("Error en regresión lineal múltiple: " + e.getMessage());
            // Inicializar con valores por defecto en caso de error
            coeficientes = new double[X[0].length + 1];
            rCuadrado = 0;
            calculadoExitosamente = false;
        }
    }

    /**
     * Verifica que los datos sean válidos.
     */
    private boolean datosValidos(double[][] X, double[] y) {
        // Verificar X
        for (int i = 0; i < X.length; i++) {
            for (int j = 0; j < X[i].length; j++) {
                if (Double.isNaN(X[i][j]) || Double.isInfinite(X[i][j])) {
                    return false;
                }
            }
        }
        
        // Verificar y
        for (int i = 0; i < y.length; i++) {
            if (Double.isNaN(y[i]) || Double.isInfinite(y[i])) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Calcula el coeficiente de determinación R².
     */
    private void calcularRCuadrado(double[][] X, double[] y) {
        try {
            int n = y.length;
            double sumaY = 0;
            for (double valor : y) {
                sumaY += valor;
            }
            double mediaY = sumaY / n;

            // Suma total de cuadrados
            double SST = 0;
            for (double valor : y) {
                SST += Math.pow(valor - mediaY, 2);
            }

            // Suma de cuadrados de los residuos
            double SSE = 0;
            for (int i = 0; i < n; i++) {
                double yPredicho = evaluar(X[i]);
                SSE += Math.pow(y[i] - yPredicho, 2);
            }

            rCuadrado = 1 - (SSE / SST);
            
            // Asegurar que R² esté en el rango [0,1]
            if (Double.isNaN(rCuadrado) || rCuadrado < 0) rCuadrado = 0;
            if (rCuadrado > 1) rCuadrado = 1;
            
        } catch (Exception e) {
            rCuadrado = 0;
        }
    }

    /**
     * Devuelve los coeficientes del modelo ajustado.
     * @return Vector [b0, b1, b2, ..., bn] donde b0 es el intercepto
     */
    public double[] getCoeficientes() {
        return coeficientes;
    }

    /**
     * Devuelve el coeficiente de determinación R².
     * @return Valor de R² entre 0 y 1
     */
    public double getRCuadrado() {
        return rCuadrado;
    }

    /**
     * Indica si el cálculo fue exitoso.
     */
    public boolean isCalculadoExitosamente() {
        return calculadoExitosamente;
    }

    /**
     * Evalúa la función ajustada para un conjunto de variables.
     * @param x Vector con los valores de las variables independientes.
     * @return Valor estimado de y.
     */
    public double evaluar(double[] x) {
        if (!calculadoExitosamente) {
            return Double.NaN;
        }
        
        double resultado = coeficientes[0]; // intercepto
        for (int i = 0; i < x.length; i++) {
            resultado += coeficientes[i + 1] * x[i];
        }
        return resultado;
    }
}