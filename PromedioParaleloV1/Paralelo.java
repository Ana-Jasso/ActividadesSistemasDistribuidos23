import java.util.Random;

public class Paralelo {

    public static void main(String[] args) throws InterruptedException {
        int numThreads = 4;
        int numNumbers = 2000000;
        int[] numbers = new int[numNumbers];
        Random random = new Random();

        // Generar números aleatorios
        for (int i = 0; i < numNumbers; i++) {
            numbers[i] = random.nextInt(100);
        }

        // Crear hilos
        NumberThread[] threads = new NumberThread[numThreads];
        int startIndex = 0;
        int endIndex = numNumbers / numThreads;
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new NumberThread(numbers, startIndex, endIndex);
            startIndex = endIndex;
            endIndex = (i == numThreads - 2) ? numNumbers : endIndex + (numNumbers / numThreads);
        }

        // Iniciar hilos
        for (int i = 0; i < numThreads; i++) {
            threads[i].start();
        }

        // Esperar a que los hilos terminen
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }

        // Calcular promedio
        long start = System.nanoTime();
        int sum = 0;
        for (int i = 0; i < numThreads; i++) {
            sum += threads[i].getSum();
        }
        double average = (double) sum / numNumbers;
        long end = System.nanoTime();

        System.out.println("Promedio: " + average);
        System.out.println("Tiempo de calculo del promedio: " + (end - start) + " ns");
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
