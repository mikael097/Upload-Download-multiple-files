package com.rogue.filemngt.controller;

import com.rogue.filemngt.dtos.response.Response;
import com.rogue.filemngt.service.IAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class AttachmentController {

  private final IAttachmentService attachmentService;

  @Autowired
  public AttachmentController(IAttachmentService attachmentService) {
    this.attachmentService = attachmentService;
  }

  @PostMapping("/upload")
  public List<Response> upload(@RequestParam("file") MultipartFile[] files) throws Exception {
    final var attachments = attachmentService.saveAttachments(files);
    return attachments.stream()
            .map(attachment -> Response.builder()
                    .fileName(attachment.getFileName())
                    .fileType(attachment.getFileType())
                    .fileSize(attachment.getData().length)
                    .downloadURL(ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/v1")
                            .path("/download/")
                            .path(attachment.getId())
                            .toUriString())
                    .build())
            .toList();
  }

  @GetMapping(value = "/download")
  public void download(@RequestParam("id") List<String> ids, HttpServletResponse response) throws Exception {
    var attachments = attachmentService.getAttachments(ids);
    attachmentService.zipFiles(attachments, response);
  }
}
