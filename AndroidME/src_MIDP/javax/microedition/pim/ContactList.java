package javax.microedition.pim;

public interface ContactList extends PIMList {

    public Contact createContact();

    public Contact importContact(Contact contact);

    public void removeContact(Contact contact) throws PIMException;
}
