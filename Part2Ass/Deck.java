import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck implements Serializable{
    private List<Card> cards;
    
    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
        shuffle();
    }
    
    private void initializeDeck() {
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] suits = {"c", "d", "h", "s"};
        
        for (String suit : suits) {
            for (String rank : ranks) {
                Card card = new Card(suit, rank);
                cards.add(card);
            }
        }
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    public Card remove() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }
        return cards.remove(0);
    }
    
    @Override
    public String toString() {
        return cards.toString();
    }

    public void clear() {
        cards.clear();
    }

    public int size() {
        return cards.size();
    }

    public static Deck fromString(String next) {
        return null;
    }

    public void save(ObjectOutputStream oos) throws IOException {
        oos.writeObject(cards);
    }

    @SuppressWarnings("unchecked")
    public void load(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        cards = (List<Card>) ois.readObject();
    }
}
