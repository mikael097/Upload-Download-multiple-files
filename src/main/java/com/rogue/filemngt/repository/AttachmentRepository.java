package com.rogue.filemngt.repository;

import com.rogue.filemngt.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
}
