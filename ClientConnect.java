import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

class ClientConnect extends Thread
{
    protected Socket clientSocket;
    protected BufferedReader in = null;
    protected PrintWriter out = null;
    protected ObjectOutputStream objectOut;

    protected StockMarket mySMRef;

    protected boolean isRegistered = false;
    protected String[] tokens;

    Calendar cal;
    SimpleDateFormat sdf;

    public ClientConnect(Socket aSocket, StockMarket aSM)
    {
        clientSocket = aSocket;
        mySMRef = aSM;
        start();

        cal = Calendar.getInstance();
        sdf = new SimpleDateFormat("HH:mm:ss");
    }

    public void run()
    {
        System.out.println("New client has connected, new thread started.");
        System.out.println("Client IP is: " + clientSocket.getRemoteSocketAddress() + "\n\n");
            
        try
        {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            objectOut = new ObjectOutputStream(clientSocket.getOutputStream());

                
            String inputText;
                
            while((inputText = in.readLine()) != null)
            {
                System.out.println("Client: " + clientSocket.getLocalSocketAddress() + " : " + inputText);
                    
                if(inputText.equals("HELO"))
                {
                    System.out.println("ACK:" + clientSocket.getLocalSocketAddress() + ":" + clientSocket.getRemoteSocketAddress());
                    out.println("ACK:" + clientSocket.getLocalSocketAddress() + ":" + clientSocket.getRemoteSocketAddress());
                    out.println("");
                }
                else if(inputText.equals("EXIT"))
                {
                    System.out.println("ACK:EXIT:Goodbye!");
                    out.println("ACK:EXIT:Connection Closed.");
                    out.println("");
                    break;
                }
                else if(inputText.equals("REGI"))
                {
                    System.out.println("ACK:REGI:"+clientSocket.getLocalSocketAddress());
                    
                    String ID = "" + clientSocket.getRemoteSocketAddress();
                    tokens = ID.split(":");
                    ID = tokens[1];

                    out.println("REGI:SUCCESS:"+ID);
                    out.println("");
                    isRegistered = mySMRef.registerUser(Integer.parseInt(ID));
                }
                else if(inputText.equals("DISP"))
                {   // Display Stock Market

                    tokens = inputText.split(":");
                    out.println("DEBUG: You entered: DISP");
                    System.out.println("DEBUG: DISP");
                    System.out.println("DEBUG: Tokens is: " + tokens.length + " in size -- Value of [1] is: " + tokens[1]);

                    if(mySMRef.checkID(Integer.parseInt(tokens[1])))
                    {
                        String [][] aStock = mySMRef.getStockMarketState();
                        //objectOut.writeObject(mySMRef.getStockMarketState());
                        for(int i = 0; i < aStock.length; i++)
                        {                            
                            out.println("STK:"+aStock[i][0]+":"+aStock[i][1]+":"+aStock[i][3]);
                        }
                        System.out.println("TIME:" + sdf.format(cal.getTime()) );
                        out.println("TIME:" + sdf.format(cal.getTime()) );
                        out.println("END:EOF");
                        out.println("");
                    }
                    else
                    {
                        out.println("ERR:Not Registered");
                        System.out.println("User not registered.");
                        out.println("");
                    }
                }
                else if(inputText.startsWith("BUY"))
                {
                    tokens = inputText.split(":");
                    
                    if(mySMRef.checkID(Integer.parseInt(tokens[3])))
                    {
                        out.println(mySMRef.buyShares(tokens));
                    }
                    else
                    {
                        out.println("ERR:Not Registered");
                    }
                    out.println("");
                }
                else if(inputText.startsWith("SELL"))
                {
                    tokens = inputText.split(":");

                    if(mySMRef.checkID(Integer.parseInt(tokens[3])))
                    {
                        out.println(mySMRef.sellShares(tokens));

                    }
                    else
                    {
                        out.println("ERR:Not Registered");
                    }
                    out.println("");
                }
                else if(inputText.startsWith("CASH"))
                {
                    tokens = inputText.split(":");

                    if(mySMRef.checkID(Integer.parseInt(tokens[1])) == true)
                    {
                        String tempStr = mySMRef.checkCash(tokens[1]);
                        out.println(tempStr);
                    }
                    else
                    {
                        out.println("ERR:Not Registered");
                    }
                    out.println("");

                }
                else if(inputText.equals("HELP"))
                {
                    out.println("Commands:");
                    out.println("REGI: - Allows authentication with the system.");
                    out.println("BUY: - Allows the purchasing of shares.");
                    out.println("SELL: - Allows the selling of shares.");
                    out.println("EXIT: - Exit the system (lose all shares and funds).");
                    out.println("DISP: - Display current Stock market values.");
                    out.println("CASH: - Display your remaining cash balance (not including shares owned).");
                    out.println("");
                }
                else
                {
                    System.out.println("DEBUG:"+inputText+":");
                }
            }
            out.close();
            in.close();

            clientSocket.close();
            isRegistered = false;
        }
        catch(IOException e)
        {
            System.out.println("Problem with socket: " + e);
        }
    }

}
