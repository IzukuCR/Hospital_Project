package data.logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

import logic.*;

public class Worker extends Thread {

    private final Server server;
    private String sessionId;
    private final Socket syncSocket, asyncSocket;
    private final ObjectOutputStream syncOs, asyncOs;
    private final ObjectInputStream syncIs, asyncIs;
    private volatile boolean running = true;
    private Service service = Service.instance();

    public Worker(Server server, String sessionId,
                  Socket syncSocket, ObjectOutputStream syncOs, ObjectInputStream syncIs,
                  Socket asyncSocket, ObjectOutputStream asyncOs, ObjectInputStream asyncIs) {
        this.server = server;
        this.sessionId = sessionId;
        this.syncSocket = syncSocket;
        this.syncOs = syncOs;
        this.syncIs = syncIs;
        this.asyncSocket = asyncSocket;
        this.asyncOs = asyncOs;
        this.asyncIs = asyncIs;
    }

    @Override
    public void run() {
        System.out.println("Worker started for session: " + sessionId);
        Thread asyncThread = new Thread(this::listenAsync);
        asyncThread.start();
        listenSync();
    }

    private void listenSync() {
        try {
            while (running) {
                int op = syncIs.readInt();
                System.out.println("SYNC op: " + op);

                if (op == Protocol.DISCONNECT) {
                    stopWorker();
                    break;
                } else if (op == Protocol.USER_VALIDATE) {
                    String[] creds = (String[]) syncIs.readObject();
                    System.out.println("Login from: " + creds[0]);
                    syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                    syncOs.writeObject("OK");
                } else {
                    syncOs.writeInt(Protocol.ERROR_UNKNOWN_METHOD);
                }
                syncOs.flush();
            }
        } catch (Exception e) {
            System.err.println("SYNC error (" + sessionId + "): " + e.getMessage());
        } finally {
            stopWorker();
        }
    }

