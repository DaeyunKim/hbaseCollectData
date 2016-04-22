package ontology;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

public class CreateOntology {
	
	

	public CreateOntology(String NS, String WORDFILE, String ONTOLOGYFILE) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(WORDFILE));
		String KeyWord;
		String str;
		
		OntModel newM = ModelFactory.createOntologyModel();
		OntClass OntKeyWord = null;
		OntClass Member = null;
		
		while((str = in.readLine()) != null) {		
			if(str.indexOf("#") > -1) {
				KeyWord=str.replaceAll("#", "");
				//System.out.println("keyword : " + KeyWord);
				OntKeyWord = newM.createClass(NS + KeyWord);		
			}
			else{
				//System.out.println(str);
				str=str.replaceAll(" ","");
				if(str != "") {
					Member = newM.createClass(NS + str);		
				
					if(OntKeyWord != null)
						OntKeyWord.addSubClass(Member);
				}
			}
		}

		FileOutputStream ontology_file = new FileOutputStream(ONTOLOGYFILE);
		
		newM.write(ontology_file);
		in.close();
		ontology_file.close();
	}
}
