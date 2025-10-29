// java
package data.logic;

import logic.Protocol;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.Socket;
import java.util.List;

public class BackendTester {
    private static final String HOST = "localhost";
    private static final int PORT = Protocol.PORT;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.flush();
            System.out.println("Connected to server successfully");

            // Test each list via server
            testList(out, in, Protocol.MEDICINE_GET_ALL, "Medicines");
            testList(out, in, Protocol.PATIENT_GET_ALL, "Patients");
            testList(out, in, Protocol.PRESCRIPTION_GET_ALL, "Prescriptions");

            System.out.println("\nClosing connection...");
            System.out.println("Test completed");

        } catch (Exception e) {
            System.err.println("Critical error: " + e);
            e.printStackTrace();
        }
    }

    private static void testList(ObjectOutputStream out, ObjectInputStream in, int requestCode, String label) {
        try {
            System.out.println("\nTesting " + label + " list...");
            System.out.println("Sending request code: " + requestCode);
            out.writeInt(requestCode);
            out.flush();
            System.out.println("Request sent, waiting for response...");

            int status = in.readInt();
            System.out.println("Received status code: " + status);

            if (status == Protocol.OK) {
                try {
                    Object response = in.readObject();
                    System.out.println("Response received. Type: " +
                            (response != null ? response.getClass().getName() : "null"));

                    if (response instanceof List<?>) {
                        List<?> list = (List<?>) response;
                        System.out.println(label + " list size: " + list.size());
                        if (!list.isEmpty()) {
                            System.out.println("First item class: " + list.get(0).getClass().getName());
                            System.out.println("First item toString: " + list.get(0).toString());
                        }
                    } else {
                        System.out.println("Response is not a List. Actual type: " +
                                (response != null ? response.getClass().getName() : "null"));
                    }
                } catch (OptionalDataException ode) {
                    System.out.println("OptionalDataException details:");
                    System.out.println("- EOF: " + ode.eof);
                    System.out.println("- Length: " + ode.length);
                }
            } else {
                System.out.println("Server returned error status: " + status);
                try {
                    Object err = in.readObject();
                    System.out.println("Error payload type: " + (err != null ? err.getClass().getName() : "null"));
                    System.out.println("Error message/payload: " + err);
                } catch (Exception e) {
                    System.out.println("No readable error message available");
                }
            }
        } catch (Exception e) {
            System.err.println("Error testing " + label.toLowerCase() + ": " + e);
            e.printStackTrace();
        }
    }
}