    private void listenAsync() {
        try {
            while (running) {
                Object obj = asyncIs.readObject();
                if (obj instanceof Message msg) {
                    System.out.println("ASYNC message from " + msg.getSender() + " to " + msg.getReceiver());
                    server.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            System.err.println("Async stream closed for " + sessionId);
        }
    }

    public void sendAsync(Message msg) {
        try {
            asyncOs.writeObject(msg);
            asyncOs.flush();
            System.out.println("Sent async message to " + msg.getReceiver());
        } catch (IOException e) {
            System.err.println("Error sending message to " + msg.getReceiver() + ": " + e.getMessage());
        }
    }

    public void stopWorker() {
        running = false;
        server.remove(sessionId);
        try { if (syncOs != null) syncOs.close(); } catch (IOException ignored) {}
        try { if (syncIs != null) syncIs.close(); } catch (IOException ignored) {}
        try { if (asyncOs != null) asyncOs.close(); } catch (IOException ignored) {}
        try { if (asyncIs != null) asyncIs.close(); } catch (IOException ignored) {}
        try { if (syncSocket != null) syncSocket.close(); } catch (IOException ignored) {}
        try { if (asyncSocket != null) asyncSocket.close(); } catch (IOException ignored) {}
        System.out.println("Worker stopped: " + sessionId);
    }

    private void writeCaseError(Exception ex) {
        try {
            syncOs.writeInt(Protocol.ERROR_ERROR);
            syncOs.writeObject("Error: " + ex.getMessage());
            syncOs.flush();
        } catch (IOException ignored) {}
    }

    public void listen() {
        int method;
        while (running) {
            try {
                method = syncIs.readInt();
                System.out.println("Operation: " + method);

                switch (method) {

                    // -------------------- DOCTOR --------------------
                    case Protocol.DOCTOR_CREATE:
                        try {
                            Doctor d = (Doctor) syncIs.readObject();
                            Doctor created = service.doctor().create(d);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(created);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_READ_BY_ID:
                        try {
                            String id = (String) syncIs.readObject();
                            Doctor doc = service.doctor().searchByID(id);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(doc);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_UPDATE:
                        try {
                            Doctor d = (Doctor) syncIs.readObject();
                            Doctor updated = service.doctor().update(d);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(updated);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_DELETE:
                        try {
                            Doctor d = (Doctor) syncIs.readObject();
                            boolean ok = service.doctor().delete(d);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(ok);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_SEARCH_BY_NAME:
                        try {
                            String name = (String) syncIs.readObject();
                            List<Doctor> list = service.doctor().searchByName(name);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_GET_ALL:
                        System.out.println("Handling request: " + method);
                        try {
                            List<Doctor> list = service.doctor().getDoctors();
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_VALIDATE_LOGIN:
                        try {
                            Doctor d = (Doctor) syncIs.readObject();
                            Doctor result = service.doctor().validateLogin(d);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(result);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;


                    // -------------------- PHARMACIST --------------------
                    case Protocol.PHARMACIST_CREATE:
                        try {
                            Pharmacist ph = (Pharmacist) syncIs.readObject();
                            Pharmacist created = service.pharmacist().create(ph);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(created);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_READ_BY_ID:
                        try {
                            String id = (String) syncIs.readObject();
                            Pharmacist ph = service.pharmacist().readById(id);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(ph);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_UPDATE:
                        try {
                            Pharmacist ph = (Pharmacist) syncIs.readObject();
                            Pharmacist updated = service.pharmacist().update(ph);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(updated);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_DELETE:
                        try {
                            Pharmacist ph = (Pharmacist) syncIs.readObject();
                            boolean ok = service.pharmacist().delete(ph);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(ok);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_SEARCH_BY_NAME:
                        try {
                            String name = (String) syncIs.readObject();
                            List<Pharmacist> list = service.pharmacist().searchByName(name);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_GET_ALL:
                        System.out.println("Handling request: " + method);
                        try {
                            List<Pharmacist> list = service.pharmacist().getPharmacists();
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_VALIDATE_LOGIN:
                        try {
                            Pharmacist ph = (Pharmacist) syncIs.readObject();
                            Pharmacist result = service.pharmacist().validateLogin(ph);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(result);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;


                    // -------------------- PATIENT --------------------
                    case Protocol.PATIENT_CREATE:
                        try {
                            Patient p = (Patient) syncIs.readObject();
                            Patient created = service.patient().create(p);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(created);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PATIENT_READ_BY_ID:
                        try {
                            String id = (String) syncIs.readObject();
                            Patient p = service.patient().readById(id);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(p);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PATIENT_UPDATE:
                        try {
                            Patient p = (Patient) syncIs.readObject();
                            Patient updated = service.patient().update(p);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(updated);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PATIENT_DELETE:
                        try {
                            Patient p = (Patient) syncIs.readObject();
                            boolean ok = service.patient().delete(p);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(ok);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PATIENT_SEARCH_BY_NAME:
                        try {
                            String name = (String) syncIs.readObject();
                            List<Patient> list = service.patient().searchByName(name);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PATIENT_GET_ALL:
                        System.out.println("Handling request: " + method);
                        try {
                            List<Patient> list = service.patient().getPatients();
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;


                    // -------------------- MEDICINE --------------------
                    case Protocol.MEDICINE_CREATE:
                        try {
                            Medicine m = (Medicine) syncIs.readObject();
                            Medicine created = service.medicine().create(m);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(created);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.MEDICINE_READ_BY_CODE:
                        try {
                            String code = (String) syncIs.readObject();
                            Medicine m = service.medicine().readByCode(code);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(m);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.MEDICINE_UPDATE:
                        try {
                            Medicine m = (Medicine) syncIs.readObject();
                            Medicine updated = service.medicine().update(m);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(updated);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.MEDICINE_DELETE:
                        try {
                            String code = (String) syncIs.readObject();
                            boolean ok = service.medicine().delete(code);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(ok);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.MEDICINE_GET_ALL:
                        System.out.println("Handling request: " + method);
                        try {
                            List<Medicine> list = service.medicine().getMedicines();
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.MEDICINE_SEARCH_BY_NAME:
                        try {
                            String name = (String) syncIs.readObject();
                            List<Medicine> list = service.medicine().searchByName(name);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;


                    // -------------------- PRESCRIPTION --------------------
                    case Protocol.PRESCRIPTION_CREATE:
                        try {
                            Prescription pr = (Prescription) syncIs.readObject();
                            Prescription created = service.prescription().create(pr);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(created);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_READ_BY_ID:
                        try {
                            String id = (String) syncIs.readObject();
                            Prescription pr = service.prescription().readById(id);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(pr);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_UPDATE:
                        try {
                            Prescription pr = (Prescription) syncIs.readObject();
                            Prescription updated = service.prescription().update(pr);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(updated);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_DELETE:
                        try {
                            String id = (String) syncIs.readObject();
                            boolean ok = service.prescription().delete(id);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(ok);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_GET_ALL:
                        System.out.println("Handling request: " + method);
                        try {
                            List<Prescription> list = service.prescription().getPrescriptions();
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_BY_PATIENT_ID:
                        try {
                            String patientId = (String) syncIs.readObject();
                            List<Prescription> list = service.prescription().getPrescriptionsByPatientID(patientId);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_BY_PATIENT_NAME:
                        try {
                            String name = (String) syncIs.readObject();
                            List<Prescription> list = service.prescription().getPrescriptionsByPatientName(name);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_BY_DOCTOR:
                        try {
                            String doctorId = (String) syncIs.readObject();
                            List<Prescription> list = service.prescription().getPrescriptionsByDoctor(doctorId);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_BY_DATE:
                        try {
                            Object[] params = (Object[]) syncIs.readObject();
                            LocalDate date = (LocalDate) params[0];
                            String patientId = (String) params[1];
                            List<Prescription> list = service.prescription().getPrescriptionsByDate(date, patientId);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(list);
                        } catch (Exception ex) { syncOs.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    // -------------------- DISCONNECT --------------------
                    case Protocol.DISCONNECT:
                        stopWorker();
                        server.remove(sessionId);
                        break;

                    case Protocol.USER_VALIDATE:
                        try {
                            String[] creds = (String[]) syncIs.readObject();
                            String userType = service.log().validateUserType(creds[0], creds[1]);
                            syncOs.writeInt(Protocol.ERROR_NO_ERROR);
                            syncOs.writeObject(userType);

                            // Actualiza el sessionId con el usuario logueado
                            if (userType != null) {
                                this.sessionId = creds[0];
                                Server.getActiveUsers().put(this.sessionId, this);
                                System.out.println("Session updated for user: " + this.sessionId);
                            }
                        } catch (Exception ex) {
                            syncOs.writeInt(Protocol.ERROR_ERROR);
                        }
                        break;

                    default:
                        syncOs.writeInt(Protocol.ERROR_UNKNOWN_METHOD);
                        break;
                }

                syncOs.flush();

            } catch (IOException e) {
                System.err.println("I/O error in worker " + sessionId + ": " + e.getMessage());
                stopWorker();
            } catch (Exception e) {
                System.err.println("Unexpected error in worker " + sessionId + ": " + e.getMessage());
                stopWorker();
            }
        }
    }
}

