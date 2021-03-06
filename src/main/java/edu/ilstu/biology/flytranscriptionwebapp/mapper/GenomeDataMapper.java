package edu.ilstu.biology.flytranscriptionwebapp.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.ilstu.biology.flytranscriptionwebapp.model.Gene;
import edu.ilstu.biology.flytranscriptionwebapp.model.GeneIdentifierXml;
import edu.ilstu.biology.flytranscriptionwebapp.model.GeneRnaDataXml;
import edu.ilstu.biology.flytranscriptionwebapp.processor.GenomeXmlUnmarshaller;

@Component
public class GenomeDataMapper {

	@Autowired
	private GenomeXmlUnmarshaller genomeXmlUnmarshaller;

	public List<Gene> mapGenomicData() {
		List<Gene> geneList = new ArrayList<Gene>();
		List<GeneRnaDataXml> geneRnaDataXmlList = genomeXmlUnmarshaller.unmarshallGenomeRnaDataFile();
		List<GeneIdentifierXml> geneIdentifierXmlList = genomeXmlUnmarshaller.unmarshallGenomeIdentifiersFile();

		/*
		 * Add all genes thru the identifier list
		 */
		for (GeneIdentifierXml geneIdentifierXml : geneIdentifierXmlList) {
			Gene gene = new Gene();
			gene.setDbIdentifier(geneIdentifierXml.getDbIdentifier());
			gene.setSecondaryIdentifier(geneIdentifierXml.getSecondaryIdentifier());
			gene.setGeneName(geneIdentifierXml.getGeneName());
			gene.setSynonyms(Arrays.asList(StringUtils.split(geneIdentifierXml.getSynonyms(), "\t")));
			geneList.add(gene);
		}

		/*
		 * TODO: This is an O(sigma[1 to N]) operation, in the future, improve
		 * this performance -Perhaps only iterate over the unchecked list (no
		 * need to iterate through genes w/ rna data already) [Complete, changed
		 * to O(n) (unverified)]
		 * 
		 * Iterate through geneRnaDataXmlList, search for gene in geneList thru
		 * the dbIdentifier, then insert rnaData
		 */
		ListIterator<Gene> geneIterator = geneList.listIterator();
		int count = 1;
		// int errorCount = 1;
		while (geneIterator.hasNext()) {
			Gene targetGene = geneIterator.next();
			GeneRnaDataXml geneRnaDataXml = null;
			ListIterator<GeneRnaDataXml> geneRnaDataIterator = geneRnaDataXmlList.listIterator();
			while (geneRnaDataIterator.hasNext()) {
				GeneRnaDataXml indexGeneRnaData = geneRnaDataIterator.next();
				if (StringUtils.equals(targetGene.getDbIdentifier(), indexGeneRnaData.getDbIdentifier())) {
					geneRnaDataXml = indexGeneRnaData;
					geneRnaDataIterator.remove();
					break;
				}
			}
			if (geneRnaDataXml != null) {
				int[] rnaExp = new int[104];
				String[] rnaDataXml = StringUtils.split(geneRnaDataXml.getRnaData(), ",");
				for (int i = 0; i < rnaDataXml.length; i++) {
					rnaExp[i] = Integer.valueOf(rnaDataXml[i]);
				}
				targetGene.setRnaExpData(rnaExp);
				geneIterator.set(targetGene);

			} else {
				// TODO: exception handling
				// System.err.println("GENE NOT FOUND: " + count + ";
				// errorCount: " + errorCount);
				// errorCount++;
			}

			// TODO: Change to use Logger SLF4J
			if (count % 1000 == 0) {
				System.out.println("GENE: " + count);
			}
			count++;
		}
		return geneList;
	}
}
