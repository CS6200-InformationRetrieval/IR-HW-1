import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.xml.soap.Text;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class XMLParser {
	static int docCount = 0;
	static int docIndex = 0;
	static int succesful = 0;
	static int failure = 0;
	static int remaining = 0;
//	static HashMap<String, String> docNoTextMap = new HashMap<String,String>();
	static HashMap<Integer,String> docIdNoMap = new HashMap<Integer,String>();
	static HashMap<Integer,Tuple[]> docTupesMap = new HashMap<>();
	
	
	public static BufferedReader parseDoc(BufferedReader br) throws IOException
	{
		String line="";
		
		StringBuilder docText = new StringBuilder();
		
		while(!(line=br.readLine()).equals("</DOC>"))
		{
			if(line.startsWith("<DOCNO>"))
			{
				String docNo = line.split(" ")[1].trim();
				//add docNo to the docIdNoMap if the docNo doesn't already exist in the map
				 if(!docIdNoMap.containsValue(docNo))
				 {
					 docIndex++;
					 docIdNoMap.put(docIndex, docNo);
				 }
				
			}
			
			if(line.equals("<TEXT>"))
			{
				while(!(line = br.readLine()).equals("</TEXT>"))
				{
					docText.append(line+" ");
				}
;			}
			else
				continue;
		}
		
		if(docNoTextMap.containsKey(docNo))
		{
			StringBuilder text = new StringBuilder();
			text.append(docNoTextMap.get(docNo));
			text.append(docText);
			
			docNoTextMap.put(docNo, text.toString());
		}
		else
		{	
			
			docNoTextMap.put(docNo, docText.toString());
		}		
		
		return br;
	}
	
	
	public static void putToIndex(String docNo, String docText,int docIndex) throws IOException
	{
		URL url = new URL("http://localhost:9200/ap_dataset/document/" + docIndex);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
        osw.write(" { \"text\": \"" + docText + "\"" + "," + "\"docno\": \"" + docNo + "\" }");
        osw.flush();
        osw.close();
        connection.disconnect();
        if(connection.getResponseCode() == 201)
        {
        	succesful++;
        }
        else if(connection.getResponseCode() == 400)
        {
        	failure++;
        	//System.out.println("response code: 400");
        }
        else
        {
        	remaining++;
        	//System.out.println("response code: "+ connection.getResponseCode());
        }
	}
	
	public static void parseXMLFile(File xmlfile) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(xmlfile));
		String line = "";
		
		while((line = br.readLine())!=null)
		{
			if(line.equals("<DOC>"))
			{
				docCount++;
				br = parseDoc(br);
			}
			else
			{
				System.out.println("Invalid XML file:"+xmlfile);
				System.out.println("line found:  -------------------- "+ line);
				
			}
				
		}
	
	}
	

	public static void main(String args[]) throws IOException
	{
		//iterate through the given file and parse each file
		
		String dataFolderName = "C:\\Users\\kaush_000\\Desktop\\IR\\AP89_DATA\\AP_DATA\\ap89_collection";
		File dataFolder = new File(dataFolderName);
	
		if(dataFolder.exists())
		{
			for(File xmlfile : dataFolder.listFiles())
			{
				
				parseXMLFile(xmlfile);
			}
			
			int i=0;
			for(String docNo : docNoTextMap.keySet())
			{
				i++;
				putToIndex(docNo, docNoTextMap.get(docNo), i);
			}
			
			System.out.println("No of Doc's found: "+docCount);
			System.out.println("Succesfully created docs: "+ succesful);
			System.out.println("Failed docs: "+ failure);
			System.out.println("remaining docs: "+ remaining);
			
		}
		else
			System.out.println("Given data folder : "+dataFolderName+" doesn't exist");
		//while parsing each file, put the docs in your index 
	}
}
