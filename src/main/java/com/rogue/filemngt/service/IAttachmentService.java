package com.rogue.filemngt.service;


import com.rogue.filemngt.model.Attachment;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IAttachmentService {
  List<Attachment> saveAttachments(MultipartFile[] file) throws Exception;

  List<Attachment> getAttachments(List<String> attachmentIds) throws Exception;

  void zipFiles(List<Attachment> attachments, HttpServletResponse response) throws Exception;
}
