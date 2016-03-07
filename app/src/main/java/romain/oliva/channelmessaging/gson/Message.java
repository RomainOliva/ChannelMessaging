package romain.oliva.channelmessaging.gson;

public class Message {
    public int userID;
    public String username;
    public String message;
    public String date;
    public String imageUrl;

    public void Message() {
    }

    public Message(int userID, String username, String message, String date, String imageUrl)
    {
        this.userID = userID;
        this.username = username;
        this.message = message;
        this.date = date;
        this.imageUrl = imageUrl;
    }
}
