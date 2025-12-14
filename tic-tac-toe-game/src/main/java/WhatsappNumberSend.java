import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WhatsappNumberSend {

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        // Usage and defaults
        // args[0] = path to CSV (default: numbers.csv in CWD)
        // args[1] = bearer token (or use env var WHATSAPP_TOKEN)
        String csvPath = "/Users/poojanthumar/Documents/Code/LLD/tic-tac-toe-game/src/main/resources/number.csv";
        String token = "EAAJDdAqS62QBQNz2TPGZBPtdDYIXdH2xuYweX3fzC49tcvAffCcZB5ZCpQrZBirUVvFuxvpS61934hi1WKkoEenCM8uF759X8uDlrDcl4C3thisbdFJ2Bq08WBf6RHX0gmdLTS0nZCbDzCSw35GPulGCP42PEiaSYcdOezUClhR6QmufprrZB86QSvdkKCvwZDZD";

        if (token == null || token.isBlank()) {
            System.err.println("ERROR: No bearer token provided. Pass as 2nd arg or set WHATSAPP_TOKEN environment variable.");
            System.exit(1);
        }

        // Config - update if needed
        String graphVersion = "v24.0";
        String phoneNumberId = "854816067723420"; // as in your example
        String endpoint = String.format("https://graph.facebook.com/%s/%s/messages", graphVersion, phoneNumberId);

        List<String> numbers;
        try {
            numbers = readNumbersFromCsv(csvPath);
        } catch (IOException e) {
            System.err.println("Failed to read CSV: " + e.getMessage());
            return;
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        for (String raw : numbers) {
            String normalized = normalizeNumber(raw);
            if (normalized == null) {
                System.out.println("Skipping invalid number: " + raw);
                continue;
            }

            String json = buildJsonBody(normalized);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(20))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            try {
                HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("To: " + normalized + " -> " + resp.statusCode());
                System.out.println(resp.body());
                // small pause to avoid accidental rate limits
                Thread.sleep(200);
            } catch (IOException | InterruptedException e) {
                System.err.println("Request failed for " + normalized + ": " + e.getMessage());
                // if interrupted, restore interrupt status
                if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            }
        }
    }

    // Read CSV file and return list of tokens that look like phone numbers
    private static List<String> readNumbersFromCsv(String path) throws IOException {
        File f = new File(path);
        if (!f.exists() || !f.isFile()) throw new IOException("File not found: " + path);

        List<String> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;
                // Split by comma for CSV values and by whitespace; handle quoted values
                String[] parts = line.split(",");
                for (String p : parts) {
                    String token = p.trim();
                    // remove surrounding quotes
                    if (token.startsWith("\"") && token.endsWith("\"") && token.length() >= 2) {
                        token = token.substring(1, token.length() - 1);
                    }
                    if (!token.isEmpty()) result.add(token);
                }
            }
        }
        return result;
    }

    // Normalize phone numbers by removing non-digit characters and leading plus
    private static String normalizeNumber(String s) {
        if (s == null) return null;
        // Remove all non-digit characters
        String digits = s.replaceAll("\\D+", "");
        if (digits.isEmpty()) return null;
        // Basic validation: require at least 8 digits (very permissive)
        if (digits.length() < 8) return null;
        return digits;
    }

    private static String buildJsonBody(String toNumber) {
        // Template name and language can be parameterized if needed
        String templateName = "gcc_schedule_announcement";
        String languageCode = "en";

        return "{" +
                "\"messaging_product\": \"whatsapp\"," +
                "\"recipient_type\": \"individual\"," +
                "\"to\": \"" + toNumber + "\"," +
                "\"type\": \"template\"," +
                "\"template\": {" +
                "\"name\": \"" + templateName + "\"," +
                "\"language\": { \"code\": \"" + languageCode + "\" }" +
                "}"
                + "}";
    }
}