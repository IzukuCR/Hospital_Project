package front.presentation;

import javax.swing.*;

public class Refresher extends Thread {

    private final ThreadListener listener;
    private volatile boolean running = false;
    private final int intervalMs;

    private long counter = 0;

    public Refresher(ThreadListener listener, int intervalMs) {
        super("Refresher-Thread");
        this.listener = listener;
        this.intervalMs = intervalMs;
    }


    @Override
    public void run() {
        running = true;
        System.out.println("[Refresher] Iniciado, intervalo = " + intervalMs + " ms");

        while (running) {
            try {
                Thread.sleep(intervalMs);
                System.out.println("[Refresher] Ciclo #" + counter++);

                // 🔹 Ejecuta la actualización del modelo/UI en el EDT
                SwingUtilities.invokeLater(listener::refresh);

            } catch (InterruptedException e) {
                System.out.println("[Refresher] Interrumpido, cerrando...");
                running = false;
            } catch (Exception e) {
                System.err.println("[Refresher] Error durante el refresco: " + e.getMessage());
            }
        }

        System.out.println("[Refresher] Finalizado correctamente.");
    }
    public void stopRefresher() {
        running = false;
        this.interrupt();
    }
}
