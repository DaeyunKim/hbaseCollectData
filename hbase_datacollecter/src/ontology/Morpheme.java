package ontology;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import kr.ac.kaist.swrc.jhannanum.comm.Eojeol;
import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import kr.ac.kaist.swrc.jhannanum.exception.ResultTypeException;
import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.ChartMorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger.HMMTagger;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.UnknownMorphProcessor.UnknownProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.InformalSentenceFilter.InformalSentenceFilter;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor.SentenceSegmentor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.NounExtractor.NounExtractor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.SimplePOSResult22.SimplePOSResult22;

public class Morpheme {
	static Workflow workflow = null;
	
	public Morpheme()
	{
			workflow = new Workflow();
			workflow.clear();
		     
			//API append.
			workflow.appendPlainTextProcessor(new InformalSentenceFilter(), null);
			workflow.appendPlainTextProcessor(new SentenceSegmentor(), null);
			workflow.setMorphAnalyzer(new ChartMorphAnalyzer(), "conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json");
			workflow.appendMorphemeProcessor(new UnknownProcessor(), null);  
			workflow.setPosTagger(new HMMTagger(), "conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json");
			workflow.appendPosProcessor(new NounExtractor(), null);
			workflow.appendPosProcessor(new SimplePOSResult22(), null);
			
			//active
		  try {
			  workflow.activateWorkflow(false);
		  } catch (FileNotFoundException e) {
			  e.printStackTrace();
		  }catch (IOException e) {
			  e.printStackTrace();
		  } catch (Exception e) {
			  e.printStackTrace();
			  }
	}
	
	
	public ArrayList<String> workflowAnalyze(String text) throws ResultTypeException
	{
		if(text != null && text.length() > 0)
		{
			String word="";
			ArrayList<String> wordlist = new ArrayList<String>();;
			workflow.analyze(text);
			
			LinkedList<Sentence> resultList = workflow.getResultOfDocument(new Sentence(0, 0, false));
		
			for (Sentence s : resultList) {
				Eojeol[] eojeolArray = s.getEojeols();
				
				
				for (int i = 0; i < eojeolArray.length; i++) {
					if (eojeolArray[i].length > 0) {
						String[] morphemes = eojeolArray[i].getMorphemes();
						
						
						word="";
						for (int j = 0; j < morphemes.length; j++) {
							word += morphemes[j];
							//System.out.println(morphemes[j]);
						}
							
						wordlist.add(word);
					}				
				}
			}
			
			return wordlist;		
		}
		else
			return null;
	}
}
