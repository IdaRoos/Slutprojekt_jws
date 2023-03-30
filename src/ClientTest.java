import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientTest {
    private final String serverAddress = "localhost"; // serverns adress
    private final int serverPort = 4321; // serverns port

    @Test
    public void testGetAllPersons() throws Exception {
        try (
                Socket socket = new Socket(serverAddress, serverPort); // skapa en ny socket för anslutning till servern
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream()); // läs in data från socket
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream()); // skriv till socket
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader); // läs in data från socket som text
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter) // skriv till socket som text
        ) {
            JSONObject request = new JSONObject();  // skapa en ny JSON-förfrågan
            request.put("httpURL", "persons");
            request.put("httpMethod", "get");

            bufferedWriter.write(request.toJSONString()); // skicka JSON-förfrågan till servern
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String response = bufferedReader.readLine(); // läs in serverns svar

            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response); // tolka serverns svar som en JSON-fil

// kontrollera att statuskoden är 200 OK
            assertEquals("200", jsonResponse.get("httpStatusCode").toString(), "Successful response 200!");
        }
    }

    @Test
    public void testAddNewPerson() throws Exception {
        try (
                Socket socket = new Socket(serverAddress, serverPort);
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)
        ) {
            JSONObject request = new JSONObject();
            request.put("httpURL", "persons");
            request.put("httpMethod", "post");

            JSONObject personData = new JSONObject(); // skapa en ny JSON-fil med personuppgifter
            personData.put("id", 12345);
            personData.put("name", "Test Person");
            personData.put("age", 30);
            personData.put("favoriteColor", "blue");

            JSONObject data = new JSONObject(); // skapa en ny JSON-fil med personuppgifter
            data.put("p1", personData); // lägg till personuppgifterna till JSON-filen
            request.put("data", data); // lägg till JSON-filen med personuppgifter till förfrågan


            bufferedWriter.write(request.toJSONString());
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String response = bufferedReader.readLine();

            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response);
// jämför värdet av "httpStatusCode" nyckeln i JSON-svaret med strängen "200"
            assertEquals("200", jsonResponse.get("httpStatusCode").toString(), "Successful response 200!");
        }
    }
}
