import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class ShamirsSecret {

    // Method to decode a value from a given base
    public static int decodeValue(String value, int base) {
        return Integer.parseInt(value, base);
    }

    // Lagrange Interpolation Method to find the constant term
    public static double lagrangeInterpolation(List<int[]> points) {
        int n = points.size();
        double constantTerm = 0;

        for (int i = 0; i < n; i++) {
            int xi = points.get(i)[0];
            int yi = points.get(i)[1];
            double li = 1.0;

            for (int j = 0; j < n; j++) {
                if (i != j) {
                    li *= (0.0 - points.get(j)[0]) / (xi - points.get(j)[0]);
                }
            }
            constantTerm += li * yi;
        }
        return Math.round(constantTerm); // Round to the nearest integer
    }

    // Method to find the secret constant term from the JSON file
    public static int findSecret(String filename) {
        try {
            // Read and parse JSON file
            String content = new String(Files.readAllBytes(Paths.get(filename)));
            JSONObject data = new JSONObject(content);

            JSONObject keys = data.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");

            // Extract and decode the points
            List<int[]> points = new ArrayList<>();
            for (String key : data.keySet()) {
                if (!key.equals("keys")) {
                    JSONObject root = data.getJSONObject(key);
                    int x = Integer.parseInt(key);
                    int y = decodeValue(root.getString("value"), root.getInt("base"));
                    points.add(new int[]{x, y});
                }
            }

            // Sort the points based on x for consistency
            points.sort((a, b) -> Integer.compare(a[0], b[0]));

            // Use only the first k points for interpolation
            List<int[]> selectedPoints = points.subList(0, k);

            // Find the constant term using Lagrange Interpolation
            return (int) lagrangeInterpolation(selectedPoints);

        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 if an error occurs
        }
    }

    public static void main(String[] args) {
        // Input JSON file paths
        String testCase1 = "testcase1.json";
        String testCase2 = "testcase2.json";

        // Calculate secrets for both test cases
        int secret1 = findSecret(testCase1);
        int secret2 = findSecret(testCase2);

        // Print results
        System.out.println("Secret for Test Case 1: " + secret1);
        System.out.println("Secret for Test Case 2: " + secret2);
    }
}
