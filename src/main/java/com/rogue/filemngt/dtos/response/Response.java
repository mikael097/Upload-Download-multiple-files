package com.rogue.filemngt.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response {

  private String fileName;
  private String downloadURL;
  private String fileType;
  private long fileSize;

}
