package com.rogue.filemngt.service.impl;

import com.rogue.filemngt.model.Attachment;
import com.rogue.filemngt.repository.AttachmentRepository;
import com.rogue.filemngt.service.IAttachmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class AttachmentServiceImpl implements IAttachmentService {
  private final AttachmentRepository attachmentRepository;

  public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
    this.attachmentRepository = attachmentRepository;
  }

  @Override
  public List<Attachment> saveAttachments(MultipartFile[] files) {
    return Arrays.stream(files)
            .map(file -> {
              try {
                if (file.isEmpty()) {
                  throw new Exception("File is not attached");
                }
                return
                        attachmentRepository.save(
                                Attachment.builder()
                                        .fileName(getFileName(file))
                                        .fileType(file.getContentType())
                                        .data(file.getBytes())
                                        .build()
                        );
              } catch (Exception e) {
                throw new RuntimeException("Could not save file");
              }

            }).toList();
  }

  @Override
  public List<Attachment> getAttachments(List<String> attachmentIds) throws Exception {
    var ids = new HashSet<>(attachmentIds);
    var attachments = attachmentRepository.findAllById(ids);
    if (attachments.isEmpty()) {
      throw new Exception("No such files");
    }
    return attachments;
  }

  @Override
  public void zipFiles(List<Attachment> attachments, HttpServletResponse response) throws Exception {
    if (attachments != null) {
      response.setContentType("application/zip");
      response.setHeader("Content-Disposition", "attachment; filename=download.zip");
      try (var zipOutPutStream = new ZipOutputStream(response.getOutputStream())) {
        attachments
                .forEach(attachment -> {
                  var zipEntry = new ZipEntry(attachment.getFileName());
                  zipEntry.setSize(attachment.getData().length);
                  zipEntry.setTime(System.currentTimeMillis());
                  try {
                    zipOutPutStream.putNextEntry(zipEntry);
                    StreamUtils.copy(attachment.getData(), zipOutPutStream);
                    zipOutPutStream.closeEntry();
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                });
        zipOutPutStream.finish();
      } catch (IOException ex) {
        throw new Exception("Cannot zip files");
      }
    } else
      throw new Exception("No such attachments found");
  }

  private String getFileName(MultipartFile file) throws Exception {
    final String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    if (fileName.contains("..")) {
      throw new Exception("File name contains invalid path sequence");
    }
    return fileName;
  }
}
