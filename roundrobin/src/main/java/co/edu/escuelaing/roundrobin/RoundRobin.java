package co.edu.escuelaing.roundrobin;

import static spark.Spark.get;
import static spark.Spark.port;

import java.util.ArrayList;
import java.util.ListIterator;

public class RoundRobin {

    private static ArrayList<Integer> ports = new ArrayList<Integer>();
    private static Integer currentPort = 0;
    private static ListIterator<Integer> it;

    public static void main( String[] args ) {
        port(getPort());
        ports.add(35001);
        ports.add(35002);
        ports.add(35003);
        it = ports.listIterator();
        get("messages", (request, response) -> {
            if (currentPort != 35003) {
                currentPort = it.next();
            } else {
                it = ports.listIterator();
                currentPort = it.next();
            }
            return "We are using port " + currentPort.toString();
        });
    }

    private static Integer getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
