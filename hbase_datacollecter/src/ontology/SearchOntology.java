package ontology;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;

public class SearchOntology {
	private OntModel newM = null;
	private String NS = null;
	private OntClass ont = null;
	
	public SearchOntology(String NS, String ONTOLOGYFILE) throws IOException {
		newM = ModelFactory.createOntologyModel();
		BufferedReader in = new BufferedReader(new FileReader(ONTOLOGYFILE));
		newM.read(in,"");
		in.close();
		
		this.NS = NS;
	}
	
	//결과가 없으면 null return.
	public String SearchKeyWord(String word)
	{
		if(newM == null) return null;
		
		word = word.toUpperCase();
		ont = newM.getOntClass(NS + word);
		
		if(ont == null)	return null;
		
		return ont.getSuperClass().getLocalName();
	}
	
	
	//결과가 없으면 null return.
	public ArrayList<String> SearchMember(String keyword)
	{
		if(newM == null) return null;
		
		ArrayList<String> list = new ArrayList<String>();
		
		ont = newM.getOntClass(NS + keyword);
		
		if(ont == null)	return null;

		for(Iterator<OntClass> i = ont.listSubClasses() ; i.hasNext() ; )
		{
			OntClass c = (OntClass) i.next();
			System.out.println(c.getLocalName());
			list.add(c.getLocalName());
		}
		
		return list;
	}
	
}


