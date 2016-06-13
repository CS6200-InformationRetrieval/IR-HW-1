import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Collection {
	
	static int docIndex = 0;
	static int vocabulary;
	static int totalTokens;
	//	static HashMap<String, String> docNoTextMap = new HashMap<String,String>();
	static HashMap<Integer,String> docIdNoMap = new HashMap<Integer,String>();
	static HashMap<Integer,ArrayList<Tuple>> docIdTuplesMap = new HashMap<>();
	static HashMap<Integer,String> termIdMap = new HashMap<>();
	static HashMap<Integer,Integer> termDFMap = new HashMap<>();
	static HashMap<Integer,Integer> termTTFMap = new HashMap<>();

	public static void main(String args[]) throws IOException
	{
		String dataFolderName = "C:\\Users\\kaush_000\\Desktop\\IR\\AP89_DATA\\AP_DATA\\ap89_collection";
		//String dataFolderName = "C:\\Users\\kaush_000\\Desktop\\IR\\AP89_DATA\\AP_DATA\\test_collection";
		File dataFolder = new File(dataFolderName);

		if(dataFolder.exists())
		{
			for(File xmlfile : dataFolder.listFiles())
			{
				System.out.println("filename: "+xmlfile.getName());

				XMLParser.parseXMLFile(xmlfile);
			
			}

			//displayTermIdMap();
			//displayTuples();
			System.out.println("No of Doc's found: "+docIndex);
			System.out.println("vocabulary :"+vocabulary);

		}
		else
			System.out.println("Given data folder : "+dataFolderName+" doesn't exist");
		 
	}
}
