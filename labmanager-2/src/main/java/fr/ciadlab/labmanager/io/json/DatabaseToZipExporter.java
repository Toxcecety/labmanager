/*
 * $Id$
 * 
 * Copyright (c) 2019-22, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of the CIAD laboratory and the Université de Technologie
 * de Belfort-Montbéliard ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with the CIAD-UTBM.
 * 
 * http://www.ciad-lab.fr/
 */

package fr.ciadlab.labmanager.io.json;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ciadlab.labmanager.configuration.Constants;
import fr.ciadlab.labmanager.io.filemanager.DownloadableFileManager;
import org.apache.jena.ext.com.google.common.base.Strings;
import org.arakhne.afc.vmutil.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Exporter of ZIP (JSON+files) archive from the database.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 * @see DatabaseToJsonExporter
 */
@Component
public class DatabaseToZipExporter {

	private static final int COPY_BUFFER_SIZE = 4096;

	private DatabaseToJsonExporter jsonExporter;

	private DownloadableFileManager download;

	/** Constructor.
	 *
	 * @param jsonExporter the exporter to JSON file.
	 * @param download the tool for accessing the downloadable files.
	 */
	public DatabaseToZipExporter(
			@Autowired DatabaseToJsonExporter jsonExporter,
			@Autowired DownloadableFileManager download) {
		this.jsonExporter = jsonExporter;
		this.download = download;
	}

	/** Start the exporting process to ZIP.
	 *
	 * @return the tool for finalizing the export to ZIP.
	 * @throws Exception if there is problem for exporting.
	 */
	public ZipExporter startExportFromDatabase() throws Exception {
		final Map<String, Object> content = this.jsonExporter.exportFromDatabase();
		return new ZipExporter(content);
	}

	private static void writeJsonToZip(Map<String, Object> json, ZipOutputStream zos) throws Exception {
		final String filename = Constants.DEFAULT_DBCONTENT_ATTACHMENT_BASENAME + ".json"; //$NON-NLS-1$
		final ZipEntry entry = new ZipEntry(filename);
		zos.putNextEntry(entry);
		final ObjectMapper mapper = new ObjectMapper();
		try (UnclosableStream ucs = new UnclosableStream(zos)) {
			mapper.writer().writeValue(ucs, json);
		}
		zos.closeEntry();
	}

	@SuppressWarnings("unchecked")
	private void writePublicationFilesToZip(Map<String, Object> json, ZipOutputStream zos) throws Exception {
		List<Map<String, Object>>  publications = (List<Map<String, Object>>) json.get(JsonTool.PUBLICATIONS_SECTION);
		if (publications != null && !publications.isEmpty()) {
			for (final Map<String, Object> publication : publications) {
				final String targetFilename0 = (String) publication.get("pathToDownloadableAwardCertificate"); //$NON-NLS-1$
				if (!Strings.isNullOrEmpty(targetFilename0)) {
					if (!copyFileToZip(targetFilename0, zos)) {
						publication.remove("pathToDownloadableAwardCertificate"); //$NON-NLS-1$
					}
				}
				final String targetFilename1 = (String) publication.get("pathToDownloadablePDF"); //$NON-NLS-1$
				if (!Strings.isNullOrEmpty(targetFilename1)) {
					if (!copyFileToZip(targetFilename1, zos)) {
						publication.remove("pathToDownloadablePDF"); //$NON-NLS-1$
					}
				}
			}
		}
	}

	private boolean copyFileToZip(String filename, ZipOutputStream zos) throws Exception {
		final File lfilename = FileSystem.convertStringToFile(filename);
		final File localFile = this.download.normalizeForServerSide(lfilename);
		if (localFile.canRead()) {
			final ZipEntry entry = new ZipEntry(lfilename.toString());
			zos.putNextEntry(entry);
			try (UnclosableStream ucs = new UnclosableStream(zos)) {
				try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localFile))) {
					final byte[] bytesIn = new byte[COPY_BUFFER_SIZE];
					int read = 0;
					while ((read = bis.read(bytesIn)) != -1) {
						ucs.write(bytesIn, 0, read);
					}
				}
			}
			zos.closeEntry();
			return true;
		}
		return false;
	}

	/** Instance of a session of exporting to ZIP.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 2.0.0
	 * @see DatabaseToZipExporter
	 */
	public class ZipExporter {

		private final Map<String, Object> content;

		/** Constructor.
		 *
		 * @param content the content of the JSON file.
		 */
		public ZipExporter(Map<String, Object> content) {
			this.content = content;
		}

		/** Run the exporter.
		 *
		 * @param output the receiver of the ZIP content, usually a stream associated to an HTTP response.
		 * @throws Exception if there is problem for exporting.
		 */
		public void exportToZip(OutputStream output) throws Exception {
			try (ZipOutputStream zos = new ZipOutputStream(output)) {
				writePublicationFilesToZip(this.content, zos);
				writeJsonToZip(this.content, zos);
			}
		}

	}

	/** An output stream that ignore the closing requests.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 2.0.0
	 */
	private static class UnclosableStream extends OutputStream {

		private final OutputStream source;
	
		/** Constructor.
		 * 
		 * @param source the source stream.
		 */
		UnclosableStream(OutputStream source) {
			this.source = source;
		}

		@Override
		public void close() throws IOException {
			//
		}

		@Override
		public void flush() throws IOException {
			this.source.flush();
		}

		@Override
		public void write(int b) throws IOException {
			this.source.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			this.source.write(b);
		}
	
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			this.source.write(b, off, len);
		}
		
	}

}