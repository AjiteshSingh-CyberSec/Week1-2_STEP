import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class UsernameAvailabilityChecker {

    private final Map<String, String> registeredUsers = new ConcurrentHashMap<>();
    private final Map<String, Integer> attemptFrequency = new ConcurrentHashMap<>();

    private String mostAttemptedUsername = null;
    private int maxAttempts = 0;

    public void registerUser(String username, String userId) {
        registeredUsers.put(username, userId);
    }

    public boolean checkAvailability(String username) {
        attemptFrequency.merge(username, 1, Integer::sum);
        int currentAttempts = attemptFrequency.get(username);

        synchronized (this) {
            if (currentAttempts > maxAttempts) {
                maxAttempts = currentAttempts;
                mostAttemptedUsername = username;
            }
        }

        return !registeredUsers.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int counter = 1;

        while (suggestions.size() < 2) {
            String suggestion = username + counter;
            if (!registeredUsers.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
            counter++;
        }

        if (username.contains("_")) {
            String dotSuggestion = username.replace("_", ".");
            if (!registeredUsers.containsKey(dotSuggestion)) {
                suggestions.add(dotSuggestion);
            }
        } else {
            String dotSuggestion = username + ".com";
            if (!registeredUsers.containsKey(dotSuggestion)) {
                suggestions.add(dotSuggestion);
            }
        }

        return suggestions;
    }

    public String getMostAttempted() {
        if (mostAttemptedUsername == null) {
            return "No attempts recorded";
        }
        return "\"" + mostAttemptedUsername + "\" (" + maxAttempts + " attempts)";
    }
}

public class Week1_2 {
    public static void main(String[] args) {
        UsernameAvailabilityChecker checker = new UsernameAvailabilityChecker();

        checker.registerUser("john_doe", "user_12345");

        boolean isJohnDoeAvailable = checker.checkAvailability("john_doe");
        System.out.println("checkAvailability(\"john_doe\") -> " + isJohnDoeAvailable);

        boolean isJaneSmithAvailable = checker.checkAvailability("jane_smith");
        System.out.println("checkAvailability(\"jane_smith\") -> " + isJaneSmithAvailable);

        System.out.println("suggestAlternatives(\"john_doe\") -> " + checker.suggestAlternatives("john_doe"));

        for (int i = 0; i < 10543; i++) {
            checker.checkAvailability("admin");
        }

        System.out.println("getMostAttempted() -> " + checker.getMostAttempted());
    }
}