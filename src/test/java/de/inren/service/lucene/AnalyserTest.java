package de.inren.service.lucene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Before;
import org.junit.Test;

public class AnalyserTest {

	private Analyzer analyzer;

	public static final CharArraySet BANKING_STOP_WORDS_SET;

	static {
		final List<String> stopWords = Arrays.asList("de", "folgenr", "verfalld", "sagt", "danke", "gmbh");
		final CharArraySet stopSet = new CharArraySet(stopWords, false);
		BANKING_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
	}

	@Before
	public void setUp() {
		analyzer = new StandardAnalyzer(BANKING_STOP_WORDS_SET);
	}

	@Test
	public void analyseTest() {
		assertEquals("[mcdonalds]", tokenizeString("00458 MCDONALDS").toString());
		assertEquals("[mcdonalds, ahlen, 7t16]",
				tokenizeString("00458 McDonalds//Ahlen/DE 2016-12-2  7T16:34:54 Folgenr.002 Verfalld.201  9-12")
						.toString());

		assertEquals("[visa, paypal, steam, games]", tokenizeString("VISA PAYPAL *STEAM GAMES").toString());
		assertEquals("[nr6047154019, kaufumsatz, 26.12]",
				tokenizeString("NR6047154019 35314369001 GB    KAUFUMSATZ  26.12  023411").toString());
		// assertEquals("", tokenizeString("").toString());
		assertEquals("[jibi, handel]", tokenizeString("Jibi Handel GmbH + Co. KG").toString());
		assertEquals("[jibi, verbrauchermarkt, i125074, gir, ahlen, 07t14, verfall]", tokenizeString(
				"JIBI Verbrauchermarkt SAGT DANKE. F  I125074 GIR 69175039//AHLEN/DE 2017  -04-07T14:29:18 Folgenr.002 Verfall  d.2019-12")
						.toString());

	}

	private List<String> tokenizeString(String string) {
		List<String> result = new ArrayList<String>();
		try {
			TokenStream stream = analyzer.tokenStream(null, new StringReader(string));
			stream.reset();
			while (stream.incrementToken()) {
				String term = stream.getAttribute(CharTermAttribute.class).toString();
				if (term.length() > 2 && !StringUtils.isNumericSpace(term)) {
					result.add(term);
				}
			}
			stream.close();
		} catch (IOException e) {
			// not thrown b/c we're using a string reader...
			throw new RuntimeException(e);
		}
		return result;
	}
}
