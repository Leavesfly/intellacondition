package com.leavesfly.iac.train.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.leavesfly.iac.domain.PowerVector;
import com.leavesfly.iac.train.domain.IntellacTrainDataItem;
import com.leavesfly.iac.util.SerializeUtil;

/**
 * 用Lucene内存进行存储
 * 
 * @author yefei.yf
 * 
 */
public class TrainDataSetManagerInLucene implements TrainDataSetManager {

	private static final String DOCUMENT_ID_FILED = "documentId";
	private static final String SENSOR_ID_FILED = "sensorId";
	private static final String POWER_VECTOR_FILED = "powerVector";
	private static final String TEMPERATURE_FILED = "temperature";
	private static final String OUTSIDE_TEMP_FILED = "outsideTemp";

	private static final AtomicLong INCREMENT_ID = new AtomicLong(0L);
	private static final int MAX_SEARCH_RESULT_NUM = 100;

	private static IndexWriter indexWriter;
	private static Directory directory;
	private static volatile IndexSearcher indexSearcher;

	static {
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, analyzer);
		directory = new RAMDirectory();
		try {
			indexWriter = new IndexWriter(directory, indexWriterConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Collection<IntellacTrainDataItem> fetchTrainDataSetBySensorId(String sensorId,
			float outsideTemp) {

		if (indexSearcher == null) {
			synchronized (this) {
				if (indexSearcher == null) {
					try {
						indexSearcher = new IndexSearcher(IndexReader.open(directory));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		TermQuery sensorIdTermQuery = new TermQuery(new Term(SENSOR_ID_FILED, sensorId));
		Query outsideTempQuery = NumericRangeQuery.newFloatRange(OUTSIDE_TEMP_FILED, outsideTemp,
				outsideTemp, true, true);
		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(sensorIdTermQuery, Occur.MUST);
		booleanQuery.add(outsideTempQuery, Occur.MUST);

		try {
			TopDocs hits = indexSearcher.search(booleanQuery, MAX_SEARCH_RESULT_NUM);
			List<IntellacTrainDataItem> searchResult = new ArrayList<IntellacTrainDataItem>(
					hits.scoreDocs.length);
			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = indexSearcher.doc(scoreDoc.doc);

				String docSensorId = doc.get(SENSOR_ID_FILED);
				PowerVector docPowerVector = (PowerVector) SerializeUtil.byteArray2Object(doc
						.getBinaryValue(POWER_VECTOR_FILED));
				NumericField docTemperature = (NumericField) doc.getFieldable(TEMPERATURE_FILED);
				NumericField docOutsideTemp = (NumericField) doc.getFieldable(OUTSIDE_TEMP_FILED);

				IntellacTrainDataItem trainDataItem = new IntellacTrainDataItem(docSensorId,
						docPowerVector, docTemperature.getNumericValue().floatValue(),
						docOutsideTemp.getNumericValue().floatValue());
				searchResult.add(trainDataItem);
			}
			return searchResult;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	@Override
	public void storeTrainDataSet(Collection<IntellacTrainDataItem> tarinDataSet) {
		for (IntellacTrainDataItem trainItem : tarinDataSet) {
			try {
				indexWriter.addDocument(makeDocument(trainItem));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			indexWriter.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Document makeDocument(IntellacTrainDataItem trainDataItem) throws Exception {
		Document doc = new Document();
		doc.add(new NumericField(DOCUMENT_ID_FILED, Field.Store.YES, true)
				.setLongValue(INCREMENT_ID.incrementAndGet()));

		doc.add(new Field(SENSOR_ID_FILED, trainDataItem.getSensorId(), Field.Store.YES,
				Field.Index.NOT_ANALYZED));

		doc.add(new Field(POWER_VECTOR_FILED, SerializeUtil.object2ByteArray(trainDataItem
				.getPowerVector())));

		doc.add(new NumericField(TEMPERATURE_FILED, Field.Store.YES, true)
				.setFloatValue(trainDataItem.getTemperature()));

		doc.add(new NumericField(OUTSIDE_TEMP_FILED, Field.Store.YES, true)
				.setFloatValue(trainDataItem.getOutsideTemp()));
		return doc;
	}

}
