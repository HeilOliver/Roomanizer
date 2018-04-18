package at.fhv.roomix.ui.view.contact.scopes;

import at.fhv.roomix.controller.contact.model.ContactPojo;
import at.fhv.roomix.ui.common.AbstractMDScope;
import at.fhv.roomix.ui.dataprovider.ContactProvider;

/**
 * Roomix
 * at.fhv.roomix.ui.view.contact.scopes
 * ContactViewScope
 * 14/04/2018 Oliver
 * <p>
 * Enter Description here
 */
public class ContactViewScope extends AbstractMDScope<ContactPojo> {

    public ContactViewScope() {
        super(ContactPojo::new, new ContactProvider());
    }
}
