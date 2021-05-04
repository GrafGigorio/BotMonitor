package com.company;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

class API
{
    public Map<String, Map> block = new HashMap<>();
    private Map<String, String> subBlock;
    
    static private final int MAXRECEIVESIZE = 65535;

    static private Socket socket = null;

    private void closeAll() throws Exception
    {
        if (socket != null)
        {
            socket.close();
            socket = null;
        }
    }

    public void display(String result) throws Exception
    {
        String value;
        String name;
        String blockName = null;
        String[] sections = result.split("\\|", 0);

        for (int i = 0; i < sections.length; i++)
        {
            if (sections[i].trim().length() > 0)
            {
                String[] data = sections[i].split(",", 0);

                for (int j = 0; j < data.length; j++)
                {
                    String[] nameval = data[j].split("=", 2);

                    if (j == 0)
                    {
                        if (nameval.length > 1
                                &&  Character.isDigit(nameval[1].charAt(0)))
                            name = nameval[0] + nameval[1];
                        else
                            name = nameval[0];
                        //System.out.println("[" + name + "] =>");
                        //System.out.println("(");
                        
                        block.put(name,null);
                        blockName = name;

                        subBlock = new HashMap<>();
                    }

                    if (nameval.length > 1)
                    {
                        name = nameval[0];
                        value = nameval[1];
                    }
                    else
                    {
                        name = "" + j;
                        value = nameval[0];
                    }
                    subBlock.put(name, value);
                    
                    //System.out.println("   ["+name+"] => "+value);
                }
                //System.out.println(")");
                block.replace(blockName, subBlock);
            }
        }
    }

    public void process(String cmd, InetAddress ip, int port) throws Exception
    {
        StringBuffer sb = new StringBuffer();
        char buf[] = new char[MAXRECEIVESIZE];
        int len = 0;

        //System.out.println("Attempting to send '"+cmd+"' to "+ip.getHostAddress()+":"+port);

        try
        {
            socket = new Socket(ip, port);
            PrintStream ps = new PrintStream(socket.getOutputStream());
            ps.print(cmd.toCharArray());
            ps.flush();

            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            while (0x80085 > 0)
            {
                len = isr.read(buf, 0, MAXRECEIVESIZE);
                if (len < 1)
                    break;
                sb.append(buf, 0, len);
                if (buf[len-1] == '\0')
                    break;
            }

            closeAll();
        }
        catch (IOException ioe)
        {
            System.err.println(ioe.toString());
            closeAll();
            return;
        }

        String result = sb.toString();

        //System.out.println("Answer='"+result+"'");

        display(result);
    }

    public API(String command, String _ip, String _port) throws Exception
    {
        InetAddress ip;
        int port;

        try
        {
            ip = InetAddress.getByName(_ip);
        }
        catch (UnknownHostException uhe)
        {
            System.err.println("Unknown host " + _ip + ": " + uhe);
            return;
        }

        try
        {
            port = Integer.parseInt(_port);
        }
        catch (NumberFormatException nfe)
        {
            System.err.println("Invalid port " + _port + ": " + nfe);
            return;
        }

        process(command, ip, port);
    }
}