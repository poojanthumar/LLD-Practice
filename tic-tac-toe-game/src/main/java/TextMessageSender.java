import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class TextMessageSender {

    public static void main(String[] args) {
        // args: [csvPath] [token] [messageBody]
        String defaultResPath = "/Users/poojanthumar/Documents/Code/LLD/tic-tac-toe-game/src/main/resources";

        String csvPath = "/Users/poojanthumar/Downloads/Giri/allMarketingReponse.csv";

        // Use the same hardcoded token as in WhatsappNumberSend
        String token = "EAAJDdAqS62QBQNz2TPGZBPtdDYIXdH2xuYweX3fzC49tcvAffCcZB5ZCpQrZBirUVvFuxvpS61934hi1WKkoEenCM8uF759X8uDlrDcl4C3thisbdFJ2Bq08WBf6RHX0gmdLTS0nZCbDzCSw35GPulGCP42PEiaSYcdOezUClhR6QmufprrZB86QSvdkKCvwZDZD";

        if (token == null || token.isBlank()) {
            System.err.println("ERROR: No bearer token provided. Pass as 2nd arg or set WHATSAPP_TOKEN environment variable.");
            System.exit(1);
        }

        String messageBody = args.length >= 3 ? args[2] : "*Greetings from Giri Camps Manali!*\\n\\nDedicated WhatsApp groups have been created for each adventure trek.\\n\\nPlease express your interest by sending a WhatsApp message to *+91 83475 00255*, mentioning the trek you’re interested in.";

        String graphVersion = "v24.0";
        String phoneNumberId = "854816067723420";
        String endpoint = String.format("https://graph.facebook.com/%s/%s/messages", graphVersion, phoneNumberId);

        List<String> numbers;
        try {
            numbers = readNumbersFromResponsesCsv(csvPath);
        } catch (IOException e) {
            System.err.println("Failed to read CSV: " + e.getMessage());
            return;
        }

        long epoch = Instant.now().getEpochSecond();
        String outFilename = String.format("whatsapp_text_responses_%d.csv", epoch);
        String outPath = defaultResPath + File.separator + outFilename;

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        Set<String> temp = new HashSet<>(numbers);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outPath))) {
            bw.write("phone_number,response_code,response_body");
            bw.newLine();

            for (String raw : temp) {
                String normalized = normalizeNumber(raw);
                if (normalized == null) {
                    System.out.println("Skipping invalid number: " + raw);
                    continue;
                }

                String json = buildJsonBody(normalized, messageBody);

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
                    writeCsvLine(bw, normalized, Integer.toString(resp.statusCode()), resp.body());
                } catch (IOException | InterruptedException e) {
                    System.err.println("Request failed for " + normalized + ": " + e.getMessage());
                    writeCsvLine(bw, normalized, "ERROR", e.getMessage() == null ? "" : e.getMessage());
                    if (e instanceof InterruptedException) Thread.currentThread().interrupt();
                }
            }
            bw.flush();
            System.out.println("Responses saved to: " + outPath);
        } catch (IOException e) {
            System.err.println("Failed to write responses CSV: " + e.getMessage());
        }
    }

    // Find the latest whatsapp_responses_*.csv file in the resources directory
    private static String findLatestWhatsappResponsesFile(String resDir) {
        File dir = new File(resDir);
        if (!dir.exists() || !dir.isDirectory()) return null;
        File[] files = dir.listFiles((d, name) -> name.startsWith("whatsapp_responses_") && name.endsWith(".csv"));
        if (files == null || files.length == 0) return null;
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files[0].getAbsolutePath();
    }

    // Read the output CSV produced by WhatsappNumberSend and extract the phone_number column
    private static List<String> readNumbersFromResponsesCsv(String path) throws IOException {
        File f = new File(path);
        if (!f.exists() || !f.isFile()) throw new IOException("File not found: " + path);

        List<String> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String headerLine = br.readLine();
            if (headerLine == null) return result;

            List<String> headerFields = parseCsvLine(headerLine);
            int phoneIdx = indexOfHeader(headerFields, "phone_number");
            if (phoneIdx < 0) phoneIdx = 0; // fallback to first column

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> fields = parseCsvLine(line);
                if (phoneIdx < fields.size()) {
                    String raw = fields.get(phoneIdx);
                    if (raw != null) raw = raw.trim();
                    if (raw != null && !raw.isEmpty()) {
                        // remove wrapping quotes if any
                        if (raw.startsWith("\"") && raw.endsWith("\"")) raw = raw.substring(1, raw.length() - 1);
                        result.add(raw);
                    }
                }
            }
        }
        return result;
    }

    private static int indexOfHeader(List<String> headers, String name) {
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i) != null && headers.get(i).trim().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        if (line == null || line.isEmpty()) return fields;

        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        int len = line.length();
        for (int i = 0; i < len; i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < len && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString());
        return fields;
    }

    private static String normalizeNumber(String s) {
        if (s == null) return null;
        String digits = s.replaceAll("\\D+", "");
        if (digits.isEmpty()) return null;
        if (digits.length() < 6) return null;
        return digits;
    }

    private static String buildJsonBody(String toNumber, String body) {
        // simple JSON escaping for body
        String escBody = body;
        return "{" +
                "\"messaging_product\": \"whatsapp\"," +
                "\"recipient_type\": \"individual\"," +
                "\"to\": \"" + toNumber + "\"," +
                "\"type\": \"text\"," +
                "\"text\": { \"preview_url\": true, \"body\": \"" + escBody + "\" }" +
                "}";
    }

    // CSV helper - escapes field and writes a line
    private static void writeCsvLine(BufferedWriter bw, String phone, String code, String body) throws IOException {
        bw.write(escapeCsv(phone));
        bw.write(',');
        bw.write(escapeCsv(code));
        bw.write(',');
        bw.write(escapeCsv(body));
        bw.newLine();
    }

    private static String escapeCsv(String field) {
        if (field == null) return "";
        boolean needsQuotes = field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r");
        String escaped = field.replace("\"", "\"\"");
        return needsQuotes ? ("\"" + escaped + "\"") : escaped;
    }
}
