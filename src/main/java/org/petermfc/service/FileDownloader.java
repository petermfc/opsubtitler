package org.petermfc.service;

import javafx.beans.property.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.petermfc.exception.DownloadException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

public class FileDownloader {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int DEFAULT_TIMEOUT_MSEC = 5000;
    private final ReadOnlyLongWrapper transferredBytes = new ReadOnlyLongWrapper();
    private final ReadOnlyLongWrapper fileSize = new ReadOnlyLongWrapper();
    private final ReadOnlyBooleanWrapper downloadFinished = new ReadOnlyBooleanWrapper();
    private final ReadOnlyStringWrapper outputFilePath = new ReadOnlyStringWrapper();

    public ReadOnlyLongProperty transferredBytesProperty() {
        return transferredBytes.getReadOnlyProperty();
    }

    public long getTransferredBytes() {
        return transferredBytesProperty().get();
    }

    private ReadOnlyLongProperty fileSizeProperty() {
        return fileSize.getReadOnlyProperty();
    }

    public long getFileSize() {
        return fileSizeProperty().get();
    }

    public String getOutputFilePath() {
        return outputFilePath.get();
    }

    public ReadOnlyBooleanProperty downloadFinishedProperty() {
        return downloadFinished.getReadOnlyProperty();
    }

    public void save(String name, String strUrl) throws DownloadException {
        downloadFinished.set(false);
        outputFilePath.setValue(name);
        Thread downloadTh = new Thread(() -> {
            try {
                downloadToFileAndUnpack(name, strUrl);
            } catch (DownloadException e) {
                throw new RuntimeException(e);
            }
        });
        downloadTh.start();
    }

    private void downloadToFileAndUnpack(String name, String strUrl) throws DownloadException {
        BufferedInputStream bis = null;
        FileOutputStream downloadOutputStream = null;
        GZIPInputStream gzis = null;
        URLConnection conn = null;
        try {
            URL url = new URL(strUrl);
            conn = url.openConnection();
            conn.setConnectTimeout(DEFAULT_TIMEOUT_MSEC);
            long contentLength = conn.getContentLengthLong();
            fileSize.set(contentLength);
            InputStream raw = conn.getInputStream();
            bis = new BufferedInputStream(raw);

            File tempFile = File.createTempFile("subtitle", ".tmp");
            tempFile.deleteOnExit();

            downloadOutputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            int bytesWritten = 0;

            while (bytesWritten < contentLength) {
                bytesRead = bis.read(buffer, 0, DEFAULT_BUFFER_SIZE-1);
                if (bytesRead == -1)
                    break;
                downloadOutputStream.write(buffer, 0, bytesRead);
                bytesWritten += bytesRead;
                transferredBytes.set(bytesWritten);
            }

            if(bytesWritten != contentLength) {
                throw new RuntimeException("Download size does not math the actual file size!");
            }

            downloadOutputStream.flush();
            File out = new File(name);

            if(strUrl.endsWith(".gz") || strUrl.endsWith(".zip")) {
                gzis = new GZIPInputStream(new FileInputStream(tempFile));
                FileUtils.copyToFile(gzis, out);
            } else {
                FileUtils.copyFile(tempFile, out);
            }
            downloadFinished.set(true);
        }
        catch (MalformedURLException mue) {
            throw new DownloadException("Ouch - a MalformedURLException happened.", mue);

        }
        catch (IOException e) {
            throw new DownloadException(e);
        }
        finally {
            IOUtils.closeQuietly(downloadOutputStream);
            IOUtils.closeQuietly(gzis);
            IOUtils.closeQuietly(bis);
            IOUtils.close(conn);
        }
    }
}
