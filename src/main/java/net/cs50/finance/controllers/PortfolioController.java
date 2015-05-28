package net.cs50.finance.controllers;

import net.cs50.finance.models.Stock;
import net.cs50.finance.models.StockHolding;
import net.cs50.finance.models.StockLookupException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import net.cs50.finance.models.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by Chris Bay on 5/17/15.
 */
@Controller
public class PortfolioController extends AbstractFinanceController {

    @RequestMapping(value = "/portfolio")
    public String portfolio(HttpServletRequest request, Model model){

        // get user
        User user = getUserFromSession(request);

        String greeting = user.getUserName() + "'s portfolio";

        // INPUT: get user's portfolio
        Map<String, StockHolding> portfolio = user.getPortfolio();

        // OUTPUT: nested maps to hold current positions
        HashMap<String, HashMap<String, String>> currentPositions = new HashMap<>();

        // iterate over entries in portfolio, building information
        // for the current value, putting it all into the currentPositions
        // map at the end
        for (Map.Entry<String, StockHolding> entry : portfolio.entrySet()) {

            // extract the stock symbol in the current entry
            String symbol = entry.getKey();

            // extract shares owned from portfolio map
            int shares = entry.getValue().getSharesOwned();
            String sharesString = String.valueOf(shares);

            // get current stock info for the current entry key
            Stock stock;
            try {
                stock = Stock.lookupStock(symbol);
            } catch (StockLookupException e) {
                e.printStackTrace();
                return this.displayError("Problem resolving stock symbol", model);
            }

            // extract current price from lookedupStock; cast it as a string
            float price = stock.getPrice();
            String priceString = "$" + String.format("%.2f", price);
            //String priceString = String.valueOf(price);

            // extract name from lookedupStock
            String name = stock.toString();

            // calculate current value; cast it as a string
            float totalValue = shares * price;
            String totalValueString = "$" + String.format("%.2f", totalValue);
            //String totalValueString = String.valueOf(totalValue);

            // new map to hold the relevant values
            HashMap<String, String> position = new HashMap<>();

            // put keys and values into the new map
            position.put("name", name);
            position.put("price", priceString);
            position.put("shares", sharesString);
            position.put("value", totalValueString);

            // save the new map in a map to pass as output later
            currentPositions.put(symbol, position);
        }

        model.addAttribute("positions", currentPositions);
        model.addAttribute("portfolioGreeting", greeting);
        model.addAttribute("title", "Portfolio");
        model.addAttribute("portfolioNavClass", "active");

        return "portfolio";
    }

}
