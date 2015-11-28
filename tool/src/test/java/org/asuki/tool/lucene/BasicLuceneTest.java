package org.asuki.tool.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static java.lang.System.out;
import static java.util.Arrays.asList;
import static org.apache.lucene.util.Version.LUCENE_46;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BasicLuceneTest {

    private static final Version VERSION = LUCENE_46;
    private static final String QUERY_STRING = "lucene";
    private static final int HITS_PER_PAGE = 10;

    @Test
    public void test() throws Exception {

        StandardAnalyzer analyzer = new StandardAnalyzer(VERSION);

        Directory index = createIndex(analyzer);

        // "title" is the default field
        Query query = new QueryParser(VERSION, "title", analyzer).parse(QUERY_STRING);

        search(index, query);
    }

    private static Directory createIndex(StandardAnalyzer analyzer) throws IOException {
        Directory index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(VERSION, analyzer);

        try (IndexWriter writer = new IndexWriter(index, config)) {
            List<Document> documents = asList(
                    doc("Lucene in Action", "1234A"),
                    doc("Effective java", "3456C"),
                    doc("Basic lucene", "2345B"),
                    doc("The Art of Computer Science", "4567D")
            );

            writer.addDocuments(documents);
        }

        return index;
    }

    private static Document doc(String title, String isbn) {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));

        return doc;
    }

    private static void search(Directory index, Query query) throws IOException {
        try (IndexReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(HITS_PER_PAGE, true);

            searcher.search(query, collector);

            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertThat(hits.length, is(2));

            for (ScoreDoc hit : hits) {
                int docId = hit.doc;
                Document doc = searcher.doc(docId);
                out.println(doc.get("title") + "\t" + doc.get("isbn"));
            }
        }
    }
}
