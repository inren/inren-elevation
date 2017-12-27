/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.inren.frontend.banking;

import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.service.banking.BankDataService;

/**
 * @author Ingo Renner
 *
 */
public class BankDataImportPanel extends Panel {

	@SpringBean
	private BankDataService bankDataService;

	public BankDataImportPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		addFinanceUpload();
		addTransactionUpload();
		super.onInitialize();
	}

	private void addFinanceUpload() {
		// Add one file input field
		FileUploadField fileUploadField = new FileUploadField("financeInput");
		final Form<Void> progressUploadForm = new Form<Void>("financeForm") {
			@Override
			protected void onSubmit() {
				final List<FileUpload> uploads = fileUploadField.getFileUploads();
				if (uploads != null) {
					for (FileUpload upload : uploads) {

						try {
							// Paarse and insert into db
							bankDataService.importFinanceCsv(upload.getBytes());
						} catch (Exception e) {
							throw new IllegalStateException("Unable to read data.", e);
						}
					}
				}
			};
		};
		// set this form to multipart mode (allways needed for uploads!)
		progressUploadForm.setMultiPart(true);
		progressUploadForm.add(fileUploadField);
		progressUploadForm.add(new UploadProgressBar("financeProgress", progressUploadForm, fileUploadField));
		add(progressUploadForm);
	}

	private void addTransactionUpload() {
		// Add one file input field
		FileUploadField fileUploadField = new FileUploadField("transactionInput");
		final Form<Void> progressUploadForm = new Form<Void>("transactionForm") {
			@Override
			protected void onSubmit() {
				final List<FileUpload> uploads = fileUploadField.getFileUploads();
				if (uploads != null) {
					for (FileUpload upload : uploads) {

						try {
							// Paarse and insert into db
							bankDataService.importTransactionCsv(upload.getBytes());
						} catch (Exception e) {
							throw new IllegalStateException("Unable to read data.", e);
						}
					}
				}
			};
		};
		// set this form to multipart mode (allways needed for uploads!)
		progressUploadForm.setMultiPart(true);
		progressUploadForm.add(fileUploadField);
		progressUploadForm.add(new UploadProgressBar("transactionProgress", progressUploadForm, fileUploadField));
		add(progressUploadForm);
	}
}
