package com.yildizan.newsfrom.locator.dto.discord;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ErrorFileDto implements MultipartFile {

    private final byte[] bytes;
    private final String name;
    private final String fileName;

    public ErrorFileDto(byte[] bytes, String name, String fileName) {
        this.bytes = bytes;
        this.name = name;
        this.fileName = fileName;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return bytes == null || bytes.length == 0;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(bytes);
    }
    
}
