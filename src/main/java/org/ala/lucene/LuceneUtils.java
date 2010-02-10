package org.ala.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.gbif.ecat.model.ParsedName;
import org.gbif.ecat.parser.NameParser;

public class LuceneUtils {

	/**
	 * Adds a scientific name to the lucene index in multiple forms to increase
	 * chances of matches
	 * 
	 * @param doc
	 * @param scientificName
	 */
	public static void addScientificNameToIndex(Document doc, String scientificName){
		
		NameParser nameParser = new NameParser();
		
		//remove the subgenus
		String normalized = "";
		
		if(scientificName!=null){
			normalized = scientificName.replaceFirst("\\([A-Za-z]{1,}\\) ", "");
		}
		ParsedName parsedName = nameParser.parseIgnoreAuthors(normalized);
    	if(parsedName!=null){
    		if(parsedName.isBinomial()){
    			//add multiple versions
    			doc.add(new Field("scientificName", parsedName.buildAbbreviatedCanonicalName().toLowerCase(), Store.YES, Index.ANALYZED));
    			doc.add(new Field("scientificName", parsedName.buildAbbreviatedFullName().toLowerCase(), Store.YES, Index.ANALYZED));
    		}
    		//add lowercased version
    		doc.add(new Field("scientificName", parsedName.buildCanonicalName().toLowerCase(), Store.YES, Index.ANALYZED));
    	} else {
    		//add lowercased version if name parser failed			    		
	    	doc.add(new Field("scientificName", normalized.toLowerCase(), Store.YES, Index.ANALYZED));
    	}
    	
    	if(scientificName!=null){
    		doc.add(new Field("scientificNameRaw", scientificName, Store.YES, Index.NO));
    	}
	}
}
