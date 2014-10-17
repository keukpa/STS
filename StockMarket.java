import java.io.*;
import java.util.*;

public class StockMarket implements Runnable
{

    private static StockMarket ref;
    private String[][] stockData; 
    private String filename = "stocks.csv";
    private String delims = ",";
    private String[] tokens;

    private String[] registeredIDs = new String[20];

    private Random rnd;

    private final long PERIOD = 5000L;
    private long lastTime;
    private long currentTime;

    private StockMarket()
    {
        stockData = new String[10][4];
        populateStockData();
        lastTime = System.currentTimeMillis() - PERIOD;
        rnd = new Random();
    }


    public static StockMarket getStockMarket()
    {
        if(ref == null)
        {
            ref = new StockMarket();
        }
        return ref;
        
    }

    public boolean registerUser(String aID)
    {
        int count = 0;
        while(registeredIDs[count] != null)
        {
            count++;
        }
        if(count < 20)
        {
            registeredIDs[count] = aID;
        }
        return true;
    }

    public String checkSharePrice(String aCompany)
    {
        String temp = "";
        for(int i = 0; i < stockData.length; i++)
        {
            if(stockData[i][0].equals(aCompany))
            {
                temp = stockData[i][1];
                break;
            }
        }
        return temp;
    }

    public boolean checkID(String anID)
    {
        int count = 0;
        while(registeredIDs[count] != null)
        {
            if(registeredIDs[count] == anID)
            {
                return true;
            }
            else
            {
                count++;
            }
        }
        return false;
    }

    private void populateStockData()
    {
        String line;
        int count = 0;

        try
        {
            FileReader fileReader = new FileReader(filename);
            BufferedReader in = new BufferedReader(fileReader);
            
            while((line = in.readLine()) != null)
            {
                tokens = line.split(delims);
                for(int i = 0; i < tokens.length; i++)
                {
                    stockData[count][i] = tokens[i];
                }
                count++;
            }
        }
        catch(Exception e)
        {
            System.out.println("Something went wrong: " + e);
        }

        for(int i = 0; i < 10; i++)
            for(int j = 0; j < 3; j++)
                System.out.println("populate: "+i+":"+j+" with: "+stockData[i][j]);

    }

    public String[][] getStockMarketState()
    {
        return stockData;
    }

    public void run()
    {
        while(true)
        {
            currentTime = System.currentTimeMillis();


            if((currentTime - lastTime) >= PERIOD)
            {
                lastTime = currentTime;

                System.out.println("Stock Market updated each 5s.");
                updateStockPrice();
            }
        }

    }

    private void updateStockPrice()
    {
        double change = 0.0;

        for(int i = 0; i < stockData.length; i++)
        {
            if(rnd.nextBoolean())
            {
                change = rnd.nextInt(11)*rnd.nextDouble();
                double aVal = Double.parseDouble(stockData[i][1]);

                if(rnd.nextBoolean())
                {
                      aVal += change;
                      stockData[i][3] = ""+change;
                }
                else
                {
                      aVal -= change;
                      stockData[i][3] = ""+(change * -1);
                }
                      stockData[i][1] = ""+aVal;

                System.out.println("UPD:"+stockData[i][0]+":"+stockData[i][1]+":"+stockData[i][3]);
            }
        }
    }
}



















