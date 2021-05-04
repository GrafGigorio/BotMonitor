/*
*  STATUS=X,When=NNN,Code=N,Msg=string,Description=string|

  STATUS=X Where X is one of:
   W - Warning
   I - Informational
   S - Success
   E - Error
   F - Fatal (code bug)
* */

package com.company;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] params) throws Exception
    {

        String command = "summary";
        String ip = "192.168.52.3";
        String port = "4028";
/*
        if (params.length > 0 && params[0].trim().length() > 0)
            command = params[0].trim();

        if (params.length > 1 && params[1].trim().length() > 0)
            ip = params[1].trim();

        if (params.length > 2 && params[2].trim().length() > 0)
            port = params[2].trim();
*/
        //API asic1 = new API(command, ip, port);
        //printMapResponse(asic1.block);

        //checkAccess(ip,port);
        summaryPrint(ip,port);



    }

    static void summaryPrint(String ip, String port) throws Exception {
        API asic1 = new API("summary", ip, port);
        String t5 = asic1.block.get("SUMMARY").get("MHS 5s").toString();
        String t1m = asic1.block.get("SUMMARY").get("MHS 1m").toString();
        String t5m = asic1.block.get("SUMMARY").get("MHS 5m").toString();
        String t15m = asic1.block.get("SUMMARY").get("MHS 15m").toString();
        String tav = asic1.block.get("SUMMARY").get("MHS av").toString();
        String[] head = {"Worker","Status", "H/R 5s.", "H/R 1m.", "H/R 5m.", "H/R 15m.", "H/R av"};

        String[] wr = {ip, "Alive", t5, t1m, t5m,t15m,tav};
        for (int i = 0; i < head.length; i++) {
            String spase = "";
            if(wr[i].length() > head[i].length())
            {
                for(int k = 0; i < wr[i].length() - head[i].length(); k++)
                {
                    spase += " ";
                }
            }
            System.out.print(head[i] + spase);
        }
        System.out.println();
        for (int i = 0; i < wr.length; i++) {
            System.out.print(wr[i] +" ");
        }



    }

    static void checkAccess(String ip, String port) throws Exception {
        String[] commandList =
                {
                        "summary", "usbstats", "pools", "check", "asccount","lcd", "version", "notify", "devs", "asc", "stats", "pgacount", "config", "edevs", "devdetails", "estats", "coin"
                };
        //cgiminer 1.0.0(bitmain)
        //stats, devs, summary, pools, version
        //cgiminer 4.0.0(inno)
        //summary, usbstats, pools, check, asccount,lcd, version, notify, devs, asc, stats, pgacount, config, edevs, devdetails, estats, coin

        Map<String,Map> avar = new HashMap<>();
        for(String com : commandList)
        {

            API asic1 = new API(com, ip, port);
            System.out.println(com + " : " + asic1.block.get("STATUS").get("STATUS"));
            if(!asic1.block.get("STATUS").get("Msg").toString().contains("Access denied") && !asic1.block.get("STATUS").get("Msg").toString().contains("Invalid command"))//Access denied to 'poolquota' command Msg -> Invalid command
            {
                avar.put(com, asic1.block);
            }
        }
        System.out.println("EE");
    }

    public static void printMapResponse(Map<String, Map> in)
    {
        for(Map.Entry<String, Map> bl : in.entrySet())
        {
            System.out.println(bl.getKey());
            Map<String, String> sub = bl.getValue();

            for (Map.Entry<String, String> sb : sub.entrySet())
            {
                System.out.println("    " + sb.getKey() + " = " +sb.getValue());
            }
        }
    }
}
