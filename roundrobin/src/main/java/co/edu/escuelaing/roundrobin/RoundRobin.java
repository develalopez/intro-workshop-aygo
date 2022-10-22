package co.edu.escuelaing.roundrobin;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;

public class RoundRobin {

    private static URL requestURL;

    public static void main(String[] args) {
        port(getPort());

        get("messages", (request, response) -> {
            String reqURL = "http://logservice:6000/messages";
            requestURL = new URL(reqURL);
            try {
                HttpURLConnection con = (HttpURLConnection) requestURL.openConnection();
                con.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer responseBuffer = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    responseBuffer.append(inputLine);
                }
                return responseBuffer.toString();
            } catch (Exception ex) {
                return ex.getMessage();
            }
        });

        post("messages", (request, response) -> {
            requestURL = new URL("http://logservice:6000/messages");
            HttpURLConnection con = (HttpURLConnection) requestURL.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(request.body().getBytes());
            os.flush();
            os.close();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String input, confirmation = "";
            while ((input = in.readLine()) != null) {
                confirmation += input;
            }
            in.close();
            return confirmation;
        });
    }

    private static Integer getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
