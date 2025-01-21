package repository;

import java.util.ArrayList;
import java.util.List;

public class ItineraryRepository {
    public List<String> storageList = new ArrayList<>();

    public String store(String input)  {
        storageList.add(input);
        return "added: " + input;
    }

    public String getAll() {
        int counter = 1;
        StringBuilder result = new StringBuilder();
        for(String word : storageList) {
            result.append(counter).append(".").append(word).append("\n");
            counter++;
        }
        return result.toString();
    }
}
