package net.cs50.finance.models;

import net.cs50.finance.models.util.PasswordHash;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cbay on 5/10/15.
 */

/**
 * Represents a user on our site
 */

// @entity = tells hibernate/spring that this is a persistent entity to
//   store in a db table
// @table = which table in the db to use
@Entity
@Table(name = "users")
public class User extends AbstractEntity {

    // stored in user table
    private String userName;
    private String hash;

    // this property is stored in a separate table in the db,
    // the stockholding table, connected by foreign keys
    private Map<String, StockHolding> portfolio;

    // TODO - add cash to user class

    public User(String userName, String password) {
        this.hash = PasswordHash.getHash(password);
        this.userName = userName;
        this.portfolio = new HashMap<String, StockHolding>();
    }

    // empty constructor so Spring can do its magic
    public User() {}

    // again, best practice is to annotate getter, not field
    // as here for username and hash
    // @notnull = enforces not null at object level
    // @column.nullable=false = enforces notnull at db level
    // slightly redundant, but good to cover all possibilities
    @NotNull
    @Column(name = "username", unique = true, nullable = false)
    public String getUserName() {
        return userName;
    }

    protected void setUserName(String userName){
        this.userName = userName;
    }

    @NotNull
    @Column(name = "hash", nullable = false)
    public String getHash() {
        return hash;
    }

    protected void setHash(String hash) {
        this.hash = hash;
    }

    // as specified above, stockholding is a map,
    // String stock symbol to Object holding
    // but need to be stored in separate db table
    // @jointocol = specifies the foreign id to link these stockholdings
    //   to users table
    // @onetomany = indicates that each row in users table can have a
    //   relation to many rows in the holdings table
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_id")
    public Map<String, StockHolding> getPortfolio() {
        return portfolio;
    }

    private void setPortfolio(Map<String, StockHolding> portfolio) {
        this.portfolio = portfolio;
    }

    void addHolding (StockHolding holding) throws IllegalArgumentException {

        // Ensure a holding for the symbol doesn't already exist
        if (portfolio.containsKey(holding.getSymbol())) {
            throw new IllegalArgumentException("A holding for symbol " + holding.getSymbol()
                    + " already exits for user " + getUid());
        }

        portfolio.put(holding.getSymbol(), holding);
    }

}