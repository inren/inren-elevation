package de.inren.service.banking;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class BankDataServiceImplTest {

	@Test
	public void sparkasseDateParsingTest() {
		new BankDataServiceImpl() {
			public void shouldParseDate() {
				Date date = getDate("30.10.17", SPARKASSE_DATE_PATTERN);
				assertEquals("Mon Oct 30 00:00:00 CET 2017", date.toString());
			}
		}.shouldParseDate();
	}

}
