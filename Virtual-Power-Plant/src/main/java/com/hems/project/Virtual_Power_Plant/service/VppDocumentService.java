package com.hems.project.Virtual_Power_Plant.service;

import com.hems.project.Virtual_Power_Plant.dto.DocumentVerificationDto;
import com.hems.project.Virtual_Power_Plant.dto.VppVerificationStatus;
import com.hems.project.Virtual_Power_Plant.entity.Vpp;
import com.hems.project.Virtual_Power_Plant.entity.VppDocument;
import com.hems.project.Virtual_Power_Plant.entity.VppDocumentStatus;
import com.hems.project.Virtual_Power_Plant.entity.VppDocumentType;
import com.hems.project.Virtual_Power_Plant.exception.ResourceNotFoundException;
import com.hems.project.Virtual_Power_Plant.repository.VppDocumentRepo;
import com.hems.project.Virtual_Power_Plant.repository.VppRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VppDocumentService {

    private final VppDocumentRepo vppDocumentRepo;
    private final VppRepository vppRepository;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional
    public Map<String, Object> uploadDocuments(UUID vppId,
                                               List<MultipartFile> files,
                                               VppDocumentType documentType) {


        Vpp vpp = vppRepository.findById(vppId)
                .orElseThrow(() -> new ResourceNotFoundException("VPP not found with id: " + vppId));

        List<VppDocument> oldDocs =
                vppDocumentRepo.findByVpp_IdAndDocumentType(vppId, documentType);

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("no files is provided");
        }

        int threads = Math.min(3, files.size());
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        List<String> urls;
        try {
            List<CompletableFuture<String>> futures = files.stream()
                    .map(file -> CompletableFuture.supplyAsync(() -> {
                        try {
                            log.info("current thread is ={}", Thread.currentThread().getName());
                            return supabaseStorageService.uploadImage(vppId, file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, executor))
                    .toList();

            urls = futures.stream().map(CompletableFuture::join).toList();
        } finally {
            executor.shutdown();
        }

        //old same name na document hase toh ene delete kari daisu
        List<String> oldFileNames = oldDocs.stream()
                .map(VppDocument::getFileName)
                .toList();

        //supabase mathi delete karsu
        supabaseStorageService.deleteFiles(vppId, oldFileNames);

        //database mathi delete karsu
        vppDocumentRepo.deleteByVpp_IdAndDocumentType(vppId, documentType);

        LocalDateTime now = LocalDateTime.now();

        List<VppDocument> newDocs = urls.stream()
                .map(url -> VppDocument.builder()
                        .vpp(vpp)
                        .documentType(documentType)
                        .fileName(extractFileName(url))
                        .url(url)
                        .uploadedAt(now)
                        .status(VppDocumentStatus.UPLOADED)
                        .notes("Uploaded " + documentType + " document")
                        .build())
                .toList();

        vppDocumentRepo.saveAll(newDocs);

        vpp.setVerificationStatus(VppVerificationStatus.PENDING);
        vpp.setSubmittedForVerificationAt(now);
        vppRepository.save(vpp);
        //mail send karsu


        return Map.of(
                "message", "Uploaded successfully",
                "count", urls.size(),
                "imageUrls", urls,
                "verificationStatus", vpp.getVerificationStatus(),
                "submittedAt", vpp.getSubmittedForVerificationAt()
        );
    }

    private String extractFileName(String url) {
        if (url == null) return "file";
        int idx = url.lastIndexOf('/');
        return (idx >= 0 && idx < url.length() - 1) ? url.substring(idx + 1) : "file";
    }


    @Transactional
    public void updateVerificationStatus(UUID vppId,
                                         DocumentVerificationDto dto) {

        Vpp vpp = vppRepository.findById(vppId)
                .orElseThrow(() -> new RuntimeException("VPP not found"));


        List<VppDocument> documents = vppDocumentRepo.findByVpp_Id(vppId);

        for (VppDocument doc : documents) {

            if (dto.getStatus() == VppDocumentStatus.APPROVED) {
                doc.setStatus(VppDocumentStatus.APPROVED);
            } else {
                doc.setStatus(VppDocumentStatus.REJECTED);
            }

            doc.setNotes(dto.getNote());
        }
    }









}
