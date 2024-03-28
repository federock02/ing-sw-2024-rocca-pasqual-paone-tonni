package it.polimi.ingsw.ingsw2024roccapasqualpaonetonni.model;

import java.util.Arrays;

public class BoardDeck {
    private ResourceCard[] resourceCards;
    private GoldCard[] goldCards;
    private ObjectiveCard[] commonGoals;

    public BoardDeck(){
        this.resourceCards =new ResourceCard[2];
        this.goldCards =new GoldCard[2];
        this.commonGoals =new ObjectiveCard[2];
    }
    public BoardDeck(ResourceCard[] rc,GoldCard[] gc, ObjectiveCard[] oc){
        this.resourceCards= rc;
        this.goldCards= gc;
        this.commonGoals= oc;
    }

    public void setResourceCards(ResourceCard pc, int position){
        resourceCards[position]=pc;
    }
    public void setGoldCards(GoldCard pc, int position){ goldCards[position]=pc;}
    public void setObjectiveCards(ObjectiveCard pc, int position){
        commonGoals[position]=pc;
    }

    public PlayingCard draw(int position,DrawableDeck d){
        PlayingCard temp;

        if(position==1 || position==2){
            temp=resourceCards[position-1];
            resourceCards[position-1]=d.drawFirstResource();
            return temp;
        }
        else {
            temp=goldCards[position-3];
            goldCards[position-1]=d.drawFirstGold();
            return temp;
        }
    }

    public ObjectiveCard[]  getCommonObjective(){return commonGoals;}

    //le 4 carte nella board sono chiamate 1,2,3,4, passando la position il client puo decidere quale carta vuole percare tra le 4, quindi indirettamente nache il suo tipo
}
