import java.io.*;
import java.util.*;
import java.text.*;

public class StockMarket implements Runnable
{

    private static StockMarket ref;
    private String[][] stockData;
    private String[][] stockDataDeltas;

    private String filename = "ftse.csv";
    private String delims = ",";
    private String[] tokens;

    private double[][] registeredIDs = new double[2000][2];

    private Random rnd;

    private int shareDaltaCount = 3;

    private final long PERIOD = 15000L;
    private long lastTime;
    private long currentTime;

    private StockMarket()
    {
        stockData = new String[10][4];
        stockDataDeltas = new String[10][2503];
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

        while((int)registeredIDs[count][0] != 0)
        {
            count++;
        }
        if(count < 2000)
        {
            registeredIDs[count][0] = aID;
            registeredIDs[count][1] = 1000000.00;

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
                System.out.println("DEBUG BUY");
            }
            else
            {
                System.out.println("ERR: Company not found @["+i+"]");
                temp = "ERR: Company not found.";
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
                    stockDataDeltas[count][i] = tokens[i];
                }
                count++;
            }
        }
        catch(Exception e)
        {
            System.out.println("Something went wrong: " + e);
        }

        for(int i = 0; i < 10; i++)
        {
            stockData[i][0] = stockDataDeltas[i][0];
            stockData[i][1] = stockDataDeltas[i][1];
            stockData[i][2] = stockDataDeltas[i][2];
            stockData[i][3] = stockDataDeltas[i][3];
        }

        for(int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                System.out.println("populate: " + i + ":" + j + " with: " + stockData[i][j]);
            }
        }

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

                System.out.println("Stock Market updated each 15s.");
                updateStockPrice();
            }
        }

    }

    private void updateStockPrice()
    {
        NumberFormat formatter = new DecimalFormat("#0.00");

        if(shareDaltaCount < 2503)
        {
            shareDaltaCount++;
        }

        for(int i = 0; i < stockData.length; i++)
        {
            double deltaChange = Double.parseDouble(stockDataDeltas[i][shareDaltaCount]);
            stockData[i][3] = "" + deltaChange;

            double newPrice = Double.parseDouble(stockData[i][1]) + deltaChange;
            stockData[i][1] = "" + newPrice;


            System.out.format("UPD:%s:%.2f:%.2f \n", stockData[i][0], Double.parseDouble(stockData[i][1]), Double.parseDouble(stockData[i][3]));
        }

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println("TIME:" + sdf.format(cal.getTime()));
    }
}
