import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Client {
    public static void main(String[] args) {

        // Initiera objekten som "something"
        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {

            socket = new Socket("localhost", 4321);

            // Tillskriv objekten/ Initiera Reader och Writer och koppla dem till socket
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);


            while (true) {

                System.out.println("Ange en siffra för att välja alternativ i menyn:");
                JSONParser parser = new JSONParser();
                // Skriv ut en meny för användaren
                System.out.println("1. Hämta data om alla personer");
                System.out.println("2. Lägg till ny person");
                System.out.println("3. Hämta data om specifik person");
                System.out.println("4. Avsluta");
                String message = userMenu();

                if ("quit".equals(message)) {
                    break;
                }


                // Skicka meddelande till server
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();


                // Hämtar response från server och skapar ett JSON objekt
                String response = bufferedReader.readLine();
                JSONObject serverResponse = (JSONObject) parser.parse(response);

                //Kollar om response lyckas
                if ("200".equals(serverResponse.get("httpStatusCode").toString())) {

                } else if ("404".equals(serverResponse.get("httpStatusCode").toString())) {
                    System.out.println(serverResponse.toJSONString());
                }

            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static String userMenu() {
        //  Initiera Scanner för att skriva i konsol
        Scanner scan = new Scanner(System.in);
        System.out.print("Skriv in ditt menyval:");

        String val = scan.nextLine();

        //Bearbeta användarens val
        switch (val) {
            case "1": {
                //Skapa JSON objekt för att hämta data om alla personer.
                JSONObject jsonReturn = new JSONObject();
                jsonReturn.put("httpURL", "persons");
                jsonReturn.put("httpMethod", "get");


                //Stringifiera objektet och returnera det.
                return jsonReturn.toJSONString();

            }
            case "2": {
                // Skapa JSON-objekt för att lägga till en ny person.
                JSONObject jsonReturn = new JSONObject();
                jsonReturn.put("httpURL", "persons");
                jsonReturn.put("httpMethod", "post");

                // Be användaren mata in persondata.
                System.out.println("Skriv in personens id:");
                int id = scan.nextInt();
                scan.nextLine();
                System.out.println("Skriv in personens namn:");
                String name = scan.nextLine();
                System.out.println("Skriv in personens ålder:");
                int age = scan.nextInt();
                scan.nextLine(); // Sätt scanner till nästa rad.
                System.out.println("Skriv in personens favoritfärg:");
                String favColor = scan.nextLine();


                // Skapa JSON-objekt för persondata.
                JSONObject personData = new JSONObject();
                personData.put("id", id);
                personData.put("name", name);
                personData.put("age", age);
                personData.put("favoriteColor", favColor);


                // Lägg till persondatan till JSON-objektet för POST-requesten.
                JSONObject data = new JSONObject();
                data.put("p1", personData);
                jsonReturn.put("data", data);


                // Skriv ut JSON-objektet.
                System.out.println("Personen har lagts till.");


                // Returnera JSON-objektet.
                return jsonReturn.toJSONString();

            }
            case "3": {
                // SKicka GET request till servern för att få en specifik person
                System.out.print("Enter person id: ");
                String getId = scan.nextLine();
                JSONObject jsonReturn = new JSONObject();

                jsonReturn.put("httpURL", "persons/" + getId); // append person id to URL
                jsonReturn.put("httpMethod", "get");

                return jsonReturn.toJSONString();

            }
            case "4": {
                System.out.println("Exiting...");
                return "quit";
            }
            default:
                System.out.println("Invalid input.");
                return "";
        }
    }


}