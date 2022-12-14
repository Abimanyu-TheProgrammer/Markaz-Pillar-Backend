package com.markaz.pillar.tools.file.impl;

import com.markaz.pillar.tools.file.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.List;

@Service("rollingImageStorage")
public class RollingImageStorage extends FileStorageService {
    @Value("#{'${service.storage.image.allowed-content-types}'.split(',')}")
    private List<String> contentTypes;

    @Value("${service.storage.image.max-file-size}")
    private String maxFileSize;

    @Override
    public String getDirectory() {
        Calendar systemDate = Calendar.getInstance();
        return String.format("%s/%d/%d", getDir(), systemDate.get(Calendar.YEAR), systemDate.get(Calendar.MONTH)+1);
    }

    @Override
    public String getRootDirectory() {
        return getRootDir();
    }

    @Override
    public String resolveAbsoluteURL(Path relativeDir, String filename) {
        return String.format("%s%s/%s", getRootUrl(), relativeDir.toString(), filename);
    }

    @Override
    protected List<String> getAllowedContentType() {
        return contentTypes;
    }

    @Override
    protected String getAllowedFileSize() {
        return maxFileSize;
    }
}
