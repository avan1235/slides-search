package `in`.procyk.slides.search

import `in`.procyk.slides.model.Slide
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.Tokenizer
import org.apache.lucene.analysis.core.LowerCaseFilter
import org.apache.lucene.analysis.en.EnglishPossessiveFilter
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter
import org.apache.lucene.analysis.miscellaneous.LengthFilter
import org.apache.lucene.analysis.miscellaneous.TrimFilter
import org.apache.lucene.analysis.pattern.PatternReplaceFilter
import org.apache.lucene.analysis.standard.StandardTokenizer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StoredField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.ByteBuffersDirectory
import java.util.regex.Pattern

class LuceneSearchEngine : SlideSearchEngine {

    // Pipeline: tokenize → strip possessives → strip punctuation → trim → lowercase → fold diacritics → drop short tokens
    private val analyzer: Analyzer = object : Analyzer() {
        override fun createComponents(fieldName: String): TokenStreamComponents {
            val tokenizer: Tokenizer = StandardTokenizer()
            val stream = tokenizer
                // EnglishPossessiveFilter strips trailing 's (e.g. "Kotlin's" → "Kotlin") before punctuation removal
                .let { EnglishPossessiveFilter(it) }
                .let { PatternReplaceFilter(it, Pattern.compile("[^\\p{L}\\p{N}\\s]"), "", true) }
                // TrimFilter cleans up any leading/trailing whitespace left on tokens by PatternReplaceFilter
                .let { TrimFilter(it) }
                .let { LowerCaseFilter(it) }
                // ASCIIFoldingFilter maps all Polish diacritics: ą→a, ę→e, ó→o, ś→s, ź/ż→z, ć→c, ń→n, ł→l
                .let { ASCIIFoldingFilter(it) }
                // LengthFilter drops single-character tokens that produce excessive fuzzy-match noise
                .let { LengthFilter(it, 2, Int.MAX_VALUE) }
            return TokenStreamComponents(tokenizer, stream)
        }
    }

    override fun search(query: String, slides: List<Slide>): List<Int> {
        if (query.isBlank()) return emptyList()

        val directory = ByteBuffersDirectory()
        val config = IndexWriterConfig(analyzer)
        IndexWriter(directory, config).use { writer ->
            slides.forEachIndexed { index, slide ->
                val doc = Document().apply {
                    add(StoredField("index", index))
                    if (slide.title != null) add(TextField("title", slide.title, Field.Store.NO))
                    add(TextField("content", slide.content, Field.Store.NO))
                }
                writer.addDocument(doc)
            }
        }

        val reader = DirectoryReader.open(directory)
        val searcher = IndexSearcher(reader)

        return try {
            val parser = MultiFieldQueryParser(
                arrayOf("title", "content"),
                analyzer,
            )
            // Append ~ to each term for fuzzy matching (handles typos and approximate matches)
            val fuzzyQuery = query.trim()
                .split("\\s+".toRegex())
                .joinToString(" ") { term -> "$term~" }

            val luceneQuery = parser.parse(fuzzyQuery)
            val topDocs = searcher.search(luceneQuery, slides.size)

            topDocs.scoreDocs.map { scoreDoc ->
                val doc = searcher.storedFields().document(scoreDoc.doc)
                doc.getField("index").numericValue().toInt()
            }
        } catch (_: Exception) {
            NaiveSearchEngine.search(query, slides)
        } finally {
            reader.close()
        }
    }
}
