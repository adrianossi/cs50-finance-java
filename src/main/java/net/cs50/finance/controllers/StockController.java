package net.cs50.finance.controllers;

import net.cs50.finance.models.Stock;
import net.cs50.finance.models.StockHolding;
import net.cs50.finance.models.StockLookupException;
import net.cs50.finance.models.User;
import net.cs50.finance.models.dao.StockHoldingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Chris Bay on 5/17/15.
 */
@Controller
public class StockController extends AbstractFinanceController {

    @Autowired
    StockHoldingDao stockHoldingDao;

    @RequestMapping(value = "/quote", method = RequestMethod.GET)
    public String quoteForm(Model model) {

        // pass data to template
        model.addAttribute("title", "Quote");
        model.addAttribute("quoteNavClass", "active");
        return "quote_form";
    }

    @RequestMapping(value = "/quote", method = RequestMethod.POST)
    public String quote(String symbol, Model model) {

        // Lookup quote
        Stock retrievedStock;
        try {
            retrievedStock = Stock.lookupStock(symbol);
        } catch (StockLookupException e) {
            e.printStackTrace();
            return this.displayError("Problem resolving stock symbol", model);
        }

        // pass data to template
        model.addAttribute("stock_desc", retrievedStock.toString());
        model.addAttribute("stock_price", retrievedStock.getPrice());
        model.addAttribute("title", "Quote");
        model.addAttribute("quoteNavClass", "active");

        return "quote_display";
    }

    @RequestMapping(value = "/buy", method = RequestMethod.GET)
    public String buyForm(Model model) {

        model.addAttribute("title", "Buy");
        model.addAttribute("action", "/buy");
        model.addAttribute("buyNavClass", "active");
        return "transaction_form";
    }

    @RequestMapping(value = "/buy", method = RequestMethod.POST)
    public String buy(String symbol, int numberOfShares, HttpServletRequest request, Model model) {

        // validate data
        if (numberOfShares < 1) {
            return this.displayError("Invalid number of shares", model);
        }

        // get user from session
        User user = getUserFromSession(request);

        // format symbol
        symbol = symbol.toUpperCase();

        // make purchase
        StockHolding userPosition;
        try {
            userPosition = StockHolding.buyShares(user, symbol, numberOfShares);
        } catch (StockLookupException e) {
            e.printStackTrace();
            return this.displayError("Problem resolving stock symbol", model);
        }

        // persist updated holding
        stockHoldingDao.save(userPosition);

        // build confirmation message
        String msg = "Purchase of " + numberOfShares;
        msg = (numberOfShares == 1) ? msg + " share of " : msg + " shares of ";
        msg = msg + symbol + " confirmed.";

        model.addAttribute("confirmMessage", msg);
        model.addAttribute("title", "Buy");
        model.addAttribute("action", "/buy");
        model.addAttribute("buyNavClass", "active");

        return "transaction_confirm";

        /* DON'T NEED TO DO ANY OF THIS

            // DONE FOR ME IN STOCKTRANSACTION CONSTRUCTOR (this.price...)
            // get current stock info from Yahoo
            Stock retrievedStock;
            try {
                retrievedStock = Stock.lookupStock(symbol);
            } catch (StockLookupException e) {
                return this.displayError("Problem resolving stock symbol", model);
            }

            // DONE FOR ME IN STOCKHOLDING.buyShares(user, symb, numShares)
            // check if user already holds the stock
            StockHolding heldStock = StockHoldingDao.findBySymbolAndOwnerId(symbol, user.getUid());

            // if new stock for this user, create new StockHolding...
            if (heldStock == null) {
                try {
                    heldStock = StockHolding.buyShares(user, symbol, numberOfShares);
                } catch (StockLookupException e) {
                    this.displayError("Problem resolving stock symbol", model);
                }
            }
            // ...else update user's current holding of this stock
        */
    }

    @RequestMapping(value = "/sell", method = RequestMethod.GET)
    public String sellForm(Model model) {
        model.addAttribute("title", "Sell");
        model.addAttribute("action", "/sell");
        model.addAttribute("sellNavClass", "active");
        return "transaction_form";
    }

    @RequestMapping(value = "/sell", method = RequestMethod.POST)
    public String sell(String symbol, int numberOfShares, HttpServletRequest request, Model model) {

        // validate data
        if (numberOfShares < 1) {
            return this.displayError("Invalid number of shares", model);
        }

        // get user from session
        User user = getUserFromSession(request);

        // format symbol
        symbol = symbol.toUpperCase();

        // make sale
        StockHolding userPosition;
        try {
            userPosition = StockHolding.sellShares(user, symbol, numberOfShares);
        } catch (StockLookupException e) {
            e.printStackTrace();
            return this.displayError("Problem resolving stock symbol", model);
        }

        // persist updated holding
        stockHoldingDao.save(userPosition);

        // build confirmation message
        String msg = "Sale of " + numberOfShares;
        msg = (numberOfShares == 1) ? msg + " share of " : msg + " shares of ";
        msg = msg + symbol + " confirmed.";

        model.addAttribute("confirmMessage", msg);
        model.addAttribute("title", "Sell");
        model.addAttribute("action", "/sell");
        model.addAttribute("sellNavClass", "active");

        return "transaction_confirm";
    }

}
