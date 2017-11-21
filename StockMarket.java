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

    public boolean registerUser(int aID)
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

    public String buyShares(String[] aTokens)
    {
        String[] tokens1 = aTokens;
        String tempStr = "";

        double sharePurchase = Double.parseDouble(tokens1[2]);

        int companyIndex = -1;
        int userIndex = -1;

        for(int i = 0; i < stockData.length; i++)
        {
            if(stockData[i][0].equals(tokens1[1]))
            {
                companyIndex = i;
                System.out.println("DEBUG: Company at: " + i);
            }
        }
        if(companyIndex < 0)
        {
            System.out.println("DEBUG:ERR: Company not found");
        }

        for(int i = 0; i < registeredIDs.length; i++)
        {
            if(registeredIDs[i][0] == Double.parseDouble(tokens1[3]))
            {
                userIndex = i;
                System.out.println("DEBUG: User at: " + i);
            }
        }

        if(userIndex < 0)
        {
            System.out.println("DEBUG:ERR: User not found");
        }


        if((companyIndex != -1) && (userIndex != -1))
        {
            sharePurchase = sharePurchase * Double.parseDouble(stockData[companyIndex][1]);
        }

        if(sharePurchase <= registeredIDs[userIndex][1])
        {
            System.out.println("DEBUG here too!");
            registeredIDs[userIndex][1] = registeredIDs[userIndex][1] - sharePurchase;
            tempStr = "ACK:BUY:" + tokens1[2] + ":" + tokens1[1] + ":COST:" + sharePurchase;
        }
        else
        {
            tempStr = "ERR:Insufficient Funds";
        }

        return tempStr;

    }

    public String sellShares(String[] aTokens)
    {
        String[] tokens1 = aTokens;
        String tempStr = "";

        double shareSelling = Double.parseDouble(tokens1[2]);

        int companyIndex = -1;
        int userIndex = -1;

        for(int i = 0; i < stockData.length; i++)
        {
            if(stockData[i][0].equals(tokens1[1]))
            {
                companyIndex = i;
                System.out.println("DEBUG: Company at: " + i);
            }
        }
        if(companyIndex < 0)
        {
            System.out.println("DEBUG:ERR: Company not found");
        }

        for(int i = 0; i < registeredIDs.length; i++)
        {
            if(registeredIDs[i][0] == Double.parseDouble(tokens1[3]))
            {
                userIndex = i;
                System.out.println("DEBUG: User at: " + i);
            }
        }

        if(userIndex < 0)
        {
            System.out.println("DEBUG:ERR: User not found");
        }

        if(userIndex < 0)
        {
            System.out.println("DEBUG:ERR: User not found");
        }

        if((companyIndex != -1) && (userIndex != -1))
        {
            shareSelling = shareSelling * Double.parseDouble(stockData[companyIndex][1]);

            registeredIDs[userIndex][1] = registeredIDs[userIndex][1] + shareSelling;
            tempStr = "ACK:SELL:" + tokens1[2] + ":" + tokens1[1] + ":MADE:" + shareSelling;
        }
        else
        {
            tempStr = "ERR:Company or User not found";
        }

        return tempStr;

    }

    public String checkCash(String aToken)
    {
        int userIndex = -1;
        String tempStr = "";

        for(int i = 0; i < registeredIDs.length; i++)
        {
            if(registeredIDs[i][0] == Double.parseDouble(aToken))
            {
                userIndex = i;
                System.out.println("DEBUG: User at: " + i);
                tempStr = "MSG:BALANCE:"+registeredIDs[i][1];
            }
        }
        return tempStr;
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

    public boolean checkID(int anID)
    {
        int count = 0;
        boolean isRegistered = false;

        while((int)registeredIDs[count][0] != 0 && count < registeredIDs.length)
        {
            if((int)registeredIDs[count][0] == anID)
            {
                isRegistered = true;
            }
            count++;
        }
        return isRegistered;
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
