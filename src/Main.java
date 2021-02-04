import factory.Result;
import utils.BusinessFlow;
import com.fasterxml.jackson.databind.ObjectMapper;
import input.Input;

import java.io.File;
import java.util.ConcurrentModificationException;

/**
 * Entry point to the simulation
 */
public final class Main {

    private Main() { }

    /**
     * Main function which reads the input file and starts simulation
     *
     * @param args input and output files
     * @throws Exception might error when reading/writing/opening files, parsing JSON
     */
    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("The expected number of arguments is 2,"
                    + " not " + args.length);
            return;
        }

        ObjectMapper collector = new ObjectMapper();
        Input center = collector.readValue(new File(args[0]), Input.class);
        BusinessFlow game = new BusinessFlow(center.getInitialData(),
                center.getMonthlyUpdates());

        try {
            game.play();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        Result result = new Result(game.getConsumers(), game.getDistributors(),
                game.getProducers());

        collector.writerWithDefaultPrettyPrinter().writeValue(new File(args[1]),
                result);
    }
}
