package edu.ilstu.biology.flytranscriptionwebapp.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ilstu.biology.flytranscriptionwebapp.model.Gene;
import edu.ilstu.biology.flytranscriptionwebapp.model.GeneXml;
import edu.ilstu.biology.flytranscriptionwebapp.processor.GenomeXmlUnmarshaller;

@Component
public class GenomeDataMapper {

	@Autowired
	private GenomeXmlUnmarshaller genomeXmlUnmarshaller; 
	
	public List<Gene> mapGenomicData() {
		List<Gene> geneList = new ArrayList<Gene>();
		List<GeneXml> geneXmlList = genomeXmlUnmarshaller.unmarshallFile();

		for (GeneXml geneXml : geneXmlList) {
			/*
			 * TODO: Move all this mapping to a new Mapper class
			 */
			Gene gene = new Gene();
			gene.setDbIdentifier(geneXml.getDbIdentifier());
			gene.setSecondaryIdentifier(geneXml.getSecondaryIdentifier());
			gene.setGeneName(geneXml.getGeneName());
			int[] rnaExp = new int[104];
			String[] rnaDataXml = StringUtils.split(geneXml.getRnaData(), ",");
			for (int i = 0; i < rnaDataXml.length; i++) {
				rnaExp[i] = Integer.valueOf(rnaDataXml[i]);
			}
			gene.setRnaExpData(rnaExp);
			geneList.add(gene);
		}
		return geneList;
	}
}
