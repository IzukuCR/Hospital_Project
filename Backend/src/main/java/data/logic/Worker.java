package data.logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

import logic.*;

public class Worker {
    Server srv;
    Socket s;
    Service service;
    ObjectOutputStream os;
    ObjectInputStream is;

    public Worker(Server srv, Socket s, Service service) {
        try {
            this.srv = srv;
            this.s = s;
            os = new ObjectOutputStream(s.getOutputStream());
            is = new ObjectInputStream(s.getInputStream());
            this.service = service;
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    boolean running;

    public void start() {
        try {
            System.out.println("Worker is now listening for requests...");
            Thread t = new Thread(this::listen);
            running = true;
            t.start();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void stop() {
        running = false;
        System.out.println("Connection closed.");
    }

    public void listen() {
        int method;
        while (running) {
            try {
                method = is.readInt();
                System.out.println("Operation: " + method);

                switch (method) {

                    // -------------------- DOCTOR --------------------
                    case Protocol.DOCTOR_CREATE:
                        try {
                            Doctor d = (Doctor) is.readObject();
                            Doctor created = service.doctor().create(d);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(created);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_READ_BY_ID:
                        try {
                            String id = (String) is.readObject();
                            Doctor doc = service.doctor().searchByID(id);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(doc);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_UPDATE:
                        try {
                            Doctor d = (Doctor) is.readObject();
                            Doctor updated = service.doctor().update(d);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(updated);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_DELETE:
                        try {
                            Doctor d = (Doctor) is.readObject();
                            boolean ok = service.doctor().delete(d);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(ok);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_SEARCH_BY_NAME:
                        try {
                            String name = (String) is.readObject();
                            List<Doctor> list = service.doctor().searchByName(name);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_GET_ALL:
                        try {
                            List<Doctor> list = service.doctor().getDoctors();
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.DOCTOR_VALIDATE_LOGIN:
                        try {
                            Doctor d = (Doctor) is.readObject();
                            Doctor result = service.doctor().validateLogin(d);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(result);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;


                    // -------------------- PHARMACIST --------------------
                    case Protocol.PHARMACIST_CREATE:
                        try {
                            Pharmacist ph = (Pharmacist) is.readObject();
                            Pharmacist created = service.pharmacist().create(ph);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(created);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_READ_BY_ID:
                        try {
                            String id = (String) is.readObject();
                            Pharmacist ph = service.pharmacist().readById(id);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(ph);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_UPDATE:
                        try {
                            Pharmacist ph = (Pharmacist) is.readObject();
                            Pharmacist updated = service.pharmacist().update(ph);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(updated);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_DELETE:
                        try {
                            Pharmacist ph = (Pharmacist) is.readObject();
                            boolean ok = service.pharmacist().delete(ph);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(ok);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_SEARCH_BY_NAME:
                        try {
                            String name = (String) is.readObject();
                            List<Pharmacist> list = service.pharmacist().searchByName(name);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_GET_ALL:
                        try {
                            List<Pharmacist> list = service.pharmacist().getPharmacists();
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PHARMACIST_VALIDATE_LOGIN:
                        try {
                            Pharmacist ph = (Pharmacist) is.readObject();
                            Pharmacist result = service.pharmacist().validateLogin(ph);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(result);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;


                    // -------------------- PATIENT --------------------
                    case Protocol.PATIENT_CREATE:
                        try {
                            Patient p = (Patient) is.readObject();
                            Patient created = service.patient().create(p);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(created);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PATIENT_READ_BY_ID:
                        try {
                            String id = (String) is.readObject();
                            Patient p = service.patient().readById(id);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(p);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PATIENT_UPDATE:
                        try {
                            Patient p = (Patient) is.readObject();
                            Patient updated = service.patient().update(p);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(updated);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PATIENT_DELETE:
                        try {
                            Patient p = (Patient) is.readObject();
                            boolean ok = service.patient().delete(p);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(ok);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PATIENT_SEARCH_BY_NAME:
                        try {
                            String name = (String) is.readObject();
                            List<Patient> list = service.patient().searchByName(name);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PATIENT_GET_ALL:
                        try {
                            List<Patient> list = service.patient().getPatients();
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;


                    // -------------------- MEDICINE --------------------
                    case Protocol.MEDICINE_CREATE:
                        try {
                            Medicine m = (Medicine) is.readObject();
                            Medicine created = service.medicine().create(m);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(created);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.MEDICINE_READ_BY_CODE:
                        try {
                            String code = (String) is.readObject();
                            Medicine m = service.medicine().readByCode(code);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(m);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.MEDICINE_UPDATE:
                        try {
                            Medicine m = (Medicine) is.readObject();
                            Medicine updated = service.medicine().update(m);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(updated);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.MEDICINE_DELETE:
                        try {
                            String code = (String) is.readObject();
                            boolean ok = service.medicine().delete(code);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(ok);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.MEDICINE_GET_ALL:
                        try {
                            List<Medicine> list = service.medicine().getMedicines();
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.MEDICINE_SEARCH_BY_NAME:
                        try {
                            String name = (String) is.readObject();
                            List<Medicine> list = service.medicine().searchByName(name);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;


                    // -------------------- PRESCRIPTION --------------------
                    case Protocol.PRESCRIPTION_CREATE:
                        try {
                            Prescription pr = (Prescription) is.readObject();
                            Prescription created = service.prescription().create(pr);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(created);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_READ_BY_ID:
                        try {
                            String id = (String) is.readObject();
                            Prescription pr = service.prescription().readById(id);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(pr);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_UPDATE:
                        try {
                            Prescription pr = (Prescription) is.readObject();
                            Prescription updated = service.prescription().update(pr);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(updated);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_DELETE:
                        try {
                            String id = (String) is.readObject();
                            boolean ok = service.prescription().delete(id);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(ok);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_GET_ALL:
                        try {
                            List<Prescription> list = service.prescription().getPrescriptions();
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_BY_PATIENT_ID:
                        try {
                            String patientId = (String) is.readObject();
                            List<Prescription> list = service.prescription().getPrescriptionsByPatientID(patientId);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_BY_PATIENT_NAME:
                        try {
                            String name = (String) is.readObject();
                            List<Prescription> list = service.prescription().getPrescriptionsByPatientName(name);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_BY_DOCTOR:
                        try {
                            String doctorId = (String) is.readObject();
                            List<Prescription> list = service.prescription().getPrescriptionsByDoctor(doctorId);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    case Protocol.PRESCRIPTION_BY_DATE:
                        try {
                            Object[] params = (Object[]) is.readObject();
                            LocalDate date = (LocalDate) params[0];
                            String patientId = (String) params[1];
                            List<Prescription> list = service.prescription().getPrescriptionsByDate(date, patientId);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(list);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;


                    // -------------------- LOGIN --------------------
                    case Protocol.USER_VALIDATE:
                        try {
                            String[] creds = (String[]) is.readObject();
                            String userType = service.log().validateUserType(creds[0], creds[1]);
                            os.writeInt(Protocol.ERROR_NO_ERROR);
                            os.writeObject(userType);
                        } catch (Exception ex) { os.writeInt(Protocol.ERROR_ERROR); }
                        break;

                    // -------------------- DISCONNECT --------------------
                    case Protocol.DISCONNECT:
                        stop();
                        srv.remove(this);
                        break;

                    default:
                        os.writeInt(Protocol.ERROR_UNKNOWN_METHOD);
                        break;
                }

                os.flush();

            } catch (IOException e) {
                stop();
            }
        }
    }
}

