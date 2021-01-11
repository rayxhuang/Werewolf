package com.example.werewolf;

import android.os.Parcel;
import android.os.Parcelable;

public class Character implements Parcelable {
    private int ID;
    private String assignedCharacter;
    private String party;
    private boolean alive;
    private int guardedPlayerID = 0;
    private int savedPlayerID = 0;
    private int poisonedPlayerID = 0;
    private boolean holdingAntidote = false;
    private boolean holdingPoison = false;
    private boolean hasBeenPoisoned = false;
    private boolean hasBeenVoted = false;

    public Character() {}

    public Character(String assignedCharacter, String party, boolean alive) {
        this.assignedCharacter = assignedCharacter;
        this.party = party;
        this.alive = alive;
    }

    public Character(int id, String assignedCharacter, String party, boolean alive) {
        this.ID = id;
        this.assignedCharacter = assignedCharacter;
        this.party = party;
        this.alive = alive;
    }

    protected Character(Parcel in) {
        ID = in.readInt();
        assignedCharacter = in.readString();
        party = in.readString();
        alive = in.readByte() != 0;
        guardedPlayerID = in.readInt();
        savedPlayerID = in.readInt();
        poisonedPlayerID = in.readInt();
        holdingAntidote = in.readByte() != 0;
        holdingPoison = in.readByte() != 0;
        hasBeenPoisoned = in.readByte() != 0;
        hasBeenVoted = in.readByte() != 0;
    }

    public static final Creator<Character> CREATOR = new Creator<Character>() {
        @Override
        public Character createFromParcel(Parcel in) {
            return new Character(in);
        }

        @Override
        public Character[] newArray(int size) {
            return new Character[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(assignedCharacter);
        dest.writeString(party);
        dest.writeByte((byte) (alive ? 1 : 0));
        dest.writeInt(guardedPlayerID);
        dest.writeInt(savedPlayerID);
        dest.writeInt(poisonedPlayerID);
        dest.writeByte((byte) (holdingAntidote ? 1 : 0));
        dest.writeByte((byte) (holdingPoison ? 1 : 0));
        dest.writeByte((byte) (hasBeenPoisoned ? 1 : 0));
        dest.writeByte((byte) (hasBeenVoted ? 1 : 0));
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public void setAssignedCharacter(String assignedCharacter) { this.assignedCharacter = assignedCharacter; }
    public void setParty(String s) { this.party = s; }
    public void setAlive(boolean t) {
        this.alive = t;
    }
    public void setGuardedPlayerID(int guardedPlayerID) { this.guardedPlayerID = guardedPlayerID; }
    public void setSavedPlayerID(int i) {
        savedPlayerID = i;
    }
    public void setPoisonedPlayerID(int i) {
        poisonedPlayerID = i;
    }
    public void setHoldingAntidote(boolean t) {
        holdingAntidote = t;
    }
    public void setHoldingPoison(boolean t) {
        holdingPoison = t;
    }
    public void setHasBeenPoisoned(boolean hasBeenPoisoned) { this.hasBeenPoisoned = hasBeenPoisoned; }
    public void setHasBeenVoted(boolean hasBeenVoted) { this.hasBeenVoted = hasBeenVoted; }

    public int getID() {
        return ID;
    }
    public String getAssignedCharacter() {
        return assignedCharacter;
    }
    public String getParty() { return party; }
    public boolean isAlive() {
        return this.alive;
    }
    public int getGuardedPlayerID() { return guardedPlayerID; }
    public int getSavedPlayerID() {
        return savedPlayerID;
    }
    public int getPoisonedPlayerID() {
        return poisonedPlayerID;
    }
    public boolean isHoldingAntidote() {
        return holdingAntidote;
    }
    public boolean isHoldingPoison() {
        return holdingPoison;
    }
    public boolean hasBeenPoisoned() { return hasBeenPoisoned; }
    public boolean hasBeenVoted() { return hasBeenVoted; }
}
