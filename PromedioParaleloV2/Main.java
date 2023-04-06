import java.util.Random;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int[] numThreadsValues = { 1, 2, 3, 4, 5, 10, 20, 30, 50 };
        long[] tiempoDeEjecucion = new long[numThreadsValues.length];
        int numNumbers = 2000000;
        int[] numbers = new int[numNumbers];
        Random random = new Random();
        // Generación de los números aleatorios
        for (int i = 0; i < numNumbers; i++) {
            numbers[i] = random.nextInt(100);
        }
        for (int i = 0; i < numThreadsValues.length; i++) {
            // Creación de los hilos
            int numThreads = numThreadsValues[i];
            NumberThread[] threads = new NumberThread[numThreads];
            int startIndex = 0;
            int endIndex = numNumbers / numThreads;
                        for (int il = 0; il < numThreads; il++) {
                threads[il] = new NumberThread(numbers, startIndex, endIndex);
                startIndex = endIndex;
                endIndex = (il == numThreads - 2) ? numNumbers : endIndex + (numNumbers / numThreads);
            }
            // Iniciación de hilos
            for (int il = 0; il < numThreads; il++) {
                threads[il].start();
            }

            // Esperar a que los hilos terminen
            for (int il = 0; il < numThreads; il++) {
                threads[il].join();
            }

            // Calcular promedio de los números aleatorios
            long start = System.nanoTime();
            int sum = 0;
            for (int il = 0; il < numThreads; il++) {
                sum += threads[il].getSum();
            }
            double average = (double) sum / numNumbers;
            long end = System.nanoTime();

            tiempoDeEjecucion[i] = end - start;

            System.out.println("Hilo: " +  numThreadsValues[i]);
            System.out.println("Tiempo de calculo del hilo "+numThreadsValues[i]+" : " +tiempoDeEjecucion[i]+ " ns");
            System.out.println("Promedio: " + average);

        }
        // Creación del gráfico gráfico "Tiempo de ejecución vs número de hilos".
        XYSeries series = new XYSeries("Tiempo de ejecución vs número de hilos");
        for (int i = 0; i < numThreadsValues.length; i++) {
            series.add(numThreadsValues[i], tiempoDeEjecucion[i]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Tiempo de ejecución vs número de hilos",
                "Número de hilos",
                "Tiempo de ejecución (ns)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame frame = new JFrame("Gráfico");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private static class NumberThread extends Thread {
        private final int[] numbers;
        private final int startIndex;
        private final int endIndex;
        private int sum;

        public NumberThread(int[] numbers, int startIndex, int endIndex) {
            this.numbers = numbers;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public int getSum() {
            return sum;
        }

        @Override
        public void run() {
            for (int i = startIndex; i < endIndex; i++) {
                sum += numbers[i];
            }
        }
    }
}
