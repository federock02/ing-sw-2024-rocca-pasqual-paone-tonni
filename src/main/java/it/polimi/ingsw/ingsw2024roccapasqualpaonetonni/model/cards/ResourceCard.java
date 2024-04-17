package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.cards;


//import static org.fusesource.jansi.Ansi.ansi;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.Corner;
import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.Seed;

public class ResourceCard extends PlayingCard {
    public ResourceCard(int id, Seed seed, Corner[] c, int points){
        super(id,seed,c,points);
    }

    public int calculatePoints(PlayingCard[][] board, int[] seedCount, int x, int y) {
        return points;
    }


}

// Resource Card is actually a PlayingCard Without being an abstract class


