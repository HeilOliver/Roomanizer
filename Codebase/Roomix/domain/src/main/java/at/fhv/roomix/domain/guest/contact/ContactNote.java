package at.fhv.roomix.domain.guest.contact;

/**
 * Roomix
 * at.fhv.roomix.persist.domain
 * ContactNote
 * 30/04/2018 Oliver
 * <p>
 * Enter Description here
 */
public class ContactNote {
    private int id;
    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
