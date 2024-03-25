package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model;

import it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model.exception.NoSeedException;

import java.util.Arrays;
import java.util.List;

public class ObjectiveCard extends Card{
    private int points;
    private boolean isCount; //se è una carta obiettivo di tipo conteggio isCount=true, se è di tipo pattern isCount=false
    private Seed type;
    private String shape;
    private Seed primaryCard;
    private Seed secondaryCard;

    public ObjectiveCard(int id, int points, boolean isCount, Seed type, Seed[] psCards, String shape){
        super(id);
        this.points = points;
        this.isCount = isCount;
        this.type = type;
        this.primaryCard = psCards[0];
        this.secondaryCard = psCards[1];
        this.shape = shape;
    }

    public int getPoints(){
        return points;
    }
    public boolean getisCount(){
        return isCount;
    }
    public Seed getType(){
        return type;
    }
    public String getShape(){
        return shape;
    }
    public Seed getPrimaryCard(){
        return primaryCard;
    }
    public Seed getSecondaryCard(){
        return secondaryCard;
    }

    public int pointCard(PlayerBoard pb){
        if(isCount){
            return pointsCountCard(pb);
        }
        else {
            return pointsPatternCard(pb);
        }
    }
    private int pointsCountCard(PlayerBoard pb){
        switch (type){
            case RED,BLUE,PURPLE,GREEN ->{
                return points * (pb.getPlayer().getCountSeed()[type.getId()] / 3);
            }
            case POTION,SCROLL,FEATHER ->{
                return points * (pb.getPlayer().getCountSeed()[type.getId()] / 2);
            }
            case MIXED -> {
                int[] seed ={pb.getPlayer().getCountSeed()[Seed.SCROLL.getId()],
                            pb.getPlayer().getCountSeed()[Seed.FEATHER.getId()],
                            pb.getPlayer().getCountSeed()[Seed.POTION.getId()]};
                return points * Arrays.stream(seed).min().orElse(0);
            }

        }
        return 0;
    }
    private int pointsPatternCard(PlayerBoard pb){
        PlayingCard[][] board = pb.getBoard();
        int totalpoints =0;

        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[i].length; j++){
                PlayingCard currentCard = board[i][j];
                if(currentCard!=null && primaryCard == currentCard.getSeed() ){
                    switch (shape){
                        case "up" -> {
                            if( i+2 <board.length && j+2 < board[i].length &&
                                board[i+1][j+1] != null &&
                                board[i+1][j+1].getSeed() == primaryCard &&
                                board[i+2][j+2] != null &&
                                board[i+1][j+1].getSeed() == primaryCard
                                ){
                                totalpoints += points;
                            }
                        }
                        case "down" -> {
                            if( i-2 > 0 && j-2 > 0 &&
                                board[i-1][j-1] != null &&
                                board[i-1][j-1].getSeed() == primaryCard &&
                                board[i-2][j-2] != null &&
                                board[i-1][j-1].getSeed() == primaryCard
                            ){
                                totalpoints += points;
                            }
                        }
                        case "ne" -> {


                        }
                        case "nw" -> {

                        }
                        case "se" -> {

                        }
                        case "sw" -> {

                        }
                    }

                }
            }
        }
        return totalpoints;
    }

}
