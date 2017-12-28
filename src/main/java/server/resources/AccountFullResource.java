package server.resources;

import server.model.Account;

public class AccountFullResource extends AccountCommonResource{
    private String background;

    public AccountFullResource(Account account) {
        super(account);
        this.background = account.getBackground();
    }

    public String getBackground() {
        return background;
    }
}
