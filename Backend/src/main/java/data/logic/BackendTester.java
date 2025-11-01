package data.logic;

import logic.Protocol;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

public class BackendTester {

    public static void main(String[] args) {
        String sessionId = "tester-session-" + UUID.randomUUID();

        try {
            System.out.println("=== BACKEND TESTER ===");

            // --- 1) Conexión SÍNCRONA ---
            Socket syncSocket = new Socket(Protocol.SERVER, Protocol.PORT_SYNC);
            ObjectOutputStream syncOs = new ObjectOutputStream(syncSocket.getOutputStream());
            syncOs.flush();
            ObjectInputStream syncIs = new ObjectInputStream(syncSocket.getInputStream());

            // --- 2) Enviar SessionID ---
            syncOs.writeObject(sessionId);
            syncOs.flush();
            String serverSession = (String) syncIs.readObject();
            System.out.println("[SYNC] Conectado con SessionID: " + serverSession);

            // --- 3) Conexión ASÍNCRONA ---
            Socket asyncSocket = new Socket(Protocol.SERVER, Protocol.PORT_ASYNC);
            ObjectOutputStream asyncOs = new ObjectOutputStream(asyncSocket.getOutputStream());
            asyncOs.flush();
            ObjectInputStream asyncIs = new ObjectInputStream(asyncSocket.getInputStream());
            System.out.println("[ASYNC] Conexión asíncrona establecida correctamente.");

            // --- 4) Solicitudes GET_ALL ---
            int[] requests = {
                    Protocol.DOCTOR_GET_ALL,
                    Protocol.PHARMACIST_GET_ALL,
                    Protocol.PATIENT_GET_ALL,
                    Protocol.MEDICINE_GET_ALL,
                    Protocol.PRESCRIPTION_GET_ALL
            };

            String[] labels = {
                    "Doctores", "Farmacéuticos", "Pacientes", "Medicamentos", "Prescripciones"
            };

            for (int i = 0; i < requests.length; i++) {
                int code = requests[i];
                String label = labels[i];

                System.out.println("\n[SYNC] Solicitando lista de " + label + "...");
                syncOs.writeInt(code);
                syncOs.flush();

                int status = syncIs.readInt();
                if (status == Protocol.ERROR_NO_ERROR) {
                    Object obj = syncIs.readObject();
                    if (obj instanceof List<?> list) {
                        System.out.println("✅ " + label + " recibidos: " + list.size());
                        if (!list.isEmpty()) {
                            System.out.println("Primer elemento: " + list.get(0));
                        }
                    } else {
                        System.out.println("⚠️ El servidor devolvió un tipo inesperado: " + obj.getClass().getName());
                    }
                } else {
                    System.out.println("❌ Error al obtener " + label);
                }
            }

            // --- 5) Cerrar sesión ---
            System.out.println("\n[SYNC] Enviando DISCONNECT...");
            syncOs.writeInt(Protocol.DISCONNECT);
            syncOs.flush();

            // --- 6) Cierre de recursos ---
            asyncIs.close();
            asyncOs.close();
            asyncSocket.close();
            syncIs.close();
            syncOs.close();
            syncSocket.close();

            System.out.println("\n=== BACKEND TESTER FINALIZADO ===");

        } catch (Exception e) {
            System.err.println("❌ Error en tester: " + e.getMessage());
            e.printStackTrace();
        }
    }
}